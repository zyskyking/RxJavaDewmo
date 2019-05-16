package com.example.rxjavadewmo.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.spokedemo.utils.LameUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * ¼���Ͳ���¼����
 *
 * @author zhuyue
 *         <p>
 *         frequence ��Ƶ�ı����� channelConfig ��Ƶ������ audioEncoding ��Ƶ���ݸ�ʽ��PCM 16λÿ������
 */
public class AudioRecordManager {
    private static final String TAG = AudioRecordManager.class.getSimpleName();

    private int frequence = 16000; // ¼��Ƶ�ʣ���λhz.����ʵ����AudioRecord�����ʱ�򣬻������ʼд��11025�Ͳ��С�ȡ����Ӳ���豸
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private OnRecordListener record_listener; // ¼���ص��ӿ�
    private OnPlayRecordListener play_listener;// ����¼���ص��ӿ�


    private int bufferSize;
    private AudioRecord mRecorder;
    private static AudioRecordManager mInstance;
    private boolean isRecording = false;
    private boolean isPlaying = false; // ���
    private boolean isStopForUser = false;//�ֶ�ֹͣ��־
    private boolean isStopPlayForUser = false;//�ֶ�ֹͣ���ű�־
    private boolean isStopForTime = false;//��ʱֹͣ��־
    private boolean isWhile = false;//�ж��Ƿ���ѭ���ȴ�
    private boolean isRecordThreadIng = false;//�ж�¼���߳��Ƿ����
    private boolean isPlayRecordThreadIng = false;//�жϲ���¼���߳��Ƿ����
    private RecordTask recorder; // ¼��
    private File audioFile; // �����ļ�
    private PlayTask player; // ������
    private RandomAccessFile accessfile;// �ϵ��ļ�
    private DataOutputStream dos;// ��ʼ�����
    private short[] buffer;
    private Context context;
    private long save_file_length; //�����ļ�����
    private String temp_fileName;// �ļ���ȥ����׺��ʱ����

    private String test_id;

    /**�������ɵ�mp3����*/
    private List<byte[]> mp3Datas;
    // �ļ�·��
//    private String file_path = Environment.getExternalStorageDirectory()
//            .getAbsolutePath() + "/com.tingshuo.student/Resource/Record/";

    private AudioRecordManager(Context context) {
        this.context = context;
        // ���ݶ���õļ������ã�����ȡ���ʵĻ����С
        bufferSize = AudioRecord.getMinBufferSize(frequence, channelConfig,
                audioEncoding);
        // ʵ����AudioRecord
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, frequence,
                channelConfig, audioEncoding, bufferSize << 4);
        // ���建��
        buffer = new short[bufferSize << 3];

//        if (!isSDCard()) {
//            file_path = context.getFilesDir().getAbsolutePath();
//        }
        // �ж�·���Ƿ����,����·��
        try {
            File filePath = new File(getPath(true));

            if (!(filePath.exists())) {
                filePath.mkdirs();
            }
            //����ļ������ļ���ɾ���������ļ���
            if (filePath.isFile()){
                filePath.delete();
                filePath.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    /**
     * ��ȡ·��
     *
     * @param isPlay �Ƿ��ǲ�����Ƶ
     * @return ·��
     */
    private String getPath(boolean isPlay) {

        return Environment.getExternalStorageDirectory()+"/rxjava/";

    }

    /**
     * ¼��ʱ����
     *
     * @param context
     * @param record_listener
     */
    public AudioRecordManager(Context context, OnRecordListener record_listener) {
        this(context);
        this.record_listener = record_listener;

    }

    /**
     * ����¼��ʱ����
     *
     * @param context
     * @param play_listener
     */
    public AudioRecordManager(Context context, OnPlayRecordListener play_listener) {
        this(context);
        this.play_listener = play_listener;
    }

    /**
     * ¼��������ʱ����
     *
     * @param context ������
     * @param record_listener ����
     * @param play_listener ����
     */
    public AudioRecordManager(Context context,
                              OnRecordListener record_listener, OnPlayRecordListener play_listener) {
        this(context);
        this.record_listener = record_listener;
        this.play_listener = play_listener;

    }

    /**
     * ��ʼ¼��
     *
     * @param testid ������ļ�������׺.pcm������Զ����
     */
    public synchronized boolean StartRecord(String testid) {
        //�Ѿ����߳��ڵȴ���

        if (isRecordThreadIng==true){
            isStopForUser = true;
        }
//        //�̵߳ȴ�
        while (isRecordThreadIng==true) {
            // �̵߳ȴ����ȴ�¼������
            isWhile = true;
            Log.i(TAG, "�ȴ�ǰһ�߳̽���");
        }

        CreateFile(testid);
        test_id = testid;


        // ��������¼������
        recorder = new RecordTask();

        recorder.execute();
        return true;
    }

    /**
     * ֹͣ¼��
     */
    public void StopRecord() {
        Log.i(TAG, "StopRecord: "+isStopForTime+" recourd :"+isRecording);
        if (isRecordThreadIng){
            isStopForTime = true;
        }else{
            isStopForTime = false;
        }

        if (null != accessfile) {
            try {
                accessfile.close();
                accessfile = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * �����̿��ƣ��ֶ�ֹͣ�����ص�onFinish�ӿ�
     */
    public void StopRecordForUser() {
        Log.i(TAG, "StopRecordForUser: "+isStopForUser+" record:"+isRecording);
        if (isRecordThreadIng) {
            isStopForUser = true;
        } else {
            isStopForUser = false;
        }
    }

    /**
     * �ж��Ƿ���¼��
     *
     * @return
     */
    public boolean isRecord() {

        return isRecording;
    }

    /**
     * ����¼��
     *
     * @param fileName ¼���ļ�����150000-A ����1500000-A.pcm
     */
    public void PlayRecord(String fileName) {
//		fileName = "150000842Q1";
        if (!TextUtils.isEmpty(fileName)) {
            fileName = fileName.split("\\.")[0];
            // �ļ����ڲ��ţ�����������ʾ
            if (TextUtils.isEmpty(getPath(true))) {
                Toast.makeText(context, "sdk¼��·��������", Toast.LENGTH_SHORT).show();
                if (play_listener!=null){
                    play_listener.onPlayRecordError(1,"sdk¼��·��������");
                }
                return;
            }

            audioFile = new File(getPath(true) + fileName + ".pcm");
            if (audioFile.exists()) {
                try {
                    if (isPlayRecordThreadIng==true){
                        isStopPlayForUser = true;
                    }

                    while (isPlayRecordThreadIng==true) {
                        // �̵߳ȴ����ȴ�¼������
                        Log.i(TAG, "�ȴ�ǰһ�߳̽���");
                    }
                    if (player == null) {
                        synchronized (AudioRecordManager.class) {
                            if (player == null) {
                                player = new PlayTask();
                                player.execute();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (play_listener!=null){
                        play_listener.onPlayRecordError(1,"���ŷ�������");
                    }
                }
            } else {
                Toast.makeText(context, "¼���ļ�������", Toast.LENGTH_SHORT).show();
                if (play_listener!=null){
                    play_listener.onPlayRecordError(0,"¼���ļ�������");
                }
            }
        } else {
            Toast.makeText(context, "¼���ļ�������", Toast.LENGTH_SHORT).show();
            if (play_listener!=null){
                play_listener.onPlayRecordError(0,"¼���ļ�������");
            }
        }
    }

    /**
     * ֹͣ����
     */
    public void StopPlay() {
        if (isPlaying){
            isStopPlayForUser = true;
        }else{
            isStopPlayForUser = false;
        }
    }

    /**
     * ��ͣ¼��
     */
    public void PauseRecord() {
        StopRecord();
    }

    /**
     * ɾ��¼���ļ�
     *
     * @param fileName
     */
    public void DeleteRecordFile(String fileName) {
        fileName = fileName.split("\\.")[0];
        try {
            File file = new File(getPath(false) + fileName + ".pcm");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ɾ������ļ�
     * @param fileName
     */
    public void DeleteRecordFiles(String fileName){
        fileName = fileName.split("\\.")[0];
        try {
            File[] files = FindFiles(fileName);
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ����Ŀ¼��ָ��test��ʼ���ļ�
     *
     * @param testid
     * @return file[] ����ļ������� return null
     */
    public File[]  FindFiles(String testid) {
        temp_fileName = testid.split("\\.")[0];
        //��ȡĿ¼����testid��ʼ���ļ�
        if (TextUtils.isEmpty(temp_fileName)) {
            return null;
        } else {
            if (TextUtils.isEmpty(getPath(true))) {
                return null;
            }
            File[] files = new File(getPath(true)).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(temp_fileName);
                }
            });

            //���ļ�������
            for (int i = 0; i < files.length; i++) {
                for (int j = i; j < files.length; j++) {
                    String fileName = files[i].getName();
                    String fileName2 = files[j].getName();
                    if (TextUtils.isEmpty(fileName)|| TextUtils.isEmpty(fileName2)){
                        break;
                    }
                    boolean isBig = false;
                    try{
                        //123412431.pcm ȥ����׺��
                        fileName = fileName.split("\\.")[0];
                        fileName2 = fileName2.split("\\.")[0];
                        //�Է�����װ���쳣
                        if (Long.parseLong(fileName)> Long.parseLong(fileName2)){
                            isBig = true;
                        }
                    }catch (Exception e){
                        //�쳣��ʾ �����ַ�����ʹ���ַ�������Ƚ�
                        if (fileName.compareTo(fileName2)>0){
                            isBig = true;
                        }
                    }

                    if (isBig) {
                        File temp = files[i];
                        files[i] = files[j];
                        files[j] = temp;
                    }
                }
            }

            return files;
        }

    }

    /**
     * ��ѯ¼���ļ�
     *
     * @param testid
     * @return
     */
    public File FindFile(String testid) {
        String fileName = testid.split("\\.")[0];
        if (TextUtils.isEmpty(fileName)) {
            return null;
        } else {
            if (TextUtils.isEmpty(getPath(true))) {
                return null;
            }
            File file = new File(getPath(true) + fileName + ".pcm");
            if (file.exists()) {
                return file;
            } else {
                Toast.makeText(context, "¼���ļ�������", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

    }




    /**
     * ¼��
     *
     * @author zhuyue
     */
    class RecordTask extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            Log.i(TAG, "doInBackground: ��ʼ¼��");
            //¼���߳����ڽ���
            isRecordThreadIng = true;
            mp3Datas = new ArrayList<>();
            boolean hasPermission = true;//�ж��Ƿ���Ȩ��
            isRecording = true;
            Log.i(TAG, "doInBackground: "+isRecording);
            //����ת��mp3
            LameUtils.init(16000,1,16000,24,7);
            try {
                // ��ͨ�������ָ�����ļ�
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile, true)));
                // ���ݶ���õļ������ã�����ȡ���ʵĻ����С
                bufferSize = AudioRecord.getMinBufferSize(frequence,
                        channelConfig, audioEncoding);
                // ���建��
                buffer = new short[bufferSize << 3];
                //ת������
                byte[] mMp3Buffer = new byte[(int) (7200 + (bufferSize * 2 * 1.25))];

                // ��ʼ¼��
                try {
                    mRecorder.startRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                //ͨ��״̬�ж��Ƿ���Ȩ��
                if (mRecorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING
                        && mRecorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
//                    Log.i("TAG", "״̬ûȨ��");
//					Toast.makeText(context, "������app¼��Ȩ��", Toast.LENGTH_LONG).show();
                    return false;
                }

                // ����ѭ��������isRecording��ֵ���ж��Ƿ����¼��
                while (true) {
                    Log.i(TAG, "doInBackground: user:"+isStopForUser+"  time::"+isStopForTime);
                    if (isStopForUser||isStopForTime){
                        break;
                    }
                    long volume = 0;
                    int bufferReadResult = mRecorder.read(buffer, 0, buffer.length);
                    int encodedSize = LameUtils.encode(buffer, buffer, buffer.length, mMp3Buffer);
                    //��ȡmp3����
                    byte[] mp3Data =new byte[encodedSize];
                    for (int i = 0; i < encodedSize; i++) {
                        mp3Data[i] = mMp3Buffer[i];
                    }
                    mp3Datas.add(mp3Data);
                    if (bufferReadResult == AudioRecord.ERROR_INVALID_OPERATION || bufferReadResult <= 0) {
                        
//						Toast.makeText(context, "������app¼��Ȩ��", Toast.LENGTH_LONG).show();
                        mRecorder.stop();
                        return false;
                    }

                    // ѭ����buffer�е���Ƶ����д�뵽OutputStream��


                    for (int i = 0; i < bufferReadResult; i++) {
                        dos.writeShort(buffer[i]);
                        volume += buffer[i] * buffer[i];
                    }
//                    if (bufferReadResult > 0) {
//                       Log.i("TAG","������С����"+(int) Math.sqrt(volume / bufferReadResult));
//                    }
                    //��������
                    long volumeTemp = (int) Math.sqrt(volume / bufferReadResult);
                    //����������С ��������100���ң�����2000����
                    record_listener.onRecording(volumeTemp);
                }
                // ¼�ƽ���
                mRecorder.stop();
                //д��mp3��β����
                int flushResult = LameUtils.flushs(mMp3Buffer);
                if (flushResult > 0) {
                    //��ȡmp3����
                    byte[] mp3Data =new byte[flushResult];
                    for (int i = 0; i < flushResult; i++) {
                        mp3Data[i] = mMp3Buffer[i];
                    }
                    mp3Datas.add(mp3Data);
                    record_listener.onRecording(0);
                }
//                Log.v("The DOS available:", "::" + audioFile.length());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != dos) {
                    try {
                        dos.close();
                        dos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != accessfile) {
                    try {
                        accessfile.close();
                        accessfile = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //�ر�mp3ת��
                LameUtils.close();
                //¼���߳̽���
                isRecordThreadIng = false;

            }
            return true;
        }

        // �������淽���е���publishProgressʱ���÷�������,�÷�����UI�߳��б�ִ��
        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Boolean result) {
            Log.i(TAG, "onPostExecute: " +isStopForUser+" time:"+isStopForTime);

            Log.i(TAG, "onPostExecute: "+isRecording);
            if (isStopForUser == false && isStopForTime && result) {
                record_listener.onRecordFinish(mp3Datas);
            } else {
                //�ص��ӿ�
                if (!result && record_listener != null) {
                    record_listener.onRecordError();
                }
            }
            isRecording = false;
            isStopForUser = false;
            isStopForTime = false;
        }

        protected void onPreExecute() {
            // ("����¼��");
            record_listener.onRecordBefore();
        }

    }

    /**
     * ����¼��
     *
     * @author zhuyue
     */
    class PlayTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            isPlayRecordThreadIng = true;
            isPlaying = true;
            AudioTrack track = null;
            int bufferSize = AudioTrack.getMinBufferSize(frequence,
                    channelConfig, audioEncoding);
            short[] buffer = new short[bufferSize / 4];
            try {
                // ����Ƶд�뵽AudioTrack���У�ʵ�ֲ���
                DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(audioFile)));

                track = new AudioTrack(AudioManager.STREAM_MUSIC,
                        frequence, channelConfig, audioEncoding, bufferSize,
                        AudioTrack.MODE_STREAM);
                // ��ʼ����
                track.play();
                // ����AudioTrack���ŵ�������������Ҫһ�߲���һ�߶�ȡ

                while (!isStopPlayForUser && dis.available() > 0) {
                    int i = 0;

                    while (dis.available() > 0 && i < buffer.length) {
                        buffer[i] = dis.readShort();
                        i++;
                    }
                    // Ȼ������д�뵽AudioTrack��
                    track.write(buffer, 0, buffer.length);
                    if (isStopPlayForUser){
                        break;
                    }
                    play_listener.onPlayRecording();
                }

                // ���Ž���
                track.stop();
                dis.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (track != null) {
                    track.release();
                    isPlayRecordThreadIng = false;
                }
            }
            isPlayRecordThreadIng = false;
            return null;
        }

        protected void onPostExecute(Void result) {
            player = null;
            isPlaying = false;
            isStopPlayForUser = false;
            play_listener.onPlayRecordFinish();
        }

        protected void onPreExecute() {
            play_listener.onPlayRecordBefore();// �ڲ���¼��֮ǰ����
        }

    }

    /**
     * ����ת��
     *
     * @param data
     * @param items
     * @return
     */
    public short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    /**
     * ¼���ص��ӿ�
     *
     * @author zhuyue
     */
    public interface OnRecordListener {
        void onRecordBefore();// ��¼��֮ǰ����

        void onRecording(long volume);// ��¼�������лص�

        void onRecordFinish(List<byte[]> mp3Datas);// ¼����������ã�����¼�ƺõ��ļ���С

        void onRecordError();

    }

    /**
     * ����¼���ص��ӿ�
     *
     * @author zhuyue
     */
    public interface OnPlayRecordListener {
        void onPlayRecordBefore();// �ڲ���¼��֮ǰ����

        void onPlayRecording();// �ڲ���¼�������лص�

        void onPlayRecordFinish();// ����¼�����������
        //����¼����������
        void onPlayRecordError(int errorCode, String errorInfo);
    }

    /**
     * �����ļ�
     *
     * @param testid path
     * @return
     */
    public void CreateFile(String testid) {

        testid = testid.split("\\.")[0];

        try {
            audioFile = new File(getPath(true) + testid + ".pcm");
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioFile.mkdirs();

        if (!audioFile.exists()) {
            try {
                audioFile.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            audioFile.delete();
            try {
                audioFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ����������С
     * �˼��㷽������samsung��������
     *
     * @param buffer
     * @param readSize
     */
    private int calculateRealVolume(short[] buffer, int readSize) {
        int mVolume =0;
        int sum = 0;
        for (int i = 0; i < readSize; i++) {
            sum += buffer[i] * buffer[i];
        }
        if (readSize > 0) {
            double amplitude = sum / readSize;
            mVolume = (int) Math.sqrt(amplitude);
        }
        return mVolume;
    };


    /**
     * ��ȡ�ļ�����
     *
     * @param testid t
     * @return s
     */
    public long GetFileLengght(String testid) {

        testid = testid.split("\\.")[0];
        if (TextUtils.isEmpty(getPath(true) )){
            return 0;
        }
        audioFile = new File(getPath(true) + testid + ".pcm");

        return audioFile.length();

    }

    /**
     * �ж�sd���Ƿ����
     *
     * @return true ����
     */
    public boolean isSDCard() {
        return Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static byte[] shortArray2ByteArray(short[] data) {
        byte[] retVal = new byte[data.length * 2];
        for (int i = 0; i < retVal.length; i++) {
            int mod = i % 2;
            if (mod == 0) {
                retVal[i] = (byte) (data[i / 2]);
            } else {
                retVal[i] = (byte) (data[i / 2] >> 8);
            }
        }
        return retVal;
    }

    /**
     * byte������short����ת��
     *
     * @param data
     * @return
     */
    public static short[] byteArray2ShortArray(byte[] data) {
        if (data == null) {
            return null;
        }
        short[] retVal = new short[data.length / 2];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
        }
        return retVal;
    }

    /**
     * ��16λ��shortת����byte����
     *
     * @param s short
     * @return byte[] ����Ϊ2
     */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * ���������ֽڵ�ת��
     *
     * @param number ������
     * @return ��λ���ֽ�����
     */
    public byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// �����λ���������λ
            temp = temp >> 8; // ������8λ
        }
        return b;
    }

    /**
     * �������ֽ�ת��
     *
     * @param i
     * @return
     */
    public byte[] intToByte(int i) {
        byte[] bt = new byte[4];
        bt[3] = (byte) (0xff & i);
        bt[2] = (byte) ((0xff00 & i) >> 8);
        bt[1] = (byte) ((0xff0000 & i) >> 16);
        bt[0] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    /**
     * �ϲ�����byte����
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        //�ǿ��ж�
        if (byte_1==null){
            return byte_2;
        }
        if (byte_2==null){
            return byte_1;
        }

        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    /**
     * ��16λ��shortת����byte����
     *
     * @param s short
     * @return byte[] ����Ϊ2
     */
    public static byte[] shortToByteArray2(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }


    /**
     * �ļ�ת������
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] fileTobytes(final File file) {
        byte[] data = null;
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                int length = fileInputStream.available();
                data = new byte[length];
                fileInputStream.read(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fileInputStream) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return data;
    }


    /*
     * ¼���ϲ���һ��¼���ļ������ڲ���
     */
    public void MergePCMFile(String testid) {
        String fileName = testid.split("\\.")[0];
        if (TextUtils.isEmpty(getPath(true))){
            return;
        }
        //��ȡtestid��ͷ�ļ�����
        File[] files = FindFiles(fileName);
        String[] filesrc = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getAbsolutePath().equals(getPath(true) + fileName + ".pcm")) {
                filesrc[i] = files[i].getAbsolutePath();
            } else {
                filesrc[i] = null;
            }
        }
        //ֻ��һ��¼�������ֺ�testid��׼���ظ�,�����и���
        if (filesrc.length==1&&filesrc[0]==null){
            return;
        }

    }

    public boolean isPlaying(){
        return isPlaying;
    }


}
