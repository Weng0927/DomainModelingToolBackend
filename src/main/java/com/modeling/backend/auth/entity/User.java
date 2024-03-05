package com.modeling.backend.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("user")
@Data
public class User {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 用户昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 用户头像
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 用户密码
     */
    @TableField("password")
    private String passWord;

    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 用户手机号
     */
    @TableField("phone_number")
    private String phoneNumber;

    /**
     * 用户账号状态（true正常 false停用）
     */
    private Boolean status;
}
