package com.example.rxjavadewmo.utils;

import android.annotation.SuppressLint;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCipher {  
    public static String encrypt(String key, String src) throws Exception {
        byte[] rawKey = key.getBytes();
        byte[] result = encrypt(rawKey, src.getBytes());  
        return toHex(result);     
    }     
         
    public static String decrypt(String key, String encrypted) throws Exception {
    	byte[] rawKey = key.getBytes();
        byte[] enc = toByte(encrypted);     
        byte[] result = decrypt(rawKey, enc);     
        return new String(result);
    }     
    
    @SuppressLint("DeletedProvider")
    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
         SecureRandom sr = null;
       if (android.os.Build.VERSION.SDK_INT >=  17) {  
         sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
       } else {  
         sr = SecureRandom.getInstance("SHA1PRNG");
       }   
        sr.setSeed(seed);     
        kgen.init(128, sr); //256 bits or 128 bits,192bits  
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();     
        return raw;     
    }     
    
         
    private static byte[] encrypt(byte[] key, byte[] src) throws Exception {
//        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");     
//        //Cipher cipher = Cipher.getInstance("AES");   
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);  
//        byte[] encrypted = cipher.doFinal(src);     
//        return encrypted;   
        
        
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        int blockSize = cipher.getBlockSize();
        int plaintextLength = src.length;
        
        String iv = generateRandomString(16);
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }

        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(src, 0, plaintext, 0, src.length);
        
        SecretKeySpec skeyspec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, skeyspec, ivspec);
        byte[] encrypted = cipher.doFinal(plaintext);
        
        byte[] text = new byte[encrypted.length+iv.length()];
        byte[] ivb = iv.getBytes();

        System.arraycopy(ivb,0,text,0,ivb.length);
        System.arraycopy(encrypted,0,text,ivb.length,encrypted.length);
       
        return text;  
          
    }     
    
    private static byte[] decrypt(byte[] key, byte[] encrypted) throws Exception {
//        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");     
//        //Cipher cipher = Cipher.getInstance("AES");  
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, skeySpec);     
//        byte[] decrypted = cipher.doFinal(encrypted);     
//        return decrypted;     
    	
        
        byte[] text = new byte[encrypted.length-16];
        byte[] ivb = new byte[16];
        System.arraycopy(encrypted,0,ivb,0,16);
        System.arraycopy(encrypted,16,text,0,encrypted.length-16);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec skeyspec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(ivb);
        
        cipher.init(Cipher.DECRYPT_MODE, skeyspec, ivspec);

        byte[] original = cipher.doFinal(text);
        return original;
    }     
    
    public static String toHex(String txt) {
        return toHex(txt.getBytes());     
    }     
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }     
         
    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;     
        byte[] result = new byte[len];     
        for (int i = 0; i < len; i++)     
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;     
    }     
    
    public static String toHex(byte[] buf) {
        if (buf == null)     
            return "";     
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {     
            appendHex(result, buf[i]);     
        }     
        return result.toString();     
    }     
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));     
    }    
    
    
    private static final String POSSIBLE_CHARS="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /** 
     * ����һ��ָ�����ȵ�����ַ��� 
     * @param length �ַ������� 
     * @return 
     */  
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {  
            sb.append(POSSIBLE_CHARS.charAt(random.nextInt(POSSIBLE_CHARS.length())));  
        }  
        return sb.toString();  
    }  
}  