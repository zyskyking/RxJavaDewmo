package com.example.rxjavadewmo.Interface;

import com.example.rxjavadewmo.bean.BaseHttpBean;
import com.example.rxjavadewmo.bean.StreamBean;
import com.example.rxjavadewmo.bean.User;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ResourceByTestidService {

    @FormUrlEncoded
    @POST("{url}")
    Observable<BaseHttpBean> post(@Path (value = "url",encoded = true) String url,@FieldMap Map<String,Object> data);


    @POST("{url}")
    Observable<ResponseBody> upLoadStream(@Path(value = "url",encoded = true) String url, @Body StreamBean stram);


    @FormUrlEncoded
    @POST("{url}")
    Observable<BaseHttpBean> login(@Path(value = "url",encoded = true) String url,@FieldMap Map<String,Object> data);

    @Headers({
            "Request-Origin:android"
    })
    @POST("{url}")
    Observable<ResponseBody> startMarkSpeak(@Path(value = "url",encoded = true) String url,@Body User String);
}
