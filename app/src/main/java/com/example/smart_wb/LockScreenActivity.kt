package com.example.smart_wb

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * 20/05/31 yama 잠금화면 액티비티
 * */
class LockScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        //초기 바텀네비게이션 세팅
        supportFragmentManager.beginTransaction().replace(R.id.frame, FragmentMainTimer()).commit()
        Log.d("락스크린액티비티", "onCreate: 여기로들어와지나")

    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        Log.d(TAG, "onBackPressed: 뒤로가기 버튼 제어")
    }
}