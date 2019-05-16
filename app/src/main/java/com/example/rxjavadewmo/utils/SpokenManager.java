package com.example.rxjavadewmo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.example.rxjavadewmo.Interface.ResourceByTestidService;
import com.example.rxjavadewmo.bean.StreamBean;
import com.example.rxjavadewmo.bean.User;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 * ���﹤����
 */
public class SpokenManager implements AudioRecordManager.OnRecordListener, AudioRecordManager.OnPlayRecordListener{

    /**
     * ���־
     */
    private static final String TAG = SpokenManager.class.getName();


    /**
     * ֹͣ��Ϣ
     */
    private static final int ORDER_STOP = 0x00;

    /**
     * ��ʼ
     */
    private static final int ORDER_START = 0x01;

    /**
     * ������Ϣ
     */
    private static final int ORDER_CONTINUE = 0x02;

    /**
     * ��ͣ��Ϣ
     */
    private static final int ORDER_PAUSE = 0x03;

    /**
     * ��һ����Ϣ
     */
    private static final int ORDER_NEXT = 0x05;

    /**
     * ���������Ϣ
     */
    private static final int ORDER_DETECTION = 0x06;
    RetrofitManager retrofitManager = new RetrofitManager();

    /**
     * ������
     */
    private Context context;

    /**
     * ֹͣ��־��falseֹͣ,�ж��Ƿ����ֶ�ֹͣ��
     */
    private boolean stop_record_flag = true;


    /**
     * �з���ͣ״̬
     */
    private boolean isPause = false;




    /**�ʶ�ģʽ*/
    public static final String EXMODE_READ ="1";



    /**
     * ��ǰ�����������������id
     */
    private String wid;



    /**
     * ¼���ļ���������ַ
     */
    private String recordUrlPath;

    /**
     * �Զ��ŷָ���¼���ļ���ַ
     */
    private StringBuilder recordPathArray;


    /**
     * ��ǰ��Ŀ������
     */
    private int count_total;
    /**
     * ¼��Ƭ�η��ͼ�¼
     */
    private int recordSendBumber;
    /**
     * ��ȡ¼��Ƭ������
     */
    private int record_size = -1;

    /**����ʶ����*/
    private int taskMarkCount = 0;




    /**
     * �����������mp3¼��Ƭ��
     */
    private List<byte[]> recordMp3List;
    /**
     * ����ϴ������name��
     */
    private List<String> idNameArray;



    /**
     * ¼��������record_manager
     */
    private AudioRecordManager record_manager;


    /**ȡ��ʱ��*/
    private long releaseTime =-1;
    /**¼������*/
    private String recordType;
    /**¼���ı�*/
    private String recordContent;


    /**
     * �չ���
     */
    private SpokenManager() {
        Log.d(TAG, "SpokenManager() called");
        recordPathArray = new StringBuilder();
    }

    /**
     * ���췽��
     *
     * @param context
     */
    public SpokenManager(Context context) {
        this();
        this.context = context;
    }


    /**
     * ��ʼ��ϰ
     */
    public void StartSpakoExercise() {
        retrofitManager.login(null,null);
        Log.d(TAG, "StartSpakoExercise() called");
        //�������ͣģʽ�������
        {
            isPause = false;

            if (recordPathArray != null) {
                recordPathArray.delete(0, recordPathArray.length());
            } else {
                recordPathArray = new StringBuilder();
            }


            if (null == record_manager) {
                synchronized (SpokenManager.class) {
                    if (null == record_manager) {
                        record_manager = new AudioRecordManager(context, this, this);
                    }
                }
            }

            stop_record_flag = true;
            control_handler.sendEmptyMessage(ORDER_START);
        }
    }



    /**
     * ����ϰ�ͷ���Դ
     */
    public void ReleaseSpokenExercise() {
        Log.d(TAG, "ReleaseSpokenExercise() called");
        stop_record_flag = false;
        if (null != record_manager) {
            record_manager.StopRecordForUser();
            record_manager.StopRecord();
            record_manager.StopPlay();
        }
        RemoveAllMessages();
    }


