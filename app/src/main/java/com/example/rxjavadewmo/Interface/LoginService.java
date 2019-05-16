package com.example.rxjavadewmo.Interface;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginService {

    @FormUrlEncoded
    @POST("province.html?mode=app")
    Observable<ResponseBody> doLogin(@FieldMap Map<String,Object> params);
}
