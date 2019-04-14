package com.whut.androidtest.util;

import android.util.Base64;
import android.util.Log;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    public static String encrypt(String input, String key){
        byte[] crypted = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return new String(Base64.encode(crypted,0));
    }

    public static String decrypt(String input, String key){
        byte[] output = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, 0));
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return new String(output);
    }

    public static void main() {
        String key = "1234567891234567";
        String data = "example";
        Log.d("AESSSS 加密", "main: "+ AESUtil.encrypt(data,key));
        Log.d("AESSSS 解密", "main: "+ AESUtil.decrypt(AESUtil.encrypt(data,key),key));


    }

}
