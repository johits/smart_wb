package com.example.smart_wb

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_permission2.*

/**2021-06-02
joker
방해 금지 권한 받기*/

// 권한 정의
private val ACTION_NOTIFICATION_POLICY_ACCESS_REQUEST_CODE: Int = 1

class Permission2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission2)

        NotifiPermission()
        Log.d(TAG, "onCreate: 노티피 퍼미션 메서드 실행됨")
    }


    fun NotifiPermission(){
        //권한 요청
        Log.d(TAG, "NotifiPermission: 방해모드 설정 권한요청")


        //권한 요청

        //권한 요청
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            Log.d(TAG, "NotifiPermission: 허용 안 되었다면")
            val intent =
                Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

            pbt2.setOnClickListener() {
                startActivity(intent)
            }
        }else{
            Log.d(TAG, "NotifiPermission: 이미 허용 되었다면")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }



}