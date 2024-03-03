package com.modeling.backend.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class User {
    private Long id;
    private String userName;
    private String nickName;
    private String avatarUrl;
    private String passWord;
    private String email;
    private String phoneNumber;
    private Boolean status;


}
