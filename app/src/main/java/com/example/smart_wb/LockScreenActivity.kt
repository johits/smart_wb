package com.example.smart_wb

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smart_wb.DrawService.Companion.MSG_REGISTER_CLIENT
import com.example.smart_wb.databinding.ActivityLockScreenBinding
import kotlinx.android.synthetic.main.activity_lock_screen.*
import kotlinx.android.synthetic.main.activity_lock_screen.view.*

/**
 * 20/05/31 yama 잠금화면 액티비티
 * */
class LockScreenActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LockScreenActivity"
    }

    //서비스에 데이터 보내기 위한 변수
    private var mServiceMessenger: Messenger? = null
    private var mIsBound = false

    private var settingTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        if(intent.hasExtra("settingTime")){
             var time = intent.getStringExtra("settingTime")?.toInt()
            Log.d(TAG, "onCreate: "+time)
            if (time != null) {
                settingTime = time
            }
        }

        tvWatch.visibility = View.GONE
        btStop.visibility = View.GONE
        //초기 바텀네비게이션 세팅

        Log.d("락스크린액티비티", "onCreate: 여기로들어와지나")
//        startService(Intent(this,DrawService::class.java))
        setStartService()

    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        Log.d(TAG, "onBackPressed: 뒤로가기 버튼 제어")
    }

    //     서비스 시작 및 Messenger 전달
    private fun setStartService() {
        startService(Intent(this@LockScreenActivity, DrawService::class.java))
        bindService(Intent(this, DrawService::class.java), mConnection, BIND_AUTO_CREATE)
        mIsBound = true
    }

    //     서비스 정지
    private fun setStopService() {
        if (mIsBound) {
            unbindService(mConnection)
            mIsBound = false
        }
        stopService(Intent(this@LockScreenActivity, DrawService::class.java))
    }

    //액티비티 서비스 연결
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mServiceMessenger = Messenger(iBinder)
            try {
                Log.d(TAG, "onServiceConnected")
                val msg = Message.obtain(null, DrawService.MSG_REGISTER_CLIENT,settingTime)
                msg.replyTo = mMessenger
                mServiceMessenger!!.send(msg)
            } catch (e: RemoteException) {
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {}
    }

//     Service 로 부터 message를 받음
    private val mMessenger = Messenger(Handler { msg ->
        Log.i("test", "act : what " + msg.what)
        when (msg.what) {
            DrawService.MSG_SEND_TO_ACTIVITY -> {
                val result = msg.data.getBoolean("result")
                val message = msg.data.getString("message")
                Log.d(TAG, " result : $result")
                Log.d(TAG, " message : $message")
                if(message.equals("finish")){
                    setStopService()
                    finish()
                }
            }
        }
        false
    })

//     Service 로 메시지를 보냄
    private fun sendMessageToService(str: String) {
        if (mIsBound) {
            Log.d(TAG, "sendMessageToService: " + mServiceMessenger)
            if (mServiceMessenger != null) {
                try {
                    Log.d(TAG, "메시지보냄" + str)
                    val msg = Message.obtain(null, DrawService.MSG_SEND_TO_SERVICE, str)
                    msg.replyTo = mMessenger
                    mServiceMessenger!!.send(msg)
                } catch (e: RemoteException) {
                }
            }
        }
    }


}


