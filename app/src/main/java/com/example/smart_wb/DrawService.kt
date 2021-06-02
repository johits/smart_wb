package com.example.smart_wb

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.*
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer


/**2021-06-01
joker
다른 앱 위에 표시되는 앱 권한 받기
 타이머 동작
 */


class DrawService : Service() {

    companion object {
        const val MSG_REGISTER_CLIENT = 1
        const val MSG_UNREGISTER_CLIENT =3
        const val MSG_SEND_TO_SERVICE = 3
        const val MSG_SEND_TO_ACTIVITY = 4
    }
    private var mClient: Messenger? = null //activity 에서 가져온 메신저

    private val TAG = "DrawService"
    var wm: WindowManager? = null
    var mView: View? = null

    var handler: Handler? = null
    var thread : Thread?= null

    var settingTime =0
    override fun onBind(p0: Intent?): IBinder {
//        throw UnsupportedOperationException("Not yet")
        return mMessenger.binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callEvent()
        handler = Handler()
        thread = StartTimer()
        handler?.post(thread as StartTimer)
        return Service.START_STICKY
    }


    fun callEvent() {

        val inflate =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params =
            WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )

        params.gravity = Gravity.LEFT or Gravity.TOP
        mView = inflate.inflate(R.layout.activity_lock_screen, null)

        val bt = mView!!.findViewById<View>(R.id.btStop) as Button
        bt.setText("종료")
        bt.setOnClickListener {
            settingTime=-1
            Log.d(TAG, "종료버튼 클릭")
            drawServiceStop(false)
        }
        wm!!.addView(mView, params)
    }

    //액티비티 호출 및 서비스 종료 위한 메세지 보냄
    fun drawServiceStop(result:Boolean){
        try {
            val watch = mView!!.findViewById<View>(R.id.tvWatch) as TextView
            watch.visibility = View.GONE
            val btStop = mView!!.findViewById<View>(R.id.btStop) as Button
            btStop.visibility = View.GONE
        }catch (e:KotlinNullPointerException){
        }
        sendMsgToActivity(result);
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        stopService(Intent(applicationContext, DrawService::class.java))
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
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
            if(settingTime>=0){
                    val watch = mView!!.findViewById<View>(R.id.tvWatch) as TextView
                if(settingTime==0){
//                    val btStop = mView!!.findViewById<View>(R.id.btStop) as Button
//                    btStop.visibility
//                    watch.text = "00:00"
                    handler?.postDelayed(this,500)
                }else{
                    watch.text = calTime(settingTime)
                    handler?.postDelayed(this, 1000)
                }
                    settingTime--
                    Log.d(TAG, "settingTime:" + settingTime)
            }else{
                Log.d(TAG, "타이머종료")
                drawServiceStop(true)
            }
        }
    }
    //시간변환기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun calTime(setTime: Int): String? {
        var result: String?
        val hour = Math.floorDiv(setTime, 3600)
        val min = Math.floorMod(setTime, 3600) / 60
        val sec = Math.floorMod(setTime, 3600) % 60

        if (hour > 0) {
            if (min < 10 && sec < 10) {
                result = "${hour}:0${min}:0${sec}"
            }else if(min<10&&sec>10){
                result = "${hour}:0${min}:${sec}"
            }else if(min>10&&sec<10){
                result = "${hour}:${min}:0${sec}"
            }else{
                result = "${hour}:${min}:${sec}"
            }
        } else {
//            Log.d("tag", "시간0")
            if (min < 10 && sec < 10) {
                result = "0${min}:0${sec}"
            }else if(min<10&&sec>10){
                result = "0${min}:${sec}"
            }else if(min>10&&sec<10){
                result = "${min}:0${sec}"
            }else{
                result = "${min}:${sec}"
            }
        }

        return result
    }

//     activity로부터 binding 된 Messenger
    private val mMessenger = Messenger(Handler { msg ->
        Log.d("tag", " message what : " + msg.what + " , msg.obj " + msg.obj)
        settingTime= msg.obj as Int
        when (msg.what) {
            MSG_REGISTER_CLIENT -> mClient = msg.replyTo // activity로부터 가져온
        }
        false
    })

    //activity에 메세지 보냄
    private fun sendMsgToActivity(sendValue: Boolean) {
        try {
            val bundle = Bundle()
            bundle.putBoolean("result", sendValue)
            bundle.putString("message", "finish")
            val msg: Message = Message.obtain(null, MSG_SEND_TO_ACTIVITY)
            msg.setData(bundle)
            mClient!!.send(msg) // msg 보내기
        } catch (e: RemoteException) {
        }
    }

}