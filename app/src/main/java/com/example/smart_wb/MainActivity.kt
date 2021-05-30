package com.example.smart_wb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 2020-05-29 yama 메인액티비티
 * */


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //초기 바텀네비게이션 세팅
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, ScreenTimeFragment())
            .commit()

        transitonNavigationBottomView(findViewById(R.id.bottomnavigation), supportFragmentManager)
    }


    //프레그먼트 전환
    fun transitonNavigationBottomView(
        bottomView: BottomNavigationView,
        fragmentManager: FragmentManager
    ) {
        bottomView.setOnNavigationItemSelectedListener {
            it.isChecked = true
            when (it.itemId) {
                R.id.item_calender -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, CalenderFragment()).commit()
                }
                R.id.item_chart -> {
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, ChartFragment())
                        .commit()
                }
                R.id.item_screen_time -> {
                    fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, ScreenTimeFragment()).commit()
                }
                R.id.item_item -> {
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, ItemFragment())
                        .commit()
                }
                R.id.item_setting -> {
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, SettingFragment())
                        .commit()
                }
                else -> Log.d("test", "error") == 0
            }
            Log.d("test", "final") == 0
        }
    }
}