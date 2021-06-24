package com.example.smart_wb.View_Controller.Receiver

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.smart_wb.Model.Shared.TimerSetShared


/**
2021-06-03
joker
전화 수신 차단
*/


//전화 중이면 전화를 받고 아니면 메세지
class CallReceiver : BroadcastReceiver() {
    //현재 전화가 오는지 받는지 끊는지 기본인지
    var phonestate: String? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {


        //최상단 액티비티 이름 구하기
        val AM =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val Info = AM.getRunningTasks(1)
        val topActivity = Info[0].topActivity
        val activityName = topActivity!!.shortClassName.substring(1)
        Log.d("CallReceiver", "현재 보고 있는 액티비티: $activityName")



        if (intent.action == "android.intent.action.PHONE_STATE") {
            val telephonyManager =
                context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val extras = intent.extras
            if (extras != null) {
                val state =
                    extras.getString(TelephonyManager.EXTRA_STATE) // 현재 폰 상태 가져옴
                phonestate = if (state == phonestate) {
                    return
                } else {
                    state
                }
                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    val phoneNo =
                        extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    Log.d("CallReceiver", phoneNo + "currentNumber")
                    Log.d("CallReceiver", "통화벨 울리는중")

                    if(phoneNo!=null){
                        //통화벨 종료 부재중전화 더하기
                        TimerSetShared.sumMissedCall(context)
                        Log.d("CallReceiver", "부재중전화 수:${TimerSetShared.getMissedCall(context)}")
                    }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if(activityName.equals("View_Controller.Activity.LockScreenActivity")){
                                telephonyManager.endCall()
                                Log.d("CallReceiver", "전화끊기, 거절")
                                Log.d("CallReceiver", "보고 있는 액티비티:"+activityName)
                            }
                        }

                } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    Log.d("CallReceiver", "통화종료 혹은 통화벨 종료")
                    Log.d("전화", "onReceive: 현재 액티비티 네임 $activityName")
                }
                Log.d("CallReceiver", "phone state : $state")
            }
        }
    }
}