    /**
     * ��ʼ����¼��
     *
     * @param testid _record_temp
     */
    public void StartPlayRecord(String testid) {
        Log.d(TAG, "StartPlayRecord() called with: testid = [" + testid + "]");
        if (null == record_manager) {
            synchronized (SpokenManager.class) {
                if (null == record_manager) {
                    record_manager = new AudioRecordManager(context, this, this);
                }
            }
        }
        if (record_manager.isPlaying()){
            record_manager.StopPlay();
        }else{
            record_manager.PlayRecord(testid);
        }
    }

    /**
     * ���ŵ�����ʱ¼��
     */
    public void StartPlayRecord() {
        Log.d(TAG, "StartPlayRecord() called");
        if (null == record_manager) {
            synchronized (SpokenManager.class) {
                if (null == record_manager) {
                    record_manager = new AudioRecordManager(context, this, this);
                }
            }
        }
        if (record_manager.isPlaying()){
            record_manager.StopPlay();
        }else{
            record_manager.PlayRecord("record_temp");
        }
    }
    /**
     * ��ʼ����¼��
     *
     * @param testId ��Ŀid
     */
    public void StartPlayRecord(String testId, int count) {
        Log.d(TAG, "StartPlayRecord() called with: testId = [" + testId + "], count = [" + count + "]");
        //�ǿ��ж�,��Ŀid���Ȳ���
        if (TextUtils.isEmpty(testId)&&testId.length()<2){
            //���Ϊ�գ�����������
            return;
        }
        if (null == record_manager) {
            synchronized (SpokenManager.class) {
                if (null == record_manager) {
                    record_manager = new AudioRecordManager(context, this, this);
                }
            }
        }
        if (record_manager.isPlaying()){
            record_manager.StopPlay();
        }else{
            record_manager.PlayRecord(testId+count);
        }
    }






    /**
     * ���뱣��ĵ�ַ
     *
     * @param pathArray
     */
    public void setRecordPathArray(StringBuilder pathArray) {
        Log.d(TAG, "setRecordPathArray() called with: pathArray = [" + pathArray + "]");
        recordPathArray = pathArray;
    }



    // TODO ���̿���
    // һ������
    // ��ʼ¼����start��������һ�仰����ɫ����ʱ�����ӳ��ȣ�������Ϣ����ʱ��ֹͣ��������Ϣ
    // //ͣ1s���ͼ�������Ϣ���������ڻص��ӿ��У����̨����֮�������,��һ��������̣���һ����������ü���¼���ķ���
    // //��ʱ���ͣ���ʱ�����ֹͣ������
    // �龰�Ի�����һ�� ��������MP3��������ɣ���ʼ¼�������֮�󣬿�ʼ�ڶ���
    @SuppressLint("HandlerLeak")
    Handler control_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ORDER_START: {
                    Log.d(TAG, "handleMessage() called with: msg = ORDER_START [" + ORDER_START + "]");
                    control_handler.removeMessages(ORDER_START);
                    stop_record_flag = true;

                    {
                        // 1ģʽ����ʼ����¼¼���ļ���������,����Դ�����
                        recordType = "2";
                        // �����1ģʽ������¼��
                        // ��ʼ���ü�����������ʼ¼��
                        // ͨ����ǰ��ҳ���ȥ��ȡtestid
                        if (record_manager.StartRecord("rxjava")) {
                            control_handler.sendEmptyMessageDelayed(ORDER_PAUSE,5000 );
                        }
                    }

                    break;
                }

                case ORDER_STOP: {
                    Log.d(TAG, "handleMessage() called with: msg ORDER_STOP = [" + ORDER_STOP + "]");
                    control_handler.removeMessages(ORDER_STOP);
                    // �ָ�����ʼ����ֹͣ¼��
                    RemoveAllMessages();
                    // �ָ�����ʼ����ֹͣ¼��
                        if (null != record_manager) {
                            record_manager.StopRecord();
                    }
                    break;
                }

                case ORDER_PAUSE: {
                    Log.d(TAG, "handleMessage() called with: msg ORDER_PAUSE = [" + ORDER_PAUSE + "]");
                    control_handler.removeMessages(ORDER_PAUSE);
                    record_manager.PauseRecord();

                    // ��ͣ¼��
                    break;
                }

                case ORDER_CONTINUE: {
                    Log.d(TAG, "handleMessage() called with: msg ORDER_CONTINUE = [" + ORDER_CONTINUE + "]");
                    control_handler.removeMessages(ORDER_CONTINUE);
                        // �ж�red green
                        recordType = "2";
                        recordContent ="Hello, my name is Peter.";
                        if (record_manager.StartRecord("rxjava")) {
                            control_handler.sendEmptyMessageDelayed(ORDER_PAUSE, 5000);
                    }
                        break;
                    }

