package com.example.smart_wb.View_Controller.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smart_wb.Model.Calculator
import com.example.smart_wb.Model.RemainTime
import com.example.smart_wb.Model.ScreenTime
import com.example.smart_wb.Model.Shared.PointItemSharedModel
import com.example.smart_wb.Model.Shared.TimerSetShared
import com.example.smart_wb.R
import com.example.smart_wb.View_Controller.Service.DrawService
import kotlinx.android.synthetic.main.activity_lock_screen.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * 20/05/31 yama 잠금화면 액티비티
 * 드로우서비스 시작&종료시 데이터를 주고 받아
 * 결과 다이얼로그&노티피케이션(사용안함)  생성  -> 메인액티비티에서 다이얼로그 생성
 * */
class LockScreenActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LockScreenActivity"

        const val CHANNEL_ID_SUCCESS: String = "smart_wb_success" //스크린타임 성공 노티피케이션 채널아이디
        const val CHANNEL_ID_MISSEDCALL: String = "smart_wb_call" //스크린타임 중 부재전화 노티피케이션 채널아이디
        const val notificationId_success: Int = 1001 //스크린타임 성공 노티피케이션아이디
        const val notificationId_call: Int = 1002 //스크린타임 중 부재전화 노티피케이션아이디

    }

    private var alertDialog: AlertDialog? = null //스크린타임 결과표시용 다이얼로그

    private var mServiceMessenger: Messenger? = null //서비스에 데이터 보내기 위한 변수
    private var mIsBound = false //서비스 동작 유무 확인용 플래그
    private var settingTime = 0 //설정시간


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        tvWatch.visibility = View.GONE
        btStop.visibility = View.GONE


        //쉐어드 적용된 아이템 불러오기(배경, 타이머)
        l_back.setImageResource(PointItemSharedModel.getBg(this))
        l_timer.setImageResource(PointItemSharedModel.getTimer(this))

        //스크린타임 시작
        if (intent.hasExtra("settingTime")) {
            TimerSetShared.clearMissedCall(this)
            var time = intent.getStringExtra("settingTime")?.toInt()

            if (time != null) {
                settingTime = time

                setStartService() //드로우서비스 시작
            }

            //핸드폰 재시작할 때
        } else if (intent.hasExtra("restart")) {
            val setTime = TimerSetShared.getSettingTime(this)
            val startTime = TimerSetShared.getTime(this)
            val startDate = TimerSetShared.getDate(this)
            val remain = RemainTime(startTime, startDate, setTime)
            val remainTime = remain.calRemainTime()//스크린타임 남은 시간계산

            Log.d(TAG, "남은시간:$remainTime")

            //남은시간이 0보다 크면 스크린타임 계속
            //남은시간이 0, 음수면 스크린타임 이미 종료됨
            if (remainTime > 0) {
                settingTime = remainTime //중요
            } else {
                settingTime = 0 //스크린타임 이미 종료
            }


            setStartService()//드로우서비스 시작
        }

    }

    //스크린타임중 홈버튼 클릭시 액티비티 다시 불러오는데 시간이 소요
    //그 부분 예외처리용
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        //스크린타임이 동작중이 아니면 락스크린 액티비티 finish
        if(!TimerSetShared.getRunning(this)){
            finish()
        }
    }

    //     서비스 시작 및 Messenger 전달
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setStartService() {
        //노티피 초기화
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //방해금지모드작동
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        //드로우서비스 시작
        startService(Intent(this@LockScreenActivity, DrawService::class.java))
//        액티비티와 서비스 연결
        bindService(Intent(this, DrawService::class.java), mConnection, BIND_AUTO_CREATE)
        mIsBound = true// 서비스상태 확인용 플래그
    }

    //     서비스 정지
    private fun setStopService() {
        Log.d(TAG, "setStopService: $mIsBound")
        if (mIsBound) {
            unbindService(mConnection)
            mIsBound = false
        }
        stopService(Intent(this@LockScreenActivity, DrawService::class.java))
    }

    //액티비티와 서비스 연결//설정시간 데이터 전달한다.
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
                val result = msg.data.getBoolean("result") //true==성공, false==종료
                val message = msg.data.getString("message") //사용안함
                Log.d(TAG, " result : $result")
                Log.d(TAG, " message : $message")

                setStopService() //서비스 종료

                //타이머쉐어드 running -> false
                TimerSetShared.setRunning(this, false)
                finish()
                //스크린타임 결과에 따른 다이얼로그, 노티피케이션
                resultScreenTime(result)


            }
        }
        false
    })

    //스크린타임 결과에 따른 데이터업데이트 다이얼로그, 노티피케이션
    @RequiresApi(Build.VERSION_CODES.O)
    private fun resultScreenTime(result: Boolean) {
        val missedCall = TimerSetShared.getMissedCall(this)//부재중 전화 수
        val setTime = TimerSetShared.getSettingTime(this)//설정시간
        val calculator = Calculator()
        val setTimeString: String = calculator.calTime(setTime)//설정시간 초 -> 시간 변환
        val flower = setTime / 600 //획득한 꽃 갯수
        if (result) { //스크린타임 성공시
            getDisplayWakeUp() //핸드폰화면 켜짐

            //테이블 스크린타임 성공, 받은 꽃 업데이트
            val screenTime = ScreenTime(this)
            screenTime.successUpdate(flower)


//            val successTitle = "목표하신 $setTimeString 동안 휴대폰을 사용하지 않으셨군요!"
//            val successText = "꽃 $flower 송이 획득."
//            showNotification(
//                notificationId_success,
//                CHANNEL_ID_SUCCESS,
//                successTitle,
//                successText
//            )
            //부재중 전화가 있으면 노티피게이션
//            if (missedCall != 0) {
//                missedCallNoti(missedCall, 4500) //성공 노티 뜨고 4.5초 딜레이 후 부재중전화 노티 생성
//            }

        } else {//스크린타임 사용자 종료시

            //부재중 전화가 있으면 노티피케이션
//            if (missedCall != 0) {
//                missedCallNoti(missedCall, 0) //딜레이 없이 노티 생성
//            }
        }
        finish()

    }



    //부재중전화 노티 호출
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun missedCallNoti(call: Int, delay: Long) {
//        val missedCallText: String = "부재중 전화 $call 건이 있습니다."
//
//        //코루틴//비동기처리
//        GlobalScope.launch {
//            delay(delay)
//            showNotification(
//                notificationId_call,
//                CHANNEL_ID_MISSEDCALL,
//                missedCallText,
//                ""
//            )
//        }
//    }

    //메인액티비티 호출
    private fun startMainActivity(title: String, setTime: String, flower: Int, missedCall: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java)

        intent.putExtra("title", title)
        intent.putExtra("setTime", setTime)
        intent.putExtra("flower", flower)
        intent.putExtra("missedCall", missedCall)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(
            intent
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
        )

        // finish()
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
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun showNotification(notiId: Int, chanelId: String, title: String, text: String) {
//
//        var builder = NotificationCompat.Builder(this, chanelId)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle(title)
//            .setContentText(text)
//            .setAutoCancel(true) //터치시 노티 지우기
//            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(), 0)) //setAutoCancel 동작안해서
//            .setPriority(NotificationCompat.PRIORITY_MAX) //오레오 이하 버전에서는 high 이상이어야 헤드업 알림
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//잠금화면에서 보여주기
//
////        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))//노티피케이션 소리설정
//        builder.setSound(Uri.EMPTY)
//
//        //알림 상태 확인
//        val notificationManager = NotificationManagerCompat.from(this)
//        notificationManager.notify(notiId, builder.build())
//    }


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


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ${TimerSetShared.getRunning(this)}")
        if (TimerSetShared.getRunning(this)) {
            startActivity(
                Intent(this, LockScreenActivity::class.java)
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        } else {
            val missedCall = TimerSetShared.getMissedCall(this)//부재중 전화 수
            val setTime = TimerSetShared.getSettingTime(this)//설정시간
            val calculator = Calculator()
            val setTimeString: String = calculator.calTime(setTime)//설정시간 초 -> 시간 변환
            val flower = setTime / 600 //획득한 꽃 갯수
            if (TimerSetShared.getResult(this)) {
                startMainActivity(
                    getString(R.string.success_dialog_title_success),
                    setTimeString,
                    flower,
                    missedCall
                )
            } else {
                startMainActivity(
                    getString(R.string.success_dialog_title_fail),
                    setTimeString,
                    0,
                    missedCall
                )
            }

        }
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
        //android.view.WindowLeaked 에러 처리 위한 구문
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
            alertDialog = null
        }
    }


    override fun onBackPressed() {
        Log.d(TAG, "락 스크린 뒤로가기 제어")
    }

}


