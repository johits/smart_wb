package com.example.smart_wb

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.Button
import androidx.annotation.RequiresApi

/**2021-06-01
joker
다른 앱 위에 표시되는 앱 권한 받기*/


class DrawService : Service() {

    var wm: WindowManager? = null
    var mView: View? = null


    override fun onBind(p0: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        callEvent()

        return Service.START_STICKY
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun callEvent(){

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
        bt.setOnClickListener{
            val intent = Intent(applicationContext, MainActivity::class.java)
            stopService(Intent(applicationContext, DrawService::class.java))


            //노티피 초기화
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //방해금지모드 해제
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)

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
        }
    }


}