                    case ORDER_NEXT: {
                        Log.d(TAG, "handleMessage() called with: msg ORDER_NEXT = [" + ORDER_NEXT + "]");
                        control_handler.removeMessages(ORDER_NEXT);
                            if (null != record_manager) {
                                record_manager.StopRecord();
                            }


                        break;
                    }
                    case ORDER_DETECTION: {
                        Log.d(TAG, "handleMessage() called with: msg ORDER_DETECTION = [" + ORDER_DETECTION + "]");
                        control_handler.removeMessages(ORDER_DETECTION);
                        //�������Ϊ��
                        if (recordMp3List == null) {
                            Log.i(TAG, "handleMessage: not find record datas");
                            return;
                        }
                        if (recordSendBumber < recordMp3List.size()) {

                            ResourceByTestidService resourceByTestidService = retrofitManager.publicMethod();
                            Map<String ,String> map = new HashMap<>();
                            map.put("apiVersion", "1.0.1");
                            map.put("id", wid);
                            map.put("index", recordSendBumber+1+"");
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("apiVersion", "1.0.1");
                            jsonObject.addProperty("id", wid);
                            jsonObject.addProperty("index", recordSendBumber);

                            RequestBody photoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), recordMp3List.get(recordSendBumber));

                            Headers headers = new Headers.Builder().add("Request-Origin", "Android").add("Pcm-Data", jsonObject.toString()).build();
                            MultipartBody.Part stream = MultipartBody.Part.create(headers,photoRequestBody);
                            StreamBean streamBean = new StreamBean();
                            streamBean.data = recordMp3List.get(recordSendBumber);

                            retrofitManager.upLoad(recordMp3List.get(recordSendBumber));

//                            resourceByTestidService.upLoadStream("uploadMp3",streamBean)
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(Schedulers.io())
//                                    .subscribe(new Observer<ResponseBody>() {
//                                        @Override
//                                        public void onSubscribe(Disposable d) {
//
//                                        }
//
//                                        @Override
//                                        public void onNext(ResponseBody responseBody) {
//                                            try {
//                                                String string = responseBody.string();
//                                                Log.d(TAG, "upLoadStream  onNext() called with: responseBody = [" + string + "]");
//                                                //��ȡ״̬
//                                                JSONObject json = new JSONObject(string);
//                                                String status = json.optString("status");
//                                                if ("1".equals(status)) {
//                                                    //�ɹ�
//                                                    String wid = json.optString("id");
//                                                    String name = json.optString("name");
//                                                    idNameArray.add(name);
//                                                    recordSendBumber++;
//                                                    control_handler .sendEmptyMessage(ORDER_DETECTION);
//                                                }
//
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError(Throwable e) {
//
//                                        }
//
//                                        @Override
//                                        public void onComplete() {
//
//                                        }
//                                    });

//                            StuHttpManager.UploadMp3(context, wid, String.valueOf(recordSendBumber + 1), recordMp3List.get(recordSendBumber),
//                                    new HttpManagerUploadbinaryCallBack() {
//
//                                        @Override
//                                        public void OnHttpUploadMp3Success(String wid, String name) {
//                                            Log.i(TAG, "�ָ������ͺ�̨�ɹ��� count::" + count);
//                                            recordSendBumber++;
//                                            idNameArray.add(name);
//                                            control_handler
//                                                    .sendEmptyMessage(ORDER_DETECTION);
//                                        }
//
//                                        @Override
//                                        public void OnHttpUploadMp3Error(int code,final String info) {
//                                            stop_record_flag = false;
//                                            Log.d(TAG, "OnHttpUploadMp3Error() called with: code = [" + code + "], info = [" + info + "]");
//
//                                        }
//                                    });

                            // ������͵ĵ��ڶ����б���ģ����ǲ������ܹ���Ƭ������50ms������⣬
                        } else if (recordSendBumber == recordMp3List.size()
                                && recordSendBumber != record_size) {
                            Log.i(TAG, "�ָ����ȴ�̨�� recordSendBumber=" + recordSendBumber + "  recordMp3List.size()=" + recordMp3List.size());
                            control_handler
                                    .sendEmptyMessageDelayed(ORDER_DETECTION, 50);
                        } else {
                            Log.i(TAG, "�ָ����������̨�� recordSendBumber=" + recordSendBumber + "  recordMp3List.size()=" + recordMp3List.size());
                            // ���������������¼��ȫ��������ϣ����ͽ�β��־
                            // ����
//                            StuHttpManager.DoRecognizeMp3(context, wid, idNameArray, SpokenManager.this);
                        }
                        break;
                    }
                }
            }
        };


        /**
         * �Ƴ�������Ϣ
         */
        public void RemoveAllMessages() {
            Log.d(TAG, "RemoveAllMessages() called");
            control_handler.removeMessages(ORDER_START);
            control_handler.removeMessages(ORDER_PAUSE);
            control_handler.removeMessages(ORDER_CONTINUE);
            control_handler.removeMessages(ORDER_NEXT);
            control_handler.removeMessages(ORDER_DETECTION);
            control_handler.removeMessages(ORDER_STOP);
        }

        /**
         * ¼��׼����ʼ�ص�
         */
        @Override
        public void onRecordBefore() {
            Log.d(TAG, "onRecordBefore() called");

            // ��ʼ¼������ʼ׼�������ļ�
            recordSendBumber = 0;
            record_size = -1;
        }

        /**
         * ¼�������лص��ӿ�
         */
        @Override
        public void onRecording(long volume) {
            Log.d(TAG, "onRecording() called with: volume = [" + volume + "]");
        }

        /**
         * ¼���߳̽����ص��ӿ�
         */
        @Override
        public void onRecordFinish(final List<byte[]> mp3Datas) {
            Log.d(TAG, "onRecordFinish() called with: mp3Datas = [" + mp3Datas + "]");

            //����ģʽ����ԭģʽ

            ResourceByTestidService resourceByTestidService = retrofitManager.publicMethod();
            User user = new User();
            user.type = "2";
            user.apiVersion = "1.0.1";
            user.content = "Hello, my name is Peter.";
            resourceByTestidService.startMarkSpeak("newAsr",user).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String string = responseBody.string();
                                Log.d(TAG, "onNext() called with: responseBody = [" + string + "]");
                                JSONObject jsonObject = new JSONObject(string);
                                String id = jsonObject.getString("id");
                                wid = id;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startPrepareMark(mp3Datas);

//
//                            //����ɹ�
//                            //��ȡ����id

//                            //׼��

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }

    /**
     * ׼���ϴ���Ƶ
     * @param mp3Datas
     */
    private void startPrepareMark(List<byte[]> mp3Datas){
        Log.d(TAG, "startPrepareMark() called with: mp3Datas = [" + mp3Datas + "]");
            //        this.file_length = file_length;
            //        record_size = record_list.size();
            //�����е���ƵƬ��ƴ�ӳ�һ��Ƭ��
            recordMp3List = jointMp3(mp3Datas);
            //�ܵ�Ƭ������
            if (recordMp3List != null) {
                record_size = recordMp3List.size();
            }

            //�������ݼ���
            recordSendBumber = 0;
            //֪ͨ��������
            control_handler.sendEmptyMessage(ORDER_DETECTION);
            //��¼mp3name
            if (idNameArray == null) {
                idNameArray = new ArrayList<>();
            } else {
                idNameArray.clear();
            }

        }


        /**
         * ����������
         */
        private List<byte[]> jointMp3(List<byte[]> datas) {
            Log.d(TAG, "jointMp3() called with: datas = [" + datas + "]");
            if (datas != null && datas.size() > 0) {
                byte[] dataAll = null;
                for (int i = 0; i < datas.size(); i++) {
                    //��һ�β��úϲ���ֱ�Ӹ�ֵ
                    if (i == 0) {
                        dataAll = datas.get(i);
                    } else {
                        dataAll = AudioRecordManager.byteMerger(dataAll, datas.get(i));
                    }
                }

                List<byte[]> bytes = new ArrayList<>();
                if (dataAll.length > 0) {
                    bytes.add(dataAll);
                }
                return bytes;
            }
            return null;
        }

        //¼������ص��ӿ�
        @Override
        public void onRecordError() {
            Log.d(TAG, "onRecordError() called");
            control_handler.sendEmptyMessage(ORDER_STOP);
            stop_record_flag = false;
        }

        // TODO ¼�����Żص��ӿ�
        @Override
        public void onPlayRecordBefore() {
            Log.d(TAG, "onPlayRecordBefore() called");

        }

        @Override
        public void onPlayRecording() {
            Log.d(TAG, "onPlayRecording() called");
        }

        @Override
        public void onPlayRecordFinish() {
            Log.d(TAG, "onPlayRecordFinish() called");
        }

        /**
         * ����¼����������ص�
         *
         * @param errorCode
         * @param errorInfo
         */
        @Override
        public void onPlayRecordError(int errorCode, String errorInfo) {
            Log.d(TAG, "onPlayRecordError() called with: errorCode = [" + errorCode + "], errorInfo = [" + errorInfo + "]");
        }


//        // TODO ��ȡ�зֻص��ӿ�
//        @Override
//        public void OnHttpEndSpeakSuccess(boolean isReappear, SpeakResultBean bean) {
//            Log.d(TAG, "OnHttpEndSpeakSuccess() called with: isReappear = [" + isReappear + "], bean = [" + bean + "]");
//            //�������
//            List<textBean.wordBean> words = null;
//
//        if (bean == null) {
//            recordUrlPath = "";
//            words=null;
//            //��Ϊʶ��ʧ�ܴ���
//            discernDefeated("ʶ��ʧ��");
//            return;
//            }
//
//            //���ǿ��
//
//            recordPathArray.append(recordUrlPath+",");
//
//
//                stop_record_flag = false;
//
//            }

    /**
     * �滻��Ӧ��·��
     * @param count
     * @param recordUrlPath
     * @param path
     * @return
     */
    private String replacePath(int count, String recordUrlPath, String path) {
        Log.d(TAG, "replacePath() called with: count = [" + count + "], recordUrlPath = [" + recordUrlPath + "], path = [" + path + "]");
        //�ǿ��ж�
        if(count<0|| TextUtils.isEmpty(path)){
            return path;
        }

        //��ȡ��ǰһ�������±�
        int firstIndex  = getCharIndexFromString(",",count,path);
        //������
        if (firstIndex==-1){
            return path;
        }
        //��ǰ�ַ���
        String firstTemp = path.substring(0,firstIndex);

        if (!TextUtils.isEmpty(firstTemp)){
            //ǰ���о���,��Ҫ��Ӷ���
            firstTemp = firstTemp+",";
        }
        //��ȡ���һ�������±�
        int lastIndex  = getCharIndexFromString(",",count+1,path);
        //�����Ҫ��
        if (lastIndex==-1){
            return firstTemp + recordUrlPath+",";
        }
        //���Ӻ�����ַ���
        String lastTemp = path.substring(lastIndex,path.length());
        String newPath = firstTemp + recordUrlPath + lastTemp;

        return newPath;
    }

    /**
     * ��ȡָ�� �ַ������ַ��������ֵ�n�ε��±�
     * @param s �ַ�
     * @param number ���ִ���
     * @param string �ַ���
     * @return
     */
    private int getCharIndexFromString(String s, int number, String string) {
        Log.d(TAG, "getCharIndexFromString() called with: s = [" + s + "], number = [" + number + "], string = [" + string + "]");
        //�ǿ��ж�
        if (TextUtils.isEmpty(s)|| TextUtils.isEmpty(string)||number<0){
            return -1;
        }else{
            //��ȡһ���±꣬�г�һ�Σ��ڼ�������˷���
            int index = 0;
            for (int i = 0; i < number; i++) {
                index = string.indexOf(s,index+1);
            }
            return index;
        }
    }

//        @Override
//        public void OnHttpEndSpeakError(int code, String info) {
//        Log.d(TAG, "OnHttpEndSpeakError() called with: info = [" + info + "]");
//
//            discernDefeated(info);
//        }



    /**
     * ʶ��ʧ��
     * @param info
     */
    private void discernDefeated(String info){
        Log.d(TAG, "discernDefeated() called with: info = [" + info + "]");

            stop_record_flag = false;
        }


        /**
         * ���￪ʼ�з�ʧ�ܻص��ӿ�
         *
         * @param info ʧ��ԭ��
         */
//        @Override
//        public void OnStartSpeakHttpError(int code,final String info) {
//            Log.d(TAG, "OnStartSpeakHttpError() called with: info = [" + info + "]");
//
//        }
}

