package com.yeling.user.mapper;

import com.yeling.user.domian.entity.User;
import com.yeling.user.vo.response.UserInfoResp;
import com.yeling.user.vo.request.UserInfoReq;
import com.yeling.user.vo.request.UserLogin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    @Insert("insert into users(email,user_password,create_time,update_time,type_user) " +
            "values (#{email},#{password},CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,#{typeUser})")
    void addAdmin(String email,String password, String typeUser);



    @Insert("insert into users(email,user_password,create_time,update_time) " +
            "values (#{email},#{password},CURRENT_TIMESTAMP,CURRENT_TIMESTAMP ) ")
    void addUser(UserLogin user);

    @Select("select *from users where email=#{email} and user_password=#{password} ")
    User getByUsernameAndPassword(String email, String password);

    @Select("select *from users where email=#{email} ")
    User getByUsername(String email);

    @Select("select *from users where email=#{email} and user_password=#{password} and role = 'admin' ")
    User getAdminLoginByUserNameAndPassword(String email, String password);

    @Select("select id,email,role from users where id=#{id} ")
    User getUserById(Integer id);

    @Select("select salt from salt where email=#{email}")
    String getSalt(String email);
//    @Select("select *from user where user_name=#{username}")
//    User fundUser(User user);
//
//    @Insert("")
//    User getByUserinfo();

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email} ")
    Integer getCountByName(String email);

    @Insert("insert into salt(email,salt) " +
            "values (#{email},#{salt})")
    void addSalt(String email,String salt);

    @Select("select id from users where email = #{email} ")
    Integer getUserId(String email);

    @Insert("insert into user_info(user_id, email,nickname,user_avatar,update_time) " +
            "values (#{id},#{email},#{nickname},#{userAvatar},'20200101' ) ")
    void addUserInfo(Integer id, String email, String nickname, String userAvatar);

    @Select("select user_id,email,nickname,user_avatar from user_info where user_id=#{id} ")
    UserInfoResp getInfo(int id);


    @Update("update user_info set nickname=#{nickname},user_avatar=#{userAvatar},update_time=#{updateTime} where user_id=#{id} ")
    void updateUserInfo(UserInfoReq userInfoReq);

    @Update("update user_info set user_avatar=#{userAvatar} where user_id=#{uid} ")
    void updateUserAvatar(Integer uid, String userAvatar);

    @Select("select COUNT(*) from user_info where nickname = #{nickname} ")
    Integer showIsUserNiCheng(String nickname);

    @Select("SELECT update_time FROM user_info WHERE user_id = #{uid} ")
    LocalDateTime getUpdateTimeByUid(Integer uid);

    @Select("SELECT nickname FROM user_info WHERE user_id = #{uid} ")
    String getNiChengByUid(Integer uid);

    @Select("SELECT COUNT(*) FROM users WHERE state = #{state} ")
    int getCountByState(Integer state);

    @Update("""
            UPDATE users SET user_password=#{newPassword} WHERE email=#{email}
            """)
    void resetPassword(String email, String newPassword);

    @Select("SELECT user_password FROM users WHERE email=#{email} ")
    String getPasswordByEmail(String email);
    
}
