package com.yeling.user.service.Impl;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import com.yeling.user.domian.entity.User;
import com.yeling.user.mapper.AdminMapper;
import com.yeling.user.mapper.UserMapper;
import com.yeling.user.service.AdminService;
import com.yeling.user.vo.request.UserLogin;
import com.yeling.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private static final long EXPIRE_TIME = 1; // 1 hour in hours
    private static final int MAX_ATTEMPTS = 5;

    private ConcurrentHashMap<String, Integer> ipAccessCount = new ConcurrentHashMap<>();

    private LoginRateLimiter loginRateLimiter = new LoginRateLimiter();

    SensitiveWordBs sensitiveWordBs =
            SensitiveWordBs.newInstance()
                    .wordAllow(WordAllows.empty())
                    .wordDeny(WordDenys.empty())
                    .init();

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;





    private String sha256(String input) {
        try {
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


    private class LoginRateLimiter {
        public boolean allowAccess(String ipAddress) {
            int accessCount = ipAccessCount.getOrDefault(ipAddress, 0);
            if (accessCount >= MAX_ATTEMPTS) {
                return false; // Exceeded max attempts
            }

            ipAccessCount.put(ipAddress, accessCount + 1);
            if (ipAccessCount.size() == 1) {
                scheduleExpirationTask();
            }

            return true;
        }

        private void scheduleExpirationTask() {
            Runnable expirationTask = () -> ipAccessCount.clear();
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(expirationTask, EXPIRE_TIME, TimeUnit.HOURS);
        }

    }

    @Transactional
    @Override
    public Boolean reg(User admin){
        Integer integer = userMapper.getCountByName(admin.getEmail());
        if(integer == 0){
            //随机生成盐
            String randomHexString = generateRandomHexString(16);
            userMapper.addSalt(admin.getEmail(),randomHexString);

            //哈希加盐加密
            admin.setPassword(sha256(randomHexString + admin.getPassword()));
            userMapper.addAdmin(admin.getEmail(), admin.getPassword(), "admin");

            Random random = new Random();
            int randomNumber = random.nextInt(100); // 生成4位随机数

            String nickname; // 保证随机数是4位数字
            nickname = "管理员" +  String.format("%02d", randomNumber);

            Integer id = userMapper.getUserId(admin.getEmail());
            userMapper.addUserInfo(id, admin.getEmail(), nickname, "/image/avatar/avatar88.jpg");

            return true;
        }
        return false;
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



}
