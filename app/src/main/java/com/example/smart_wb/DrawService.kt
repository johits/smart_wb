package com.example.smart_wb

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.*
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.smart_wb.Model.Calculator
import com.example.smart_wb.Model.RemainTime
import com.example.smart_wb.Shared.PointItemShared
import com.example.smart_wb.Shared.TimerSetShared
import java.text.SimpleDateFormat
import java.util.*


/**2021-06-01
joker
다른 앱 위에 표시되는 앱 권한 받기
타이머 동작
 */


class DrawService : Service() {

    companion object {
        //액티비티와 서비스간 데이터전달 위한 상수값
        const val MSG_REGISTER_CLIENT = 1
        const val MSG_UNREGISTER_CLIENT = 3
        const val MSG_SEND_TO_SERVICE = 3
        const val MSG_SEND_TO_ACTIVITY = 4

        const val timerDelay: Long = 10 //타이머 속도 상수값
    }

    private var mClient: Messenger? = null //activity 에서 가져온 메신저

    private val TAG = "DrawService"
    var wm: WindowManager? = null
    var mView: View? = null

    //타이머 동작 위한 핸들러, 쓰레드
    var handler: Handler? = null
    var thread: Thread? = null

    //설정시간
    var settingTime = 0

    val calculator : Calculator = Calculator() //시간변환기
    private lateinit var remainTime :RemainTime //남은시간계산용변수선언
    //doze모드 방지 변수
    lateinit var powerManager: PowerManager
    lateinit var wakeLock: PowerManager.WakeLock

    override fun onBind(p0: Intent?): IBinder {
//        throw UnsupportedOperationException("Not yet")
        return mMessenger.binder
    }

    @SuppressLint("WakelockTimeout")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callEvent()

        //절전모드 안 들어가게 함
        //noDoze()
        
//        val calculator = Calculator()
//        Log.d(TAG, "기존 남은시간: ${calculator.calRemainTime(this)}")
        val time = TimerSetShared.getTime(this)
        val date = TimerSetShared.getDate(this)
        val setTime = TimerSetShared.getSettingTime(this)
        remainTime = RemainTime(time,date,setTime)
        Log.d(TAG, "개선된 남은시간:${remainTime.calRemainTime()}")
        //타이머 시작
        handler = Handler()
        thread = StartTimer()
        handler?.post(thread as StartTimer)

        return Service.START_STICKY //START_STICKY 서비스가 죽어도 시스템에서 재생성
    }

    //Doze모드(절전) 방지
    fun noDoze(){
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyApp::smart_wb_tag"
        )
        wakeLock.acquire()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun callEvent() {

        val inflate =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        //oreo 이전 이후 플래그값 구별헤야 한다.
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params =
            WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )

        params.gravity = Gravity.LEFT or Gravity.TOP
        mView = inflate.inflate(R.layout.activity_lock_screen, null)

        val l_back = mView!!.findViewById<View>(R.id.l_back) as ImageView
        val l_timer = mView!!.findViewById<View>(R.id.l_timer) as ImageView

        //쉐어드 불러오기 (배경, 타이머 아이템 적용)
        l_back.setImageResource(PointItemShared.getBg(this))
        l_timer.setImageResource(PointItemShared.getTimer(this))

        val bt = mView!!.findViewById<View>(R.id.btStop) as Button
        bt.setText("종료")
        bt.setOnClickListener {
            settingTime = -3
            Log.d(TAG, "종료버튼 클릭")
            // drawServiceStop(false)

        }
        wm!!.addView(mView, params)
    }

//    fun startForegroundService() {
//            val CHANNEL_ID = "snwodeer_service_channel"
//        if (Build.VERSION.SDK_INT >= 26) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "SnowDeer Service Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
//                .createNotificationChannel(channel)
//        }
//
////        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
////            .setContentTitle("앱")
////            .setContentIntent(pendingIntent)
//            var builder = NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle("서비스 동작중")
//                .setContentText("사랑한다")
//                .setAutoCancel(true) //터치시 노티 지우기
//                .setContentIntent(PendingIntent.getActivity(this, 0, Intent(), 0)) //setAutoCancel 동작안해서
//                .setPriority(NotificationCompat.PRIORITY_MAX) //오레오 이하 버전에서는 high 이상이어야 헤드업 알림
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//잠금화면에서 보여주기
//
//            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//
//            //알림 상태 확인
////            val notificationManager = NotificationManagerCompat.from(this)
////            notificationManager.notify(1, builder.build())
//
//            startForeground(1, builder.build())
//    } // startForegroundService()..


    //액티비티 호출 및 서비스 종료 위한 메세지 보냄
    @RequiresApi(Build.VERSION_CODES.M)
    fun drawServiceStop(result: Boolean) {
        try {
            val watch = mView!!.findViewById<View>(R.id.tvWatch) as TextView
            watch.visibility = View.GONE
            val btStop = mView!!.findViewById<View>(R.id.btStop) as Button
            btStop.visibility = View.GONE
        } catch (e: KotlinNullPointerException) {
        }
        //노티피 초기화
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //방해금지모드 해제
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)

        sendMsgToActivity(result);//액티비티에 메세지보내기//result true == 성공, false == 종료버튼터치
        stopService(Intent(applicationContext, DrawService::class.java))

        wakeLock.release()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        Log.d(TAG, "onUnbind: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "드로우서비스 onDestroy: ")
        if (wm != null) {
            if (mView != null) {
                wm!!.removeView(mView)
                mView = null
            }
            wm = null

        }
    }

    //타이머쓰레드 이너클래스
    inner class StartTimer : Thread() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun run() {
            val watch = mView!!.findViewById<View>(R.id.tvWatch) as TextView
            if (settingTime >= 0) { //설정시간(초)이 0보다 클떄 동작
                if (settingTime == 0) {
//                    watch.text = calTime(settingTime)
                    handler?.postDelayed(this, 100)//액티비티와 서비스 연결 위한 딜레이
                    settingTime-- //스레드가 동작할 때마다 1초씩 빼준다
                } else {
                    settingTime=remainTime.calRemainTime()
//                    settingTime--
                    watch.text = calculator.changeTime(settingTime) //초->시간 변환되서 표시//ex 3660->01시01분
                    handler?.postDelayed(this, Companion.timerDelay)
                }
//                Log.d(TAG, "남은시간:$settingTime")
            } else if (settingTime == -1) {
                watch.text = "00초"
                settingTime--
                handler?.postDelayed(this, 500)
            } else if (settingTime == -2) {//스크린타임 정상적으로 종료
                Log.d(TAG, "스크린타임 성공")
                drawServiceStop(true)
            } else if (settingTime == -3) {//사용자가 종료버튼 눌렀을때
                Log.d(TAG, "스크린타임 강제종료")
                drawServiceStop(false)
            }else{
                Log.d(TAG, "스크린타임 성공")
                drawServiceStop(true)
            }
        }
    }

    //남은시간 계산기 //남은시간 리턴
    //시작시간+설정시간=종료시간
    //종료시간-현재시간=남은시간
    //남은시간 양수 스크린타임 계속
    //남은시간 0or음수 스크린타임 이미 종료
    //날짜가 바뀌면 보정을 해야한다. 어떻게?
//    @SuppressLint("SimpleDateFormat")
//    private fun calRemainTime(): Int {
//        var result = 0
//        val timeStamp = System.currentTimeMillis()
//        // 현재 시간을 Date 타입으로 변환
//        val dateType = Date(timeStamp)
//        // 날짜, 시간을 가져오고 싶은 형태 선언
//        val dateFormatDate = SimpleDateFormat("yyyy-MM-dd")
//        val dateFormatTime = SimpleDateFormat("HH:mm:ss")
//        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
//        val nowDate: String = dateFormatDate.format(dateType) //현재 년 월 일
//        val nowTime: Int = calSec(dateFormatTime.format(dateType))//현재시간
//        val startTime: Int = calSec(TimerSetShared.getTime(this)) //시작시간
//        val settingTime: Int = TimerSetShared.getSettingTime(this)//설정시간
//        var endTime = startTime + settingTime// 종료시간
//
//        //종료시간이 하루가 지난 상황 보정
//        if (endTime > 86400) {
//            if (nowDate.equals(TimerSetShared.getDate(this))) {
//                result = endTime - nowTime
//            } else {
//                result = endTime - nowTime - 86400//보정필요하다
//            }
//        } else {
//            result = endTime - nowTime//남은시간
//        }
//
//        return result
//    }
//
//    //시간 -> 초 변환 //String->Int //ex 01:01:00 -> 3660
//    private fun calSec(time: String): Int {
//        val parts = time.split(":").toTypedArray()
//        val hour: Int = parts[0].toInt()
//        val min: Int = parts[1].toInt()
//        val sec: Int = parts[2].toInt()
//        return hour * 3600 + min * 60 + sec
//    }
//
//    //시간변환기
//    @RequiresApi(Build.VERSION_CODES.N)
//    private fun changeTime(setTime: Int): String? {
//        val result: String?
//        val hour = Math.floorDiv(setTime, 3600)
//        val min = Math.floorMod(setTime, 3600) / 60
//        val sec = Math.floorMod(setTime, 3600) % 60
//
//        if (hour > 0) {//1시간 초과 남았을떄 ex 02시22분
//            result = "%1$02d시 %2$02d분".format(hour, min)
//        } else if (hour == 0 && min > 0) { //1시간 이하 남았을 때 ex 22분22초
//            result = "%1$02d분 %2$02d초".format(min, sec)
//        } else if (hour == 0 && min == 0) { // 1분 이하 남았을 때 ex 22초
//            result = "%1$02d초".format(sec)
//        } else { //리턴값 있어서 else 넣어야 한다. ex 22초
//            result = "%1$02d초".format(sec)
//        }
//        return result
//    }

    //     activity로부터 binding 된 Messenger //메시지 받음
    private val mMessenger = Messenger(Handler { msg ->
        Log.d("tag", " message what : " + msg.what + " , msg.obj " + msg.obj)
        settingTime = msg.obj as Int
        when (msg.what) {
            MSG_REGISTER_CLIENT -> mClient = msg.replyTo // activity로부터 가져온
        }
        false
    })

    //activity에 메세지 보냄// result true == 성공, false == 종료버튼터치
    private fun sendMsgToActivity(result: Boolean) {
        try {
            Log.d(TAG, "결과" + result)
            val bundle = Bundle()
            bundle.putBoolean("result", result)
            bundle.putString("message", "finish")
            val msg: Message = Message.obtain(null, MSG_SEND_TO_ACTIVITY)
            msg.setData(bundle)
            mClient!!.send(msg) // msg 보내기
        } catch (e: RemoteException) {
        }
    }
}


