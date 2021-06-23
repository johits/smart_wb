package com.example.smart_wb.View_Controller.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.smart_wb.View_Controller.Activity.LockScreenActivity
import com.example.smart_wb.Model.Shared.TimerSetShared


class Restart : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        val i = Intent(context, DrawService::class.java)
        val settingTime = TimerSetShared.getSettingTime(context)
        val action = intent.getAction();
        if(action.equals("android.intent.action.BOOT_COMPLETED")) {
            if(settingTime==null){ //스크린타임 동작 x

            }else if(settingTime>0){ //스크린타임 동작 o
                val i = Intent(context, LockScreenActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("restart", true)
                context.startActivity(i);

            }
        }
//        val i = Intent(context, LockScreenActivity::class.java)
//        i.putExtra("restart", true)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            context.startForegroundService(i)
//            context.startActivity(i)
//            Log.d("Restart", "onReceive: 재실행")
//        } else {
//            context.startActivity(i)
////            context.startService(i)
//        }
    }
}