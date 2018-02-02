package com.infisight.hudprojector.util;

/**
 * Created by Administrator on 2015/9/17.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

public class RecordUtils {
    public static SoundPool soundPool;
    public static String innerSDcard = Environment
            .getExternalStorageDirectory().getPath();
    private SharedPreferences spf;

    public RecordUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取当前时间 format:yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 检测字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 检查设备是否有摄像头
     *
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * 格式化时间
     */
    public static String format(int i) {
        String s = i + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * 获取外置SD卡路径
     *
     * @return 应该就一条记录或空
     */
    public static List getExtSDCardPath(Context context) {
        List lResult = new ArrayList();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
            Log.i("getExtSDCardPath", lResult + "");
        } catch (Exception e) {
            Toast.makeText(context, "请插入SD卡", 0).show();
        }
        return lResult;
    }

    /**
     * 当内存卡容量少于1000M时，自动删除视频列表里面的第一个文件
     */
    public static void spaceNotEnoughDeleteTempFile(Context context) {
        Log.i("deleteFile", "startRunning");
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            // File path = Environment.getExternalStorageDirectory();
            String path = innerSDcard + "/Recorders/";
            StatFs statfs = new StatFs(path);
            // 获取block的SIZE
            long blocSize = statfs.getBlockSize();
            // 获取总的BLOCK数量
            long totalBlocks = statfs.getBlockCount();
            // 可使用的Block的数量
            long availaBlock = statfs.getAvailableBlocks();
            // 获取当前可用内存容量，单位：MB
            long sd = availaBlock * blocSize / 1024 / 1024;
            if (sd < 1000) {
                String filepath = innerSDcard + "/Recorders/temporary";
                File file = new File(filepath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File[] files = file.listFiles();
                if (files.length > 0) {
                    String dele = files[0] + "";
                    File file2 = new File(dele);
                    Log.i("deleteFile", file2 + "");
                    file2.delete();
                }
            }
        } else if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_REMOVED)) {
            Toast.makeText(context, "请插入内存卡", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字(是包名+服务的类名（例如：net.loonggg.testbackstage.TestService） )
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(200);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().toString().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 存储数据（SharedPreferences）
     *
     * @param context
     */
    public static void putData(Context context,boolean data) {
        SharedPreferences spf = context.getSharedPreferences(Constants.FILE_DATA, Context.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = spf.edit();
        //用putString的方法保存数据
        editor.putBoolean("isRecording",data);
        //提交当前数据
        editor.commit();
    }
    /**
     * 取得数据
     */
    public static boolean getData(Context context){
        SharedPreferences spf = context.getSharedPreferences(Constants.FILE_DATA, Context.MODE_PRIVATE);
        boolean data=spf.getBoolean("isRecording",false);
        return data;
    }

    /**
     * 输入一个秒数返回一个 时分秒的三位数组
     * @param secords
     * @return
     */
    public static int[] formateTime(int secords){
        int hour,min,secord;
        hour=secords/3600;
        min=secords/60;
        if(min>=60){
            min=min-60*hour;
        }
        secord=secords%60;
        int[] timeArray={hour,min,secord};
        return timeArray;
    }


}
