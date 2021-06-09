package com.example.smart_wb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class Restart : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//        val i = Intent(context, DrawService::class.java)
        val i = Intent(context, LockScreenActivity::class.java)
        i.putExtra("restart", true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(i)
            context.startActivity(i)
            Log.d("Restart", "onReceive: 재실행")
        } else {
            context.startActivity(i)
//            context.startService(i)
        }
    }
}