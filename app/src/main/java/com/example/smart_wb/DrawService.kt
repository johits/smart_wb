package com.example.smart_wb

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Button

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


    fun callEvent(){


        val inflate =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params =
            WindowManager.LayoutParams( /*ViewGroup.LayoutParams.MATCH_PARENT*/
                ViewGroup.LayoutParams.MATCH_PARENT,  //                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )

        params.gravity = Gravity.LEFT or Gravity.TOP
        mView = inflate.inflate(R.layout.fragment_main_timer, null)
//        val textView = mView!!.findViewById<View>(R.id.textView) as TextView
        val bt = mView!!.findViewById<View>(R.id.start) as Button
//        val end =
//            mView!!.findViewById<View>(R.id.end) as Button
        bt.setText("종료")
        bt.setOnClickListener{
            Log.d("DrawService", "시작 버튼 누름")
            Log.d("DrawService", "시작->종료 버튼으로 변경됨")
            if(bt.text.toString().equals("종료")){
                val intent = Intent(applicationContext, MainActivity::class.java)
                Log.d("마이서비스", "onClick: 앱으로 이동")
                stopService(
                    Intent(
                        applicationContext,
                        DrawService::class.java
                    )
                )
                Log.d("마이서비스", "onClick: 서비스 종료실행")
                startActivity(intent)
                Log.d("마이서비스", "onClick: 이동 완료")
            }
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