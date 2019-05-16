package com.example.rxjavadewmo.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpUtils {


    public static Map<String, Object> GetLoginMap(String username, String password) {

        String passmd5 = MD5Util.getMD5String(password);
        passmd5 = passmd5.toLowerCase();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("username", username);
            jsonObject1.put("password", passmd5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sjson = jsonObject1.toString();
        return getPublicParametersMap(sjson);
    }

    public static Map<String, Object> getPublicParametersMap(String json) {
        // json非空判断
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        String Md5Json = MD5Util.getMD5String(json);
        String AESjson = null;
        try {
            AESjson = AESCipher.encrypt("b9ul32jtfia&y$o[", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String apkVersion = "1.6.4";

        map.put("json", AESjson);
        map.put("jsonLength", AESjson.length());
        map.put("md5", Md5Json);
        map.put("fileNum", 0);
        map.put("createTime", 0);
        map.put("NotForceUpdate", "0");
        map.put("SpecialVersion", "0");
        map.put("productVersion", apkVersion);
        map.put("origin", "Android");
        map.put("requestType", "GET");
        map.put("appName","tsStudyingJunior");
        return map;
    }


}
