package com.zhouplus.plusreader.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;


/**
 * Created by zhouplus
 * Time at 2016/9/7
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class BatteryReceiver extends BroadcastReceiver {
    Handler handler;

    public BatteryReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            //获取当前电量
            int level = intent.getIntExtra("level", 0);
            //电量的总刻度
            int scale = intent.getIntExtra("scale", 100);
            //把它转成百分比
            float pf = (level * 100) / scale;
            int percent = Math.round(pf);

            int status = intent.getIntExtra("status", -1);
            Message msg = handler.obtainMessage();
            msg.what = 1;  //   ReadingActivity.MSG_BATTERY
            if (status != -1) {
                if (BatteryManager.BATTERY_STATUS_CHARGING == status) {
                    msg.obj = "充电中";
                    msg.arg1 = Color.GREEN;
                } else if (BatteryManager.BATTERY_STATUS_NOT_CHARGING == status) {
                    msg.obj = "电量：" + pf;
                    if (percent <= 30) {
                        msg.arg1 = Color.RED;
                    } else {
                        msg.arg1 = Color.BLACK;
                    }
                } else if (BatteryManager.BATTERY_STATUS_DISCHARGING == status) {
                    msg.obj = "电量：" + pf;
                    if (percent <= 30) {
                        msg.arg1 = Color.RED;
                    } else {
                        msg.arg1 = Color.BLACK;
                    }
                } else if (BatteryManager.BATTERY_STATUS_FULL == status) {
                    msg.obj = "电量：100%";
                    msg.arg1 = Color.BLACK;
                }
            } else {
                msg.obj = "未识别电池";
            }
            handler.sendMessage(msg);
        }
    }
}
