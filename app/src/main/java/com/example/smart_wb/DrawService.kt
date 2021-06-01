package com.example.smart_wb

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView

/**2021-06-01
joker
다른 앱 위에 표시되는 앱 권한 받기*/


class DrawService : Service() {
    private val TAG = "DrawService"
    var wm: WindowManager? = null
    var mView: View? = null

    var handler: Handler? = null
    var flag : Boolean= true

    override fun onBind(p0: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callEvent()
        handler = Handler()
        var thread = StartTimer()
        handler?.post(thread)
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
            Log.d(TAG, "종료버튼 클릭")
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            stopService(Intent(applicationContext, DrawService::class.java))
            startActivity(intent)
        }
        wm!!.addView(mView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wm != null) {
            if (mView != null) {
                wm!!.removeView(mView)
                mView = null
            }
            wm = null
            flag=false
        }
    }

    //타이머쓰레드 이너클래스
    inner class StartTimer : Thread() {
        override fun run() {
            if(flag){
                val watch = mView!!.findViewById<View>(R.id.tvWatch) as TextView
                watch.text = System.currentTimeMillis().toString()
                handler?.postDelayed(this, 1000)
            }
        }
    }

}