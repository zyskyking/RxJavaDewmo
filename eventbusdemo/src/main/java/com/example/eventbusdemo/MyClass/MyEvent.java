package com.example.eventbusdemo.MyClass;

public class MyEvent {
    private static MyEvent defaultInstance;

    private MyEvent(){

    }

    /**
     * 单例
     * @return
     */
    public static MyEvent getDefault() {
        if (defaultInstance == null) {
            synchronized (MyEvent.class) {
                if (defaultInstance == null) {
                    defaultInstance = new MyEvent();
                }
            }
        }
        return defaultInstance;
    }
}
