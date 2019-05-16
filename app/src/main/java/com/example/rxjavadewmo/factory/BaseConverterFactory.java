package com.example.rxjavadewmo.factory;

import com.example.rxjavadewmo.bean.BaseHttpBean;
import com.example.rxjavadewmo.utils.AESCipher;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class BaseConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //判断类型
        if (type == BaseHttpBean.class){
            return new BaseResponseConverter(type);
        }else{
            return null;
        }
    }


    public class BaseResponseConverter<T> implements Converter<ResponseBody, BaseHttpBean> {

        private Type type;
        Gson gosn = new Gson();

        public BaseResponseConverter(Type type){
            this.type = type;
        }

        @Override
        public BaseHttpBean convert(ResponseBody value) throws IOException {
            if (value!=null){
                String string = value.string();
                BaseHttpBean baseHttpBean = gosn.fromJson(string, BaseHttpBean.class);
                if (baseHttpBean!=null){
                    if ("1".equals(baseHttpBean.getStatus())){
                        //请求成功,将信息解密
                        try {
                            baseHttpBean.setInfo(AESCipher.decrypt("b9ul32jtfia&y$o[", baseHttpBean.getInfo()).trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }

                return baseHttpBean;
            }
            return null;
        }
    }
}
