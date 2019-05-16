package com.example.rxjavadewmo.Interface;

import com.example.rxjavadewmo.bean.MovieBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MovieService {

    @GET("top250")
    Call<MovieBean> getTop250(@Query("start") int start, @Query("count") int count);

    @FormUrlEncoded
    @POST("top250")
    Call<MovieBean> postTop250(@Field("start") int start, @Field("count") int count);
}
