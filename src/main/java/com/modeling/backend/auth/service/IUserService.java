package com.modeling.backend.auth.service;

import com.modeling.backend.auth.entity.User;

public interface IUserService {
    String queryHashedPasswordByUserName(String userName);

    User queryByUserName(String userName);

    void register(String userName, String nickName, String hashedPassword);

    boolean checkUserNameExist(String userName);

    String queryAvatarUrlByUserName(String userName);

    String queryNicknameByUsername(String username);
}
