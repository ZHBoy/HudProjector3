package com.infisight.hudprojector.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.infisight.hudprojector.service.BackgroundRecorder;

/**
 * Created by Administrator on 2015/9/21.
 * 当电量小于5%时自动关闭录像
 */
public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
            Toast.makeText(context, "电量过低，请尽快充电！", Toast.LENGTH_LONG).show();
        }
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            // 获取当前电量
            int current = bundle.getInt("level");
            // 获取总电量
            int total = bundle.getInt("scale");
            StringBuffer sb = new StringBuffer();
            // 如果当前电量小于总电量的5%
            if (current * 1.0 / total < 0.04) {
                Intent service=new Intent(context, BackgroundRecorder.class);
                context.stopService(service);
                Toast.makeText(context,"电量过低，行车记录仪已关闭",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
