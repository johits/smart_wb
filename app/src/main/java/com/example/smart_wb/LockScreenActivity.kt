package com.example.smart_wb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LockScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        //초기 바텀네비게이션 세팅
        supportFragmentManager.beginTransaction().replace(R.id.frame, FragmentMainTimer()).commit()
    }
}