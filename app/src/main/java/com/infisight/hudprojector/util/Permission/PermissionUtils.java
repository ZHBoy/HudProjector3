package com.infisight.hudprojector.util.Permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.util.ToastUtil;

/**
 * @作者 hao
 * @创建日期 2018/1/18 16:32
 * Description: 权限检测
 */

public class PermissionUtils {

    //For API 23+ you need to request the read/write permissions even if they are already in your manifest.

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static final int REQUEST_EXTERNAL_RECORD = 2;

    private static final int REQUEST_EXTERNAL_CAMERA_STORAGE = 3;

    private static final int REQUEST_LOCATION= 4;

    /**
     * 文件读取权限
     *
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        boolean isPermission = true;
        for (String permissionName : Permission.STORAGE){
            int permission = ActivityCompat.checkSelfPermission(activity, permissionName);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        Permission.STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
                isPermission = false;
            }
        }
        return isPermission;
    }

    /**
     * 视频拍摄
     * @param activity
     * @return true权限都申请成功
     */
    public static boolean verifyRecordPermissions(Activity activity) {
        boolean isPermission = true;
        for (String permissionName : Permission.RECORD){
            if (permissionName == null)break;
            int permission = ActivityCompat.checkSelfPermission(activity, permissionName);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        Permission.RECORD,
                        REQUEST_EXTERNAL_RECORD
                );
                isPermission = false;
            }
        }
        if (isPermission) return isPermission;//说明权限都开启了
        for (String permissionName : Permission.RECORD){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)){
                //之前点击了“不再询问”，无法再次弹出权限申请框。
                //可以给Toast提示，或者Dialog反馈给用户，引导去开启相应权限
                ToastUtil.show(activity,activity.getResources().getString(R.string.permission_error));
                isPermission = false;
                break;
            }
        }
        return isPermission;
    }

    /**
     * 拍摄、相册选择
     * @param activity
     * @return true权限都申请成功
     */
    public static boolean verifyCameraAndStoragePermissions(Activity activity) {
        boolean isPermission = true;
        for (String permissionName : Permission.CAMERA_STORAGE){
            int permission = ActivityCompat.checkSelfPermission(activity, permissionName);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        Permission.CAMERA_STORAGE,
                        REQUEST_EXTERNAL_CAMERA_STORAGE
                );
                isPermission = false;
            }
        }
        if (isPermission) return isPermission;//说明权限都开启了
        for (String permissionName : Permission.CAMERA_STORAGE){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)){
                //之前点击了“不再询问”，无法再次弹出权限申请框。
                //可以给Toast提示，或者Dialog反馈给用户，引导去开启相应权限
                ToastUtil.show(activity,activity.getResources().getString(R.string.permission_error));
                isPermission = false;
                break;
            }
        }
        return isPermission;
    }

    /**
     * 定位权限
     * @param activity
     * @return true权限都申请成功
     */
    public static boolean verifyLocationPermissions(Activity activity) {
        boolean isPermission = true;
        for (String permissionName : Permission.LOCATION){
            int permission = ActivityCompat.checkSelfPermission(activity, permissionName);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        Permission.LOCATION,
                        REQUEST_LOCATION
                );
                isPermission = false;
            }
        }
        if (isPermission) return isPermission;//说明权限都开启了
        for (String permissionName : Permission.LOCATION){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)){
                //之前点击了“不再询问”，无法再次弹出权限申请框。
                //可以给Toast提示，或者Dialog反馈给用户，引导去开启相应权限
                ToastUtil.show(activity,activity.getResources().getString(R.string.permission_error));
                isPermission = false;
                break;
            }
        }
        return isPermission;
    }
}
