package com.example.smart_wb

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.sqlite.SQLiteDatabase
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smart_wb.SQLite.TimerData
import com.example.smart_wb.SQLite.TimerDbHelper
import com.example.smart_wb.Shared.PointItemShared
import com.example.smart_wb.Shared.TimerSetShared
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        tvWatch.visibility = View.GONE
        btStop.visibility = View.GONE
        Log.d("락스크린액티비티", "onCreate: 여기로들어와지나")

        if (intent.hasExtra("settingTime")) {
            var time = intent.getStringExtra("settingTime")?.toInt()

            if (time != null) {
                settingTime = time

                //노티피 초기화
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                //방해금지모드작동
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)

                setStartService()
            }
        } else if (intent.hasExtra("restart")) {
            val remainTime = calRemainTime()
            Log.d(TAG, "남은시간:$remainTime")
            //남은시간이 0보다 크면 스크린타임 계속
            //남은시간이 0, 음수면 스크린타임 이미 종료됨
            if (remainTime > 0) {
                settingTime=remainTime //중요
            } else {
                settingTime=0
//                showNotification()//노티활성화
//                successUpdate() //성공시//sqlite 업데이트
//                TimerSetShared.clearTimerSet(this)//쉐어드 초기화
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
            }
                //노티피 초기화
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                //방해금지모드작동
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)

                setStartService()
        }

    }

//    override fun onBackPressed() {
//        // 뒤로가기 버튼 클릭
//        Log.d(TAG, "onBackPressed: 뒤로가기 버튼 제어")
//    }

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

    //액티비티 서비스 연결//설정시간 데이터 전달한다.
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
                if (result) { //스크린타임 성공시 노티활성화 //데이터 업데이트//꽃받음//쉐어드 클리어
                    getDisplayWakeUp()
                    showNotification()
                    successUpdate() //성공시//sqlite 업데이트
                } else {//스크린타임 실패시

                }
                //쉐어드 데이터 클리어
                TimerSetShared.clearTimerSet(this)
                //쉐어드 저장 확인용 로그
//                Log.d(
//                    TAG, "시작날짜:" + TimerSetShared.getDate(this) + " " +
//                            "시작시간:" + TimerSetShared.getTime(this) + " " +
//                            "설정시간:" + TimerSetShared.getSettingTime(this)
//                )
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
   private fun showNotification() {
        Log.d(TAG, "showNotification: ")
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.screen_time_success_noti_title))
            .setContentText(getString(R.string.screen_time_success_noti_text))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(), 0)) //setAutoCancel
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//잠금화면에서 보여주기

        createNotificationChannel(CHANNEL_ID, channel_name, getString(R.string.app_name))

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, builder.build())

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
//        val vibrationEffect = VibrationEffect.createOneShot(1000, DEFAULT_AMPLITUDE)
//        vibrator.vibrate(vibrationEffect);

//        vibrator.vibrate(VibrationEffect.createOneShot(1000, 50))


    }

    //화면 기상
    @SuppressLint("WakelockTimeout")
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

    override fun onStop() {
        super.onStop()

    }

    //성공시 sqlite timer table 에 success 업데이트//쉐어드에 받은 꽃 더하기
   private fun successUpdate() {
        Log.d(TAG, "successUpdate: ")
        var date = TimerSetShared.getDate(this)
        var time = TimerSetShared.getTime(this)
        var settingTime = TimerSetShared.getSettingTime(this)
        var flower = settingTime / 600 //꽃 갯수 10분당 1개 받는다.

        var timerDbHelper = TimerDbHelper(this, "timerDb.db", null, 1)
        var database = timerDbHelper.writableDatabase

        //데이터 삽입
        timerDbHelper.upDate(date, time, flower)
        //   데이터 불러오기
        var arr: ArrayList<TimerData> = timerDbHelper.select()
        // 데이터 확인용 로그
//        for (data in arr) {
//            Log.d(
//                TAG,
//                "id:" + data.id + " date:" + data.date + " " +
//                        "time:" + data.time + " settingTime:" + data.settingTime + " " +
//                        "success:" + data.success + " flower:" + data.flower
//            )
//        }

        //받은 꽃 쉐어드에 더한다.
        PointItemShared.sumFlower(this, flower)

        Log.d(TAG, "현재 꽃 갯수" + PointItemShared.getFlower(this))
    }

    //남은시간 계산기 //남은시간 리턴
    //시작시간+설정시간=종료시간
    //종료시간-현재시간=남은시간
    //남은시간 양수 스크린타임 계속
    //남은시간 0or음수 스크린타임 이미 종료
    //날짜가 바뀌면 보정을 해야한다. 어떻게?
    private fun calRemainTime(): Int {
        var result = 0
        val timeStamp = System.currentTimeMillis()
        // 현재 시간을 Date 타입으로 변환
        val dateType = Date(timeStamp)
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val dateFormatDate = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatTime = SimpleDateFormat("HH:mm:ss")
        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        val nowDate:String = dateFormatDate.format(dateType) //현재 년 월 일
        val nowTime: Int = calSec(dateFormatTime.format(dateType))//현재시간
        val startTime: Int = calSec(TimerSetShared.getTime(this)) //시작시간
        val settingTime: Int = TimerSetShared.getSettingTime(this)//설정시간
        var endTime = startTime + settingTime// 종료시간

        //종료시간이 하루가 지난 상황
        if(endTime>86400){
            if(nowDate.equals(TimerSetShared.getDate(this))){
                result=endTime-nowTime
            }else{
                result=endTime-nowTime-86400//보정필요하다
            }
        }else{
            result = endTime - nowTime//남은시간
        }

        return result
    }

    //시간 -> 초 변환 //String->Int
    private fun calSec(time: String): Int {
        val parts = time.split(":").toTypedArray()
        val hour: Int = parts[0].toInt()
        val min: Int = parts[1].toInt()
        val sec: Int = parts[2].toInt()
        return hour * 3600 + min * 60 + sec
    }
}


