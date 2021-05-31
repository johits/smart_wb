package com.example.smart_wb

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import com.example.smart_wb.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_timer.*

private lateinit var binding: ActivityMainBinding


//2020-05-29 joker 메인 클래스 (프래그먼트 메뉴에 대한 코드)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
//        var start = findViewById(R.id.start) as Button
        // 생성된 뷰를 액티비티에 표시합니다.
        setContentView(binding.root)
        configureBottomNavigation()

    }
        private fun configureBottomNavigation(){
            xml_main_viewpaper.adapter = AdapterMainFragment(supportFragmentManager, 5)
            xml_main_tablayout.setupWithViewPager(xml_main_viewpaper)

            val viewBtmNaviMain : View = this.layoutInflater.inflate(R.layout.bottom_navigation, null, false)

            xml_main_tablayout.getTabAt(0)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_timer)  as RelativeLayout
            xml_main_tablayout.getTabAt(1)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_calendar)  as RelativeLayout
            xml_main_tablayout.getTabAt(2)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_chart)   as RelativeLayout
            xml_main_tablayout.getTabAt(3)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_item)     as RelativeLayout
            xml_main_tablayout.getTabAt(4)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_setting)  as RelativeLayout
        }



    override fun onBackPressed() {
        if (xml_main_viewpaper.currentItem == 0) {
            // 사용자가 첫 번째 페이지에서 뒤로가기 버튼을 누를 경우
            Log.d(TAG, "뒤로가기 방지")
        }
    }
}


