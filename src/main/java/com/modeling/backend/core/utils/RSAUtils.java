package com.modeling.backend.core.utils;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

public final class RSAUtils {
    private static KeyPair keyPair;

    /**
     * 获取RSA公私钥匙对
     */
    static {
        File privateKeyFile = new File("privateKey.txt");
        File publicKeyFile = new File("publicKey.txt");
        if(!privateKeyFile.exists() && !publicKeyFile.exists()) {
            KeyPairGenerator keyPairGenerator = null;
            try {
                keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            keyPairGenerator.initialize(2048); //512、1024、2048
            keyPair = keyPairGenerator.generateKeyPair();
            try {
                String publicKey = getPublicKey();
                String privateKey = getPrivateKey();
                string2File(publicKey, "publicKey.txt");
                string2File(privateKey, "privateKey.txt");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<String> temp = Files.readAllLines(publicKeyFile.toPath());
                String publicKey = String.join("\n", temp);
                temp = Files.readAllLines(privateKeyFile.toPath());
                String privateKey = String.join("\n", temp);
                keyPair = new KeyPair(string2PublicKey(publicKey), string2PrivateKey(privateKey));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }


    /**
     * 获取公钥(base64编码)
     */
    public static String getPublicKey() {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64Tool.byteToBase64(bytes);
    }


    /**
     * 获取私钥(Base64编码)
     */
    public static String getPrivateKey() {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64Tool.byteToBase64(bytes);
    }


    public static String[] genKeyPair() throws Exception {
        String[] keyPairArr = new String[2];
        keyPairArr[0] = getPublicKey();
        keyPairArr[1] = getPrivateKey();
        return keyPairArr;
    }

    /**
     * 将Base64编码后的公钥转换成PublicKey对象
     */
    public static PublicKey string2PublicKey(String pubStr) throws Exception {
        byte[] keyBytes = Base64Tool.base64ToByte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 将Base64编码后的字符串写入文件
     */
    public static void string2File(String key, String path) {
        try {
            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(key);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     */
    public static PrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = Base64Tool.base64ToByte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 公钥加密
     */
    public static String publicEncrypt(String content, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, RSAUtils.string2PublicKey(publicKey));
        byte[] byteEncrypt = cipher.doFinal(content.getBytes("utf-8"));
        String msg = Base64Tool.byteToBase64(byteEncrypt);
        return msg;
    }

    /**
     * 私钥解密
     */
    public static String privateDecrypt(String contentBase64, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, RSAUtils.string2PrivateKey(privateKey));
        byte[] bytesDecrypt = cipher.doFinal(Base64Tool.base64ToByte(contentBase64));
        String msg = new String(bytesDecrypt, "utf-8");
        return msg;
    }

    static class Base64Tool {
        /**
         * 字节数组转Base64编码
         */
        public static String byteToBase64(byte[] bytes) {
            return new String(Base64.getMimeEncoder().encode(bytes));
        }


        /**
         * Base64编码转字节数组
         */
        public static byte[] base64ToByte(String base64Key) throws IOException {
            return Base64.getMimeDecoder().decode(base64Key);
        }

    }
}
