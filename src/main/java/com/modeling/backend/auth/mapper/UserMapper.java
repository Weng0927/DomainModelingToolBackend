package com.modeling.backend.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.modeling.backend.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT password FROM user WHERE user_name = #{userName}")
    String queryHashedPasswordByUserName(String userName);

    @Select("SELECT user_name, nick_name, password, email, user_id, phone_number, status, login_ip, login_date" +
            " FROM user WHERE user_name = #{userName}")
    User queryByUserName(String userName);

    @Select("SELECT COUNT(*) FROM user WHERE user_name = #{userName}")
    int checkUserNameExist(String userName);

    @Select("SELECT avatar_url FROM user WHERE user_name = #{userName}")
    String queryAvatarUrlByUserName(String userName);

    @Select("SELECT nick_name FROM user WHERE user_name = #{userName}")
    String queryNickNameByUserName(String userName);
}
