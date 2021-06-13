package com.example.smart_wb

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.media.RingtoneManager
import android.os.*
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        const val CHANNEL_ID_SUCCESS: String = "smart_wb_success"
        const val CHANNEL_ID_MISSEDCALL: String = "smart_wb_call"
        const val notificationId_success: Int = 1001
        const val notificationId_call: Int = 1002

    }

//    private var dialog: CustomDialog? = null
    private var alertDialog:AlertDialog?=null
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
        //쉐어드 적용된 아이템 불러오기(배경, 타이머)
        l_back.setImageResource(PointItemShared.getBg(this))
        l_timer.setImageResource(PointItemShared.getTimer(this))

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
                settingTime = remainTime //중요
            } else {
                settingTime = 0
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
                val missedCall = TimerSetShared.getMissedCall(this)//부재중 전화 수
                val setTime = TimerSetShared.getSettingTime(this)//설정시간
                val setTimeString: String = calTime(setTime)//설정시간 초 -> 시간 변환
                val flower = setTime / 600 //획득한 꽃 갯수
                if (result) { //스크린타임 성공시 노티활성화 //데이터 업데이트//꽃받음//쉐어드 클리어
                    getDisplayWakeUp()

                    successUpdate(setTime,flower) //성공시//sqlite 업데이트
                    showDialog(getString(R.string.success_dialog_title_success), setTimeString, flower, missedCall)
                    //노티
                    val successTitle = "목표하신 $setTimeString 시간 동안 휴대폰을 사용하지 않으셨군요!"
                    val successText = "꽃 $flower 송이 획득."
                    showNotification(
                        notificationId_success,
                        CHANNEL_ID_SUCCESS,
                        successTitle,
                        successText
                    )
                    //부재중 전화가 있으면 알람
                    if (missedCall != 0) {
                        missedCallNoti(missedCall, 4500)
                    }

                } else {//스크린타임 사용자 종료시
                    showDialog(getString(R.string.success_dialog_title_fail), setTimeString, flower, missedCall)
                    //부재중 전화가 있으면 알람
                    if (missedCall != 0) {
                        missedCallNoti(missedCall, 0)
                    }
                    startMainActivity()
                }
                //쉐어드 데이터 클리어
                TimerSetShared.clearTimerSet(this)

                setStopService()

            }
        }
        false
    })

    //스크린타임 종료시 다이얼로그
    private fun showDialog(title:String, setTime:String, flower:Int, missedCall:Int){
        Log.d(TAG, "showDialog: ")
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.success_dialog, null)

         alertDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val tvFlower = view.findViewById<TextView>(R.id.tvFlower)
        val tvMissedCall = view.findViewById<TextView>(R.id.tvMissedCall)
        val tvSettingTime = view.findViewById<TextView>(R.id.tvSettingTime)

        tvTitle.text=title
        tvSettingTime.text=setTime
        tvFlower.text=flower.toString()
        tvMissedCall.text = missedCall.toString()

        btnConfirm.setOnClickListener {
            alertDialog!!.dismiss()
            startMainActivity()
        }
        alertDialog!!.show()
    }

    //부재중전화 노티 호출
    @RequiresApi(Build.VERSION_CODES.O)
    private fun missedCallNoti(call: Int, delay: Long) {
        val missedCallText: String = "부재중 전화 $call 건이 있습니다."
        //코루틴//비동기처리
        GlobalScope.launch {
            delay(delay)
            showNotification(
                notificationId_call,
                CHANNEL_ID_MISSEDCALL,
                missedCallText,
                ""
            )
        }
    }

    //메인액티비티 호출
    private fun startMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        finish()
    }

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


    //노티피케이션 발생
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(notiId: Int, chanelId: String, title: String, text: String) {
        val arr = arrayListOf(0, 1, 2)
        var a = longArrayOf(1000)
        var builder = NotificationCompat.Builder(this, chanelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(), 0)) //setAutoCancel 동작안해서
            .setPriority(NotificationCompat.PRIORITY_MAX) //오레오 이하 버전에서는 high 이상이어야 헤드업 알림
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//잠금화면에서 보여주기

        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        //알림 상태 확인
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notiId, builder.build())

        //누가에선 터진다.
//        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
//        val vibrationEffect = VibrationEffect.createOneShot(1000, DEFAULT_AMPLITUDE)
//        vibrator.vibrate(vibrationEffect);
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

    override fun onDestroy() {
        super.onDestroy()
        if (alertDialog != null&&alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
            alertDialog=null
        }
    }

    //성공시 sqlite timer table 에 success 업데이트//쉐어드에 받은 꽃 더하기
    private fun successUpdate(settingTime:Int, flower:Int) {
        Log.d(TAG, "successUpdate: ")
        var date = TimerSetShared.getDate(this)
        var time = TimerSetShared.getTime(this)
//        var settingTime = TimerSetShared.getSettingTime(this)
//        var flower = settingTime / 600 //꽃 갯수 10분당 1개 받는다.

        Log.d(TAG, "$date , $time , $settingTime , $flower")

        var timerDbHelper = TimerDbHelper(this, "timerDb.db", null, 1)
        var database = timerDbHelper.writableDatabase

        //데이터 업데이트
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
        val nowDate: String = dateFormatDate.format(dateType) //현재 년 월 일
        val nowTime: Int = calSec(dateFormatTime.format(dateType))//현재시간
        val startTime: Int = calSec(TimerSetShared.getTime(this)) //시작시간
        val settingTime: Int = TimerSetShared.getSettingTime(this)//설정시간
        var endTime = startTime + settingTime// 종료시간

        //종료시간이 하루가 지난 상황
        if (endTime > 86400) {
            if (nowDate.equals(TimerSetShared.getDate(this))) {
                result = endTime - nowTime
            } else {
                result = endTime - nowTime - 86400//보정필요하다
            }
        } else {
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

    //초-> 시간 변환환
    @RequiresApi(Build.VERSION_CODES.N)
    private fun calTime(setTime: Int): String {
        val result: String?
        val hour = Math.floorDiv(setTime, 3600)
        val min = Math.floorMod(setTime, 3600) / 60
        //  val sec = Math.floorMod(setTime, 3600) % 60
        //if (hour > 0) {
        result = "%1$02d:%2$02d".format(hour, min)
        // } else {
        //   result="%1$02d:%2$02d".format(min,sec)
        //}
        return result
    }
}


