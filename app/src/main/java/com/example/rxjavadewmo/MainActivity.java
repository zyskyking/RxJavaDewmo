package com.example.rxjavadewmo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.rxjavadewmo.Interface.LoginService;
import com.example.rxjavadewmo.Interface.ResourceByTestidService;
import com.example.rxjavadewmo.bean.BaseHttpBean;
import com.example.rxjavadewmo.bean.User;
import com.example.rxjavadewmo.factory.BaseConverterFactory;
import com.example.rxjavadewmo.utils.AESCipher;
import com.example.rxjavadewmo.utils.HttpUtils;
import com.example.rxjavadewmo.utils.RetrofitManager;
import com.example.rxjavadewmo.utils.SpokenManager;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainRxJava";

    SpokenManager spokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spokenManager = new SpokenManager(this);
    }


    public void click(View view) {
        spokenManager.StartSpakoExercise();
//        new RetrofitManager().speakDemo();
    }
}

