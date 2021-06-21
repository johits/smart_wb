package com.example.smart_wb

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
import com.example.smart_wb.Controller.Shared.PointItemShared


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

        const val timerDelay: Long = 1000 //타이머 속도 상수값
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

    override fun onBind(p0: Intent?): IBinder {
//        throw UnsupportedOperationException("Not yet")
        return mMessenger.binder
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callEvent()

        //타이머 시작
        handler = Handler()
        thread = StartTimer()
        handler?.post(thread as StartTimer)

        return Service.START_STICKY
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
                } else {
                    watch.text = calTime(settingTime) //초->시간 변환되서 표시//ex 3660->01시01분
                    handler?.postDelayed(this, Companion.timerDelay)
                }
                settingTime-- //스레드가 동작할 때마다 1초씩 빼준다

            } else if (settingTime == -1) {
                watch.text="00초"
                settingTime--
                handler?.postDelayed(this, 500)
            } else if (settingTime == -2) {//스크린타임 정상적으로 종료
                Log.d(TAG, "스크린타임 성공")
                drawServiceStop(true)
            }else if (settingTime == -3) {//사용자가 종료버튼 눌렀을때
                Log.d(TAG, "스크린타임 강제종료")
                drawServiceStop(false)
            }
        }
    }

    //시간변환기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun calTime(setTime: Int): String? {
        val result: String?
        val hour = Math.floorDiv(setTime, 3600)
        val min = Math.floorMod(setTime, 3600) / 60
        val sec = Math.floorMod(setTime, 3600) % 60

        if (hour > 0) {//1시간 초과 남았을떄 ex 02시22분
            result = "%1$02d시 %2$02d분".format(hour, min)
        } else if (hour == 0 && min > 0) { //1시간 이하 남았을 때 ex 22분22초
            result = "%1$02d분 %2$02d초".format(min, sec)
        } else if (hour == 0 && min == 0) { // 1분 이하 남았을 때 ex 22초
            result = "%1$02d초".format(sec)
        } else { //리턴값 있어서 else 넣어야 한다. ex 22초
            result = "%1$02d초".format(sec)
        }
        return result
    }

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


