package com.example.smart_wb

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi


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
                    Log.d("qqq", phoneNo + "currentNumber")
                    Log.d("qqq", "통화벨 울리는중")
                    Log.d("Qqq", "onReceive: 현재 내 컨텍스트:"+context)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            telephonyManager.endCall()
                            Log.d("2", "onReceive: 전화끊기, 거절")

                        }

                    // telephonyManager.acceptRingingCall(); 전화 받기 함수이다.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    telephonyManager.endCall(); 전화 끊기, 거절 함수이다.
//                }
                } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    Log.d("qqq", "통화중")
                } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    Log.d("qqq", "통화종료 혹은 통화벨 종료")
                }
                Log.d("qqq", "phone state : $state")
                //                Log.d("qqq", "phone currentPhonestate : " + currentPhoneState);
            }
        }
    }
}


//TelephonyManager.EXTRA_STATE_IDLE: 통화종료 혹은 통화벨 종료
//
//        TelephonyManager.EXTRA_STATE_RINGING: 통화벨 울리는중
//
//        TelephonyManager.EXTRA_STATE_OFFHOOK: 통화중

