package com.yeling.user.service.Impl;

import com.wf.captcha.SpecCaptcha;
import com.yeling.common.service.RedisService;
import com.yeling.user.domian.entity.User;
import com.yeling.exception.*;
import com.yeling.user.service.EmailService;
import com.yeling.user.vo.request.ResetPasswordReq;
import com.yeling.user.vo.response.LoginResp;
import com.yeling.user.vo.response.UserInfoResp;
import com.yeling.user.mapper.UserMapper;
import com.yeling.user.service.MenuService;
import com.yeling.user.service.UserService;
import com.yeling.user.domian.entity.VerifyCodeEntity;
import com.yeling.user.vo.request.UserInfoReq;
import com.yeling.user.vo.request.UserLogin;
import com.yeling.utils.JwtUtils;
import com.yeling.utils.LoginRateLimiter;
import com.yeling.utils.VerCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class UserServiceImpl implements UserService {


    /**
     * redis缓存
     */
    @Autowired // 自动注入 RedisTemplate
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private EmailService emailService;

    private static final String LOGIN_ATTEMPTS_KEY_PREFIX = "login_attempts:";
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MINUTES = 10;
    private static final long BLOCK_DURATION_SECONDS = TimeUnit.MINUTES.toSeconds(BLOCK_DURATION_MINUTES);



    @Override
    public LoginResp login(UserLogin userLogin, String ip) {
        // 检查登录尝试次数
        if (!allowLoginAttempt(ip)) {
            throw new TooManyAttemptsException();
        }

        // 校验验证码
        validateCaptcha(userLogin.getVerifyCodeKey(), userLogin.getVerifyCode());

        // 验证用户凭据
        User user = authenticateUser(userLogin);

        // 生成令牌并返回响应
        return generateLoginResponse(user);
    }

    private void validateCaptcha(String key, String actual) {

        // 检查验证码是否过期
        if (redisTemplate.getExpire(key, TimeUnit.SECONDS) < 0) {
            throw new CaptchaExpiredException();
        }

        // 获取并删除缓存中的验证码
        String expect = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);

        if (!StringUtils.hasText(expect) || !StringUtils.hasText(actual) || !actual.equalsIgnoreCase(expect)) {
            throw new CaptchaMismatchException();
        }
    }

    private User authenticateUser(UserLogin userLogin) {
        // 获取盐值
        String salt = userMapper.getSalt(userLogin.getEmail());
        if (salt == null || salt.isEmpty()) {
            throw new UserNotFoundException();
        }

        // 加密密码
        String encryptedPassword = sha256(salt + userLogin.getPassword());

        // 验证用户凭据
        User user = userMapper.getByUsernameAndPassword(userLogin.getEmail(), encryptedPassword);
        if (user == null) {
            throw new InvalidCredentialsException("用户名或密码错误");
        }

        return user;
    }

    private LoginResp generateLoginResponse(User user) {
        UserInfoResp userInfo = userMapper.getInfo(user.getId());

        // 创建响应对象
        LoginResp loginResp = new LoginResp();
        loginResp.setAccessToken(getToken(user.getId(), userInfo.getEmail(),
                userInfo.getNickname(), userInfo.getUserAvatar(), false));
        loginResp.setRefreshToken(getToken(user.getId(), userInfo.getEmail(),
                userInfo.getNickname(), userInfo.getUserAvatar(), true));

        // 存储角色信息到Redis
        String redisRoleKey = "user_role:" + user.getId();
        redisTemplate.opsForValue().set(redisRoleKey, user.getRole());
        redisTemplate.expire(redisRoleKey, 12, TimeUnit.MINUTES);

        // 超级管理员单点登录处理
        if ("admin".equals(user.getRole())) {
            String adminTokenKey = "admin_token:" + user.getId();
            String actualToken = loginResp.getAccessToken();
            redisTemplate.opsForValue().set(adminTokenKey, actualToken);
            redisTemplate.expire(adminTokenKey, 12, TimeUnit.MINUTES);
        }

        return loginResp;
    }


    @Override
    public LoginResp adminLogin(UserLogin userLogin, String ip){

        if (!allowLoginAttempt(ip)) {
            throw new TooManyAttemptsException();
        }

        validateCaptcha(userLogin.getVerifyCodeKey(), userLogin.getVerifyCode());

        User user = authenticateUser(userLogin);

        setAdminMenu(user.getId());

        return generateLoginResponse(user);

    }

    private void setAdminMenu(Integer id){
        // 新增：查询用户可访问的菜单接口路径列表（从数据库获取）
        List<String> allowedPaths = menuService.getUserAllowedPaths(id);

        // 将菜单权限存入Redis（使用Set存储）
        String menuPermissionKey = "user:menu:" + id;
        redisTemplate.opsForSet().add(menuPermissionKey, allowedPaths.toArray(new String[0]));
        redisTemplate.expire(menuPermissionKey, 12, TimeUnit.HOURS);
    }

    private String getToken(Integer id, String email, String nickname, String userAvatar, boolean isLong){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("email", email);
        claims.put("nickname", nickname);
        claims.put("userAvatar", userAvatar);
        log.info(claims.toString());
        if(isLong){
            return JwtUtils.generateLongJwt(claims);
        }else{
            return JwtUtils.generateShortJwt(claims);
        }
    }

    private String sha256(String input) {
        try {
            //使用SHA-256加密
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }



    public boolean allowLoginAttempt(String ip) {
        String key = LOGIN_ATTEMPTS_KEY_PREFIX + ip;

        // 获取当前尝试次数
        Integer attempts = (Integer) redisService.get(key);

        // 如果已超过最大尝试次数，拒绝登录
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            return false;
        }

        // 增加尝试次数
        long newAttempts = redisService.increment(key, 1);

        // 如果是第一次尝试，设置过期时间
        if (newAttempts == 1) {
            redisService.expire(key, BLOCK_DURATION_SECONDS, TimeUnit.SECONDS);
        }

        return true;
    }

    @Transactional
    @Override
    public Integer reg(UserLogin userLogin){


        // 校验验证码
        validateCaptcha(userLogin.getEmail(), userLogin.getVerifyCode());


        Integer integer = userMapper.getCountByName(userLogin.getEmail());
        if(integer == 0){
            //随机生成盐
            String randomHexString = generateRandomHexString(16);
            userMapper.addSalt(userLogin.getEmail(), randomHexString);

            //哈希加盐加密
            userLogin.setPassword(sha256(randomHexString + userLogin.getPassword()));

            //生成随机用户名
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
            String date = now.format(formatter);

            Random random = new Random();
            int randomNumber = random.nextInt(100); // 生成4位随机数

            String orderNumber; // 保证随机数是4位数字
            orderNumber = "用户_" + date + String.format("%02d", randomNumber);


            userMapper.addUser(userLogin);
            Integer id = userMapper.getUserId(userLogin.getEmail());
            userMapper.addUserInfo(id, userLogin.getEmail(), orderNumber, "/image/avatar/avatar0.png");

            return 3;
        }
        return 0;

    }

    public String generateRandomHexString(int length) {
        StringBuilder hexString = new StringBuilder();
        Random rand = new Random();
        while (hexString.length() < length) {
            // 生成0-15之间的随机整数
            int number = rand.nextInt(16);
            if (number < 10) {
                // 如果是0-9之间的数字，直接转换为字符并添加到字符串中
                hexString.append(number);
            } else {
                // 如果是10-15之间的数字，转换为对应的a-f字符并添加到字符串中
                hexString.append((char) (number - 10 + 'a'));
            }
        }
        return hexString.toString();
    }


    public VerifyCodeEntity generateVerifyCode() {
        // 创建验证码对象
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);

        // 生成验证码编号
        String verifyCodeKey = UUID.randomUUID().toString();
        String verifyCode = specCaptcha.text().toLowerCase();

        // 获取验证码图片，构造响应结果
        VerifyCodeEntity verifyCodeEntity = new VerifyCodeEntity(verifyCodeKey, specCaptcha.toBase64(), verifyCode);

        log.info("验证码key:"+verifyCodeKey);
        // 存入Redis，设置120s过期
        redisTemplate.opsForValue().set(verifyCodeKey, verifyCode, 120, TimeUnit.SECONDS);

        return verifyCodeEntity;
    }

    @Override
    public UserInfoResp getInfo(int id){
        return userMapper.getInfo(id);
    }

    @Override
    public UserInfoResp getInfoById(Integer id){
        return userMapper.getInfo(id);
    }

    @Override
    public Integer updateUserInfo(UserInfoReq userInfoReq, String jwt) {

        Integer id = JwtUtils.getUserId(jwt);

        userInfoReq.setId(id);

        //获取昵称相同直接修改
        String user_ni_cheng = userMapper.getNiChengByUid(userInfoReq.getId());

        if(Objects.equals(user_ni_cheng, userInfoReq.getNickname())){

            String[] avatarData = {
                    "/image/avatar/avatar0.jpg",
                    "/image/avatar/avatar1.jpg",
                    "/image/avatar/avatar2.jpg",
                    "/image/avatar/avatar3.jpg",
                    "/image/avatar/avatar4.jpg",
                    "/image/avatar/avatar5.jpg"};

            String avatarStr = userInfoReq.getUserAvatar(); // 这是前端上传的路径字符串

            // 直接在数组中查找是否存在与avatarStr相同的元素
            for (String path : avatarData) {
                if (path.equals(avatarStr)) {

                    // 更新用户信息
                    userMapper.updateUserAvatar(id, userInfoReq.getUserAvatar());
                    return 1; // 更新成功
                }
            }
            // 如果没有找到匹配项，则输出错误信息并返回-1
            System.out.println("无效的头像路径：" + avatarStr);
            return -1;

        }

        //昵称不同判断是否重名
        Integer nameId = userMapper.showIsUserNiCheng(userInfoReq.getNickname());
        if (nameId != null && nameId > 0) {
            return -2; // 昵称已存在
        }

        // 获取更新时间
        LocalDateTime updateTime = userMapper.getUpdateTimeByUid(id);
        if (updateTime == null) {
            // 如果获取不到更新时间，可以根据业务需要处理，这里假设直接返回-3
            return updateUserInfoData(userInfoReq);
        }

        // 计算当前时间与更新时间之间的差值
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(updateTime, now);

        // 判断是否超过30天
        if (daysBetween > 30) {
            // ... (之前的代码保持不变，从头像路径检查开始)
            // 如果找到匹配项，则更新用户信息
            // ... (更新用户信息的代码)
            return updateUserInfoData(userInfoReq);

        } else {
            // 如果未超过180天，则返回-3
            return -3; // 更新时间未超过180天，不允许更新
        }

    }

    public Integer updateUserInfoData(UserInfoReq userInfoReq){
        String[] avatarData = {
                "/image/avatar/avatar0.jpg",
                "/image/avatar/avatar1.jpg",
                "/image/avatar/avatar2.jpg",
                "/image/avatar/avatar3.jpg",
                "/image/avatar/avatar4.jpg",
                "/image/avatar/avatar5.jpg"};

        String avatarStr = userInfoReq.getUserAvatar(); // 这是前端上传的路径字符串

        // 直接在数组中查找是否存在与avatarStr相同的元素
        for (String path : avatarData) {
            if (path.equals(avatarStr)) {

                LocalDateTime now = LocalDateTime.now();
                userInfoReq.setUpdateTime(now);
                log.info(userInfoReq.getNickname());
                // 更新用户信息
                userMapper.updateUserInfo(userInfoReq);
                return 1; // 更新成功
            }
        }

        // 如果没有找到匹配项，则输出错误信息并返回-1
        System.out.println("无效的头像路径：" + avatarStr);
        return -1;
    }


    @Override
    public void getEmailVerifyCode(String email, boolean isRegistration) {
        // 检查验证码是否已存在
        if (redisTemplate.hasKey(email)) {
            throw new TooManyAttemptsException("已发送过验证码，请稍后再试");
        }

        // 根据操作类型检查邮箱状态
        if (isRegistration) {
            // 注册场景：检查邮箱是否已注册
            if (userMapper.getCountByName(email) > 0) {
                throw new EmailAlreadyRegisteredException();
            }
        } else {
            // 登录场景：检查邮箱是否未注册
            if (userMapper.getCountByName(email) == 0) {
                throw new EmailNotRegisteredException();
            }
        }

        // 生成验证码
        String verifyCode = VerCodeGenerateUtil.generateVerCode();
        log.info("邮箱 {} 生成验证码: {}", email, verifyCode);

        // 存储验证码到Redis（5分钟有效期）
        redisTemplate.opsForValue().set(email, verifyCode, 300, TimeUnit.SECONDS);

        // 发送邮件
        emailService.sendVerificationCodeAsync(verifyCode, email);
    }

    @Override
    public LoginResp captchaLogin(UserLogin userLogin, String ip){
        // 检查登录尝试次数
        if (!allowLoginAttempt(ip)) {
            throw new TooManyAttemptsException();
        }
        // 校验验证码
        validateCaptcha(userLogin.getEmail(), userLogin.getVerifyCode());

        // 获取用户信息
        User user =  userMapper.getByUsername(userLogin.getEmail());

        // 生成令牌并返回响应
        return generateLoginResponse(user);

    }

    private String ForgetVerifyCode(String email) {
        String key = "reset:" + email;
        if (redisTemplate.hasKey(key)) {
            throw new TooManyAttemptsException("已发送过验证码，请稍后再试");
        }

        String verifyCode = VerCodeGenerateUtil.generateVerCode();
        redisTemplate.opsForValue().set(key, verifyCode, 300, TimeUnit.SECONDS);
        return verifyCode;
    }
    private boolean tryGetEmailVerifyCode(String ip) {
        String key = "verify:attempts:" + ip;

        // 获取当前尝试次数
        Long attempts = redisTemplate.opsForValue().increment(key);

        // 如果是第一次尝试，设置过期时间
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, 60,TimeUnit.MINUTES); // 5分钟窗口期
        }

        // 检查是否超过限制
        if (attempts != null && attempts > 5) { // 限制5分钟内最多5次尝试
            return false;
        }

        return true;
    }
    @Override
    public void getForgetEmailVerifyCode(String email, String ip) {

        // 检查尝试次数
        if (!tryGetEmailVerifyCode(ip)) {
            throw new TooManyAttemptsException();
        }

        // 检查邮箱是否注册
        if (userMapper.getCountByName(email) == 0) {
            throw new EmailNotRegisteredException();
        }
        String code = ForgetVerifyCode(email);

        emailService.sendVerificationCodeAsync(code, email);

    }

    @Override
    public String checkResetCaptcha(UserLogin userLogin){

        String redisKey = "reset:" + userLogin.getEmail();

        // 获取并删除验证码
        String expect = redisTemplate.opsForValue().getAndDelete(redisKey);
        // 校验验证码
        validateCaptcha(redisKey, expect);

        // 检查用户是否存在
        if (userMapper.getCountByName(userLogin.getEmail()) == 0) {
            throw new UserNotFoundException();
        }

        // 生成重置token
        String token = UUID.randomUUID().toString();
        String tokenKey = "resetKey:" + userLogin.getEmail();
        redisTemplate.opsForValue().set(tokenKey, token, 300, TimeUnit.SECONDS);
        return token;
    }

    @Override
    public void resetPassword(ResetPasswordReq req) {
        String tokenKey = "resetKey:" + req.getEmail();
        String expect = null;

        try {
            // 检查验证码是否过期
            if (redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS) < 0) {
                throw new BaseException(401, "超出重置密码时效");
            }

            // 从 Redis 读取验证码并删除缓存
            expect = (String) redisTemplate.opsForValue().get(tokenKey);
            if (expect == null) {
                throw new BaseException(401, "超出重置密码时效");
            }
            redisTemplate.delete(tokenKey);

            // 比较用户输入的验证码和缓存中的验证码是否一致
            if (!req.getPasswordKey().equalsIgnoreCase(expect)) {
                throw new CaptchaMismatchException();
            }

            // 获取用户盐值
            String salt = userMapper.getSalt(req.getEmail());
            if (salt == null) {
                throw new EmailNotRegisteredException();
            }

            // 生成新密码
            String newPassword = sha256(salt + req.getPassword());

            String password = userMapper.getPasswordByEmail(req.getEmail());
            if(Objects.equals(password, newPassword)){
                throw new BaseException(401, "新密码不能与旧密码相同");
            }

            // 更新用户密码
            userMapper.resetPassword(req.getEmail(), newPassword);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String refresh(Integer id) {

        UserInfoResp userInfoResp = userMapper.getInfo(id);

        log.info("用户刷新令牌, ID: {} ,邮箱：{}， 昵称：{}， 头像：{}", userInfoResp.getUserId(), userInfoResp.getEmail(), userInfoResp.getNickname(), userInfoResp.getUserAvatar());

        return getToken(userInfoResp.getUserId(), userInfoResp.getEmail(), userInfoResp.getNickname(), userInfoResp.getUserAvatar(), false);
    }

    @Override
    public LoginResp  adminRefresh(Integer id) {
        User user = userMapper.getUserById(id);

        log.info("管理员刷新令牌, ID: {} ,邮箱：{}，权限：{}", user.getId(), user.getEmail(), user.getRole());

        return generateLoginResponse(user);

    }

}
