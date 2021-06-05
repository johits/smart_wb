package com.example.smart_wb

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_lock_screen.*


/**
 * 20/05/31 yama 잠금화면 액티비티
 * 서비스에서 성공 메세지 받으면 노티피케이션 발생
 * */
class LockScreenActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LockScreenActivity"
        const val channel_name: String = "smart_wb_channel"
        const val CHANNEL_ID: String = "com.example.smart_wb"
        const val notificationId: Int = 1001

    }

    //서비스에 데이터 보내기 위한 변수
    private var mServiceMessenger: Messenger? = null
    private var mIsBound = false

    private var settingTime = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)



        if (intent.hasExtra("settingTime")) {
            var time = intent.getStringExtra("settingTime")?.toInt()
            Log.d(TAG, "onCreate: " + time)
            if (time != null) {
                settingTime = time
            }
        }

        tvWatch.visibility = View.GONE
        btStop.visibility = View.GONE

        //노티피 초기화
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //방해금지모드작동
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        Log.d("락스크린액티비티", "onCreate: 여기로들어와지나")

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
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            mServiceMessenger = Messenger(iBinder)
            try {
                Log.d(TAG, "onServiceConnected")
                val msg = Message.obtain(null, DrawService.MSG_REGISTER_CLIENT, settingTime)
                msg.replyTo = mMessenger
                mServiceMessenger!!.send(msg)
            } catch (e: RemoteException) {
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {}
    }

    //     Service 로 부터 message를 받음
    @RequiresApi(Build.VERSION_CODES.O)
    private val mMessenger = Messenger(Handler { msg ->
        Log.i("test", "act : what " + msg.what)
        when (msg.what) {
            DrawService.MSG_SEND_TO_ACTIVITY -> {
                val result = msg.data.getBoolean("result")
                val message = msg.data.getString("message")
                Log.d(TAG, " result : $result")
                Log.d(TAG, " message : $message")
                if (result) { //스크린타임 성공시 노티활성화
                    getDisplayWakeUp()
                    showNotification()
                } else {//스크린타임 실패시

                }
                setStopService()
                finish()
            }
        }
        false
    })

    //     Service 로 메시지를 보냄
    @RequiresApi(Build.VERSION_CODES.O)
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

    //노티피케이션 채널 생성
    private fun createNotificationChannel(id: String, names: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = names
            val descriptionText = channelDescription
            val importance = NotificationManager.IMPORTANCE_HIGH //high 이상이여야 헤드업 알림 나온다.
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = descriptionText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    //노티피케이션 발생
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification() {
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.screen_time_success_noti_title))
            .setContentText(getString(R.string.screen_time_success_noti_text))
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//잠금화면에서 보여주기

        createNotificationChannel(CHANNEL_ID, channel_name, getString(R.string.app_name))

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, builder.build())

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
//        val vibrationEffect = VibrationEffect.createOneShot(1000, DEFAULT_AMPLITUDE)
//        vibrator.vibrate(vibrationEffect);

        vibrator.vibrate(VibrationEffect.createOneShot(1000, 50))


    }

    //화면 기상
    fun getDisplayWakeUp() {
        try {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val wakelock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "My:Tag"
            )
            wakelock.acquire() //화면 기상
            wakelock.release() //WakeLock 자원 해제
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


