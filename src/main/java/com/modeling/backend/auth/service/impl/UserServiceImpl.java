package com.modeling.backend.auth.service.impl;

import com.modeling.backend.auth.entity.User;
import com.modeling.backend.auth.mapper.UserMapper;
import com.modeling.backend.auth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String queryHashedPasswordByUserName(String userName) {
        return userMapper.queryHashedPasswordByUserName(userName);
    }

    @Override
    public User queryByUserName(String userName) {
        return userMapper.queryByUserName(userName);
    }

    @Override
    public void register(String userName, String nickName, String hashedPassword) {
        User user = new User();
        user.setUserName(userName);
        user.setNickName(nickName);
        user.setPassWord(hashedPassword);
        user.setStatus(true);
        userMapper.insert(user);
    }

    @Override
    public boolean checkUserNameExist(String userName) {
        return userMapper.checkUserNameExist(userName) > 0;
    }

    @Override
    public String queryAvatarUrlByUserName(String userName) {
        return userMapper.queryAvatarUrlByUserName(userName);
    }

    @Override
    public String queryNicknameByUsername(String userName) {
        return userMapper.queryNickNameByUserName(userName);
    }
}
