package com.modeling.backend.auth.controller;

import jakarta.servlet.http.HttpSession;
import com.alibaba.fastjson2.JSONObject;
import com.modeling.backend.core.domain.Result;
import com.modeling.backend.core.utils.RSAUtils;
import com.modeling.backend.core.utils.PasswordStorageUtils;
import com.modeling.backend.auth.service.IUserService;
import com.modeling.backend.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SessionController {
    private final String privateKey = RSAUtils.getPrivateKey();
    private final String publicKey = RSAUtils.getPublicKey();
    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login/requestWithUsername")
    public Result<Object> requestLoginWithUsername(@RequestBody JSONObject params) {
        String userName = (String) params.get("username");
        if (!userService.checkUserNameExist(userName)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("avatarUrl", "");
            jsonObject.put("availableLoginMethod", new String[]{"register"});
            return Result.ok(jsonObject, "用户名不存在!");
        } else {  // 用户名存在
            String avatarUrl = userService.queryAvatarUrlByUserName(userName);
            User user = userService.queryByUserName(userName);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("nickname", user.getNickName());
            jsonObject.put("avatarUrl", avatarUrl);
            jsonObject.put("availableLoginMethod", new String[]{"password"});
            return Result.ok(jsonObject, "用户名存在");
        }
    }

    @PostMapping("/login/checkUsernameExist")
    public Result<Boolean> checkUsernameExist(@RequestBody JSONObject param) {
        try {
            if (userService.checkUserNameExist((String) param.get("username"))) {
                return Result.ok(true, "用户名存在!");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return Result.ok(false, "用户名不存在!");
    }

    @GetMapping("/publicKey")
    public Result<String> getPublicKey() {
        try {
            return Result.ok(publicKey, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register")
    public Result<Object> register(@RequestBody JSONObject params) {
        String userName = (String) params.get("username");
        String nickName = (String) params.get("nickname");
        String password = (String) params.get("password");
        if (userService.checkUserNameExist(userName)) {
            return Result.fail("用户名已存在!");
        }
        // 来自前端的请求中, password 应该经过 RSA 加密
        try {
            password = RSAUtils.privateDecrypt(password, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            // 使用 PBKDF2 对密码进行加密存储
            String hashedPassword = PasswordStorageUtils.createHash(password);
            userService.register(userName, nickName, hashedPassword);

            return Result.ok(setResult(userName,
                            userService.queryAvatarUrlByUserName(userName),
                            userService.queryNicknameByUsername(userName)),
                    "注册成功!");
        } catch (PasswordStorageUtils.CannotPerformOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Result.fail("注册失败!");
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody JSONObject params, HttpSession session) {
        String userName = (String) params.get("username");
        String password = null;
        System.out.println("password is " + password);
        try {
            password = RSAUtils.privateDecrypt((String) params.get("password"), privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String hashedPassword = userService.queryHashedPasswordByUserName(userName);
        if (hashedPassword == null) {
            return Result.fail("用户名错误 无法查询");
        }
        try {
//            // 使用 PBKDF2 对提供的密码进行验证
            if (PasswordStorageUtils.verifyPassword(password, hashedPassword)) {
                session.setAttribute("user", userName);


                return Result.ok(setResult(userName,
                        userService.queryAvatarUrlByUserName(userName),
                        userService.queryNicknameByUsername(userName)), "登录成功!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("登录失败!");
        }
        return Result.fail("用户或密码错误!");
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return Result.ok();
    }

    /**
     * 服务间通信方法 不对前端提供接口
     *
     * @return 用户信息
     */
    @GetMapping("/queryUserByUserName/{userName}")
    public User queryUserByUserName(@PathVariable String userName) {
        return userService.queryByUserName(userName);
    }

    private JSONObject setResult(String userName, String avatarUrl, String nickName) {
        JSONObject result = new JSONObject();
        result.put("username", userName);
        result.put("avatarUrl", avatarUrl);
        result.put("nickname", nickName);
        return result;
    }

}