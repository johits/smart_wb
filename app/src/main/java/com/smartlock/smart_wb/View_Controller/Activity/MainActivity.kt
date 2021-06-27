package com.smartlock.smart_wb.View_Controller.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.smartlock.smart_wb.View_Controller.Fragment.AdapterMainFragment
import com.smartlock.smart_wb.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//2020-05-29 joker 메인 클래스 (프래그먼트 메뉴에 대한 코드) .

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = "MainActivity"
    }

//    private var _binding: ActivityMainBinding? = null
//    private val binding get() = _binding!!
    private lateinit var stack: Stack<Int>//프래그먼트트 전환는 순서대로 스택에 푸시한다.

    private val adapter by lazy {
        AdapterMainFragment(
            supportFragmentManager,
            4
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ")

//        _binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        configureBottomNavigation()

        //스크린타임 결과에 따라 다이얼로그 생성
        if(intent.hasExtra("title")&&intent.hasExtra("setTime")){
            val title = intent.getStringExtra("title")
            val setTime = intent.getStringExtra("setTime")
            val flower = intent.getIntExtra("flower", 0)
            val missedCall = intent.getIntExtra("missedCall",0)

            if (title != null&&setTime != null) {
                    showDialog(title,setTime, flower,missedCall)
            }
        }

        stack = Stack<Int>()
        stack.push(0)//FragmentMainTimer 가 처음 화면 이므로 값을 넣는다.

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


        // 뷰페이저 어댑터 연결
        xml_main_viewpaper.adapter = MainActivity@ adapter

        xml_main_viewpaper.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                pushStack(position)

                xml_main_tablayout.getTabAt(0)?.setIcon(R.drawable.mtimer1)
                xml_main_tablayout.getTabAt(1)?.setIcon(R.drawable.mcalendar1)
                xml_main_tablayout.getTabAt(2)?.setIcon(R.drawable.mchart1)
                xml_main_tablayout.getTabAt(3)?.setIcon(R.drawable.mitem1)
//                xml_main_tablayout.getTabAt(4)?.setIcon(R.drawable.msetting1)


                when (position) {

                    0 -> xml_main_tablayout.getTabAt(0)?.setIcon(R.drawable.mtimer2)
                    1 -> xml_main_tablayout.getTabAt(1)?.setIcon(R.drawable.mcalendar2)
                    2 -> xml_main_tablayout.getTabAt(2)?.setIcon(R.drawable.mchart2)
                    3 -> xml_main_tablayout.getTabAt(3)?.setIcon(R.drawable.mitem2)
//                    4   ->    xml_main_tablayout.getTabAt(4)?.setIcon(R.drawable.msetting2)
                }


            }

        })

        // 탭 레이아웃에 뷰페이저 연결
        xml_main_tablayout.setupWithViewPager(xml_main_viewpaper)

        // 탭 레이아웃 초기화
        xml_main_tablayout.getTabAt(0)?.setIcon(R.drawable.mtimer2)
        xml_main_tablayout.getTabAt(1)?.setIcon(R.drawable.mcalendar1)
        xml_main_tablayout.getTabAt(2)?.setIcon(R.drawable.mchart1)
        xml_main_tablayout.getTabAt(3)?.setIcon(R.drawable.mitem1)
//        xml_main_tablayout.getTabAt(4)?.setIcon(R.drawable.msetting1)

    }

    //프래그먼트 화면 전환할 때 마다 스택에 포지션 값 푸쉬
    //백버튼 누르면 스택 팝 한다.
    //스택.peek() 으로 최상단 값 가져온다.
    //가져온 값으로 화면 전환.
    override fun onBackPressed() {
        stack.pop()
        if (stack.empty()) {   //스택에 값이 없으면 백버튼 정상 동작
            super.onBackPressed()
        } else { //스택에 값이 있으면 이전 프래그먼트로 전환
            Log.d(TAG, "스택 상단의 값 ${stack.peek()}")
            xml_main_viewpaper.setCurrentItem(stack.peek())//프래그먼트 전환
            stack.pop()
        }
    }

    //스택크기 제한한다.
    private fun pushStack(position:Int){
        if(stack.size<3){
            stack.push(position)
        }
    }


    //스크린타임 결과 다이얼로그
    @SuppressLint("SetTextI18n")
    private fun showDialog(title: String, setTime: String, flower: Int, missedCall: Int) {
        Log.d(LockScreenActivity.TAG, "showDialog: ")
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.success_dialog, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val tvFlower = view.findViewById<TextView>(R.id.tvFlower)
        val tvMissedCall = view.findViewById<TextView>(R.id.tvMissedCall)
        val tvSettingTime = view.findViewById<TextView>(R.id.tvSettingTime)

        tvTitle.text = title
        tvSettingTime.text = setTime //설정시간표시

        //알림 상태 확인
        val audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val alarm: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val rt: Ringtone = RingtoneManager.getRingtone(applicationContext, alarm)

        if(!title.equals("종료되었습니다.")){ //종료버튼 누른 게 아니라면 알림 소리 남
            Log.d(TAG, "showDialog: 여기로 들어와")
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                //소리 알람
                rt.play()
            } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                //진동 알람
                vib.vibrate(longArrayOf(500, 300, 500, 300), 0) //repeat: 0 = 무한반복 , -1 = 한번만 실행
            }
        }


        if (flower == 0) {
            tvFlower.text = "없음"
        } else {
            tvFlower.text = flower.toString() + "송이"//얻은 꽃 표시
        }
        if (missedCall == 0) {
            tvMissedCall.text = "없음"
        } else {
            tvMissedCall.text = missedCall.toString() + "통화"// 부재중 전화 표시
        }

        //확인버튼 클릭 이벤트
        btnConfirm.setOnClickListener {
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                //소리 알람
                rt.stop()
            } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                //진동 알람
                vib.cancel()
            }
            alertDialog!!.dismiss()
        }
        alertDialog!!.show()
    }

}
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
////        var start = findViewById(R.id.start) as Button
//        // 생성된 뷰를 액티비티에 표시합니다.
//        setContentView(binding.root)
//        configureBottomNavigation()
//
//
//        //전화 제어 퍼미션 얻어오기
//        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
//                Manifest.permission.ANSWER_PHONE_CALLS
//            ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
//                Manifest.permission.CALL_PHONE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this@MainActivity,
//                arrayOf(
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.ANSWER_PHONE_CALLS,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.CALL_PHONE,
//                    Manifest.permission.SYSTEM_ALERT_WINDOW
//                ),
//                1
//            )
//        }
//
//    }
//        @SuppressLint("ResourceType")
//        private fun configureBottomNavigation(){
//
//            xml_main_viewpaper.adapter = AdapterMainFragment(supportFragmentManager, 5)
//            xml_main_tablayout.setupWithViewPager(xml_main_viewpaper)
//
//            val viewBtmNaviMain : View = this.layoutInflater.inflate(R.layout.bottom_navigation, null, false)
//
//            xml_main_tablayout.getTabAt(0)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_timer)  as RelativeLayout
//            xml_main_tablayout.getTabAt(1)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_calendar)  as RelativeLayout
//            xml_main_tablayout.getTabAt(2)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_chart)   as RelativeLayout
//            xml_main_tablayout.getTabAt(3)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_item)     as RelativeLayout
//            xml_main_tablayout.getTabAt(4)!!.customView = viewBtmNaviMain.findViewById(R.id.xml_btmnv_btn_setting)  as RelativeLayout
//
//            xml_main_tablayout.setSelectedTabIndicatorColor(Color.parseColor("#2FA9FF"));
//
//
////            xml_main_tablayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
//
//
//        }
//
//
//
//
//}
//
//
//
