package com.example.smart_wb

import android.app.Notification
import android.app.NotificationChannel
import android.content.ComponentName
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.smart_wb.DrawService.Companion.MSG_REGISTER_CLIENT
import com.example.smart_wb.databinding.ActivityLockScreenBinding
import kotlinx.android.synthetic.main.activity_lock_screen.*
import kotlinx.android.synthetic.main.activity_lock_screen.view.*


/**
 * 20/05/31 yama 잠금화면 액티비티
 * DrawService 생성, 메시지 송수신
 * */
class LockScreenActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LockScreenActivity"
    }
    //노티피케이션
    private val channelID = "com.example.smart_wb.channel"
    private var notificationManager: NotificationManager? = null
    private val KEY_REPLY = "key_reply"

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
//        createNotificationChannel(channelID,"success","success alarm")
        btStop.setOnClickListener {
            Log.d(TAG, "버튼")
            //showNotification()
            createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_DEFAULT,
                false, "smart_wb", "App notification channel") // 1
            val notificationId = 22
            val channelId = "$packageName-${getString(R.string.app_name)}" // 2
            val title = "Android Developer"
            val content = "Notifications in Android P"

            val intent = Intent(baseContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(baseContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)    // 3

            val builder = NotificationCompat.Builder(this, channelId)  // 4
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)    // 5
            builder.setContentTitle(title)    // 6
            builder.setContentText(content)    // 7
            builder.priority = NotificationCompat.PRIORITY_DEFAULT    // 8
            builder.setAutoCancel(true)   // 9
            builder.setContentIntent(pendingIntent)   // 10

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationId, builder.build())    // 11
        }
        //노티피 초기화
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        //방해금지모드작동
//        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        Log.d("락스크린액티비티", "onCreate: 여기로들어와지나")

//        setStartService()
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
                val msg = Message.obtain(null, DrawService.MSG_REGISTER_CLIENT, settingTime)
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
                if (message.equals("finish")) {
                    if(result) showNotification()
                    setStopService()
//                    finish()
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

    //노티피케이션 채널 생성
//    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //중요도
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            //채널 생성
//            val channel = NotificationChannel(id, name, importance).apply {
//                description = channelDescription
//            }
//
//            notificationManager?.createNotificationChannel(channel)
//        } else {
//
//        }
//    }
    private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean,
                                          name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        Log.d(TAG, "showNotification: 실행")
//         1. 알림콘텐츠 설정
        //채널 ID
        val notificationId = 45
        //알림의 탭 작업 설정 -----------------------------------------------------------------------
        val tapResultIntent = Intent(this, MainActivity::class.java).apply {
            //현재 액티비티에서 새로운 액티비티를 실행한다면 현재 액티비티를 새로운 액티비티로 교체하는 플래그
            flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            //이전에 실행된 액티비티들을 모두 없엔 후 새로운 액티비티 실행 플래그
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //알림의 탭 작업 설정(Reply 용)--------------------------------------------------------------
        val replyResultIntent = Intent(this, MainActivity::class.java)
        val replyPendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            replyResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //바로 답장 작업 추가(reply action)
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_REPLY).run {
            setLabel("Insert you name here") //텍스트 입력 힌트
            build()
        }

        val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            0, //icon
            "REPLY", //title
            replyPendingIntent
        ).addRemoteInput(remoteInput)
            .build()
        //작업 버튼 추가(action button 1)-----------------------------------------------------------
        val intent2 = Intent(this, MainActivity::class.java)
        val pendingIntent2: PendingIntent = PendingIntent.getActivity(
            this,
            0, //request code
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action2: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Details", pendingIntent2).build()
        // 작업 버튼 추가(action button 2)
        val intent3 = Intent(this, MainActivity::class.java)
        val pendingIntent3: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent3,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action3: NotificationCompat.Action =
            NotificationCompat.Action.Builder(0, "Settings", pendingIntent3).build()

        //노티피케이션 생성 -------------------------------------------------------------------------
        val notification: Notification = NotificationCompat.Builder(this@LockScreenActivity, channelID)
            .setContentTitle(R.string.app_name.toString()) // 노티 제목
            .setContentText("스크린 타임 성공!") // 노티 내용
            .setSmallIcon(android.R.drawable.ic_dialog_info) //아이콘이미지
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 삭제합니다.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) //노티클릭시 인텐트작업
//            .addAction(action2) //액션버튼 인텐트
//            .addAction(action3)
//            .addAction(replyAction) //바로 답장 작업 추가(reply action) 액션버튼
            .build()
        /* 3. 알림 표시*///---------------------------------------------------------------------------
        //NotificationManagerCompat.notify()에 전달하는 알림 ID를 저장해야 합니다.
        // 알림을 업데이트하거나 삭제하려면 나중에 필요하기 때문입니다.
        notificationManager?.notify(notificationId, notification) //노티실행

    }
}


