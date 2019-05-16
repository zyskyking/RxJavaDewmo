package com.example.rxjavadewmo.utils;

import android.util.Log;

import com.example.rxjavadewmo.Interface.ResourceByTestidService;
import com.example.rxjavadewmo.bean.BaseHttpBean;
import com.example.rxjavadewmo.bean.User;
import com.example.rxjavadewmo.factory.BaseConverterFactory;
import com.example.rxjavadewmo.factory.StreamRequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求工具类
 */
public class RetrofitManager {

    private static final String TAG=RetrofitManager.class.getCanonicalName();

    private String baseUrl = "https://api.douban.com/v2/movie/";

    private String BaseUrl = "https://api.waiyutong.org/Studenttool/";
    public static final String GET_PROVINCE = "https://student.waiyutong.org/District/";
    public static final String key = "b9ul32jtfia&y$o[";

    private static final long DEFAULT_TIME_OUT = 30000;

    private String tempStartData = "{\"apiVersion\":\"1.0.1\",\"type\":\"2\",\"content\":\"Hello, my name is Peter.\"}";
    private String startUrl = "newAsr";
    private String upUrl = "uploadMp3";
    private String speakBaseUrl = "https://asr.waiyutong.org/Mp3/";

    public ResourceByTestidService publicMethod(){

        //日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //基本参数设置
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();

        //创建retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(speakBaseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ResourceByTestidService resourceByTestidService = retrofit.create(ResourceByTestidService.class);

        return resourceByTestidService;
    }

    public void login(String username ,String password){
        String passmd5 = MD5Util.getMD5String("ts123456");
        passmd5 = passmd5.toLowerCase();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("username","zyxx");
            jsonObject1.put("password", passmd5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sjson = jsonObject1.toString();
        Map<String, Object> publicParametersMap = HttpUtils.getPublicParametersMap(sjson);

        //创建retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ResourceByTestidService resourceByTestidService = retrofit.create(ResourceByTestidService.class);
        resourceByTestidService.login("login",publicParametersMap).subscribeOn(Schedulers.io()).subscribe(new Observer<BaseHttpBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseHttpBean baseHttpBean) {
                Log.d(TAG, "onNext() called with: baseHttpBean = [" + baseHttpBean.getInfo() + "]");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    public void upLoad(byte[] datas){
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(5, TimeUnit.SECONDS);
        client.newBuilder().readTimeout(5, TimeUnit.SECONDS);
        client.newBuilder().writeTimeout(5, TimeUnit.SECONDS);




        RequestBody requestBody = StreamRequestBody.create(MediaType.parse("application/octet-stream"),datas);


        Request request = new Request.Builder().url("https://asr.waiyutong.org/Mp3/uploadMp3").post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.i(TAG, "onResponse: "+string);
            }
        });

    }

    public void speakDemo(){
//日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //基本参数设置
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();

        //创建retrofit
        Retrofit retrofit = new Retrofit.Builder().baseUrl(speakBaseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ResourceByTestidService resourceByTestidService = retrofit.create(ResourceByTestidService.class);
        User user = new User();
        user.type = "2";
        user.apiVersion = "1.0.1";
        user.content = "Hello, my name is Peter.";

        resourceByTestidService.startMarkSpeak(startUrl,user).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.d(TAG, "onNext() called with: responseBody = [" + responseBody.string() + "]");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    public void demo(){

        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//自定义OkHttpClient
        OkHttpClient Clien = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.baseUrl(BaseUrl)
                .addConverterFactory(new BaseConverterFactory())
                .client(Clien)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();


        byte[] binary = new byte[]{11,2,1,2,1,2,3,12,3,13,12,3,123,12,3,8,8,88,7,7,7,77,88,88,88,88,88,88,88,88,88,88,77,55,65,5,45,64,63,53,53,23,42,22,33,22,22,22,22,22,22,22,22,22,22,22,22,22,33};
        RequestBody streamBody = RequestBody.create(MediaType.parse("application/octet-stream"), binary);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("stream", "Pcm-Data", streamBody);

        ResourceByTestidService resourceByTestidService1 = retrofit.create(ResourceByTestidService.class);





        ResourceByTestidService resourceByTestidService = retrofit.create(ResourceByTestidService.class);
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("userId", "2172112");
            jsonObject1.put("test_id", "140001546");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sjson = jsonObject1.toString();
        Map<String, Object> publicParametersMap = HttpUtils.getPublicParametersMap(sjson);

        resourceByTestidService.post("",publicParametersMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseHttpBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseHttpBean baseHttpBean) {
                        if (baseHttpBean!=null){
                            String status = baseHttpBean.getStatus();
                            String info = baseHttpBean.getInfo();
                            Log.d(TAG, "onNext() called with: status = [" + status + "]info = [" + info + "]");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

//        Call<BaseHttpBean> resourceByTestid = resourceByTestidService.getResourceByTestid(publicParametersMap);
//        resourceByTestid.enqueue(new Callback<BaseHttpBean>() {
//            @Override
//            public void onResponse(Call<BaseHttpBean> call, Response<BaseHttpBean> response) {
//                if (response!=null){
//                    String status = response.body().getStatus();
//
//                    String info = response.body().getInfo();
//
//                    Log.d(TAG, "onResponse() called with: status = [" + status + "], info = [" + info + "]");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseHttpBean> call, Throwable t) {
//
//            }
//        });


//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接 超时时间
//        builder.writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
//        builder.readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//读操作 超时时间
//        builder.retryOnConnectionFailure(true);//错误重连
//
//        //拦截器设置头信息
//        Interceptor interceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                Request.Builder xmlHttpRequest = request.newBuilder()
////                        .addHeader("X-Requested-With", "XMLHttpRequest")
//                        .method(request.method(), request.body());
//                Request build = xmlHttpRequest.build();
//                return chain.proceed(build);
//            }
//        };
//
//        builder.addInterceptor(interceptor);
//        //创建实例
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(builder.build())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(GET_PROVINCE).build();
//
//        //进行请求
//        LoginService loginService = retrofit.create(LoginService.class);
//
//        Observable<ResponseBody> responseBodyObservable = loginService.doLogin(HttpUtils.GetLoginMap("zyxx", "ts123456"));
//
//        responseBodyObservable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.d(TAG, "onSubscribe() called with: d = [" + d + "]");
//
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            Log.d(TAG, "onNext() called with: responseBody = [" + responseBody.string() + "]");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }
}
