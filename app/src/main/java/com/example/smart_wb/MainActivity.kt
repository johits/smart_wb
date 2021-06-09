package com.example.smart_wb

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.smart_wb.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

private lateinit var binding: ActivityMainBinding
var dialog : Boolean= false

//2020-05-29 joker 메인 클래스 (프래그먼트 메뉴에 대한 코드)

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.hasExtra("dialog")) {
            dialog = intent.getBooleanExtra("dialog",false)
        }


        Log.d(TAG, "onCreate: 메인으로넘어옴")
        binding = ActivityMainBinding.inflate(layoutInflater)
//        var start = findViewById(R.id.start) as Button
        // 생성된 뷰를 액티비티에 표시합니다.
        setContentView(binding.root)
        configureBottomNavigation()

        //전화 제어 퍼미션 얻어오기
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                Manifest.permission.ANSWER_PHONE_CALLS
            ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.ANSWER_PHONE_CALLS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW
                ),
                1
            )
        }


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

            if(dialog){
                xml_main_viewpaper.setCurrentItem(3)
            }
        }



}
