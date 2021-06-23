package com.example.smart_wb.View_Controller.Activity

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_wb.R
import kotlinx.android.synthetic.main.activity_permission2.*


/**2021-06-02
joker
방해 금지 권한 받기*/

// 권한 정의
private val ON_DO_NOT_DISTURB_CALLBACK_CODE: Int = 1

class Permission2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission2)

            requestMutePermissions()

        Log.d(TAG, "onCreate: 노티피 퍼미션 메서드 실행됨")
    }


    private fun requestMutePermissions() {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                Log.d(TAG, "onActivityResult: 테스트0")
                val audioManager =
                    applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            } else if (Build.VERSION.SDK_INT >= 23) {
                Log.d(TAG, "onActivityResult: 테스트00")
                requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp()
            }
        } catch (e: SecurityException) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // if user granted access else ask for permission
        if (notificationManager.isNotificationPolicyAccessGranted) {
            Log.d(TAG, "onActivityResult: 권한 허락했으면")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Log.d(TAG, "onActivityResult: 권한 허락 안 했으면")
            // Open Setting screen to ask for permisssion
            pbt2.setOnClickListener(){
                val intent =
                    Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivityForResult(intent,
                    ON_DO_NOT_DISTURB_CALLBACK_CODE
                )
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == ON_DO_NOT_DISTURB_CALLBACK_CODE) {
            requestForDoNotDisturbPermissionOrSetDoNotDisturbForApi23AndUp()
        }
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