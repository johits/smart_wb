package com.example.smart_wb

import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_lock_screen.*

/**
 * 20/05/31 yama 잠금화면 액티비티
 * */
class LockScreenActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)



        //노티피 초기화
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //방해금지모드작동
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)


        Log.d("락스크린액티비티", "onCreate: 여기로들어와지나")
        startService(Intent(this,DrawService::class.java))
        finish()
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        Log.d(TAG, "onBackPressed: 뒤로가기 버튼 제어")
    }
}