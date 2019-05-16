package com.example.eventbusdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.eventbusdemo.bean.EventBean;

import org.greenrobot.eventbus.EventBus;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void evnetClick(View view) {
        //子线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG, "evnetClick:run :Thread:: 第二页子线程 "+Thread.currentThread().getName());
//                //发布
//                EventBean eventBean = new EventBean();
//                eventBean.setName("发送到第三个界面 子线程");
////                EventBus.getDefault().post(eventBean);
//                EventBus.getDefault().postSticky(eventBean);
//            }
//        }).start();
//        Log.e(TAG, "evnetClick:Thread:: "+Thread.currentThread().getName());

//        finish();
//        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 10; i++) {
//            EventBus.getDefault().post(new EventBean().setName("name:"+i));
//        }
//        long finialTime = System.currentTimeMillis();
//        Log.e(TAG, "evnetClick: time::" + (finialTime - startTime));
//        finish();


    }

    public void rxClick(View view) {

    }

    public void turnClick(View view) {
        startActivity(new Intent(this,ThirdActivity.class));
    }
}
