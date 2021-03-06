    package com.smartlock.smart_wb.View_Controller.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.smartlock.smart_wb.Model.SQLite.ScreenTimeDbHelper
import com.smartlock.smart_wb.Model.Shared.GuideShowCheckShared
import com.smartlock.smart_wb.Model.Shared.PointItemSharedModel
import com.smartlock.smart_wb.Model.Shared.TimerSetShared
import com.smartlock.smart_wb.R
import com.smartlock.smart_wb.View_Controller.Activity.LockScreenActivity
import com.smartlock.smart_wb.View_Controller.Activity.MainActivity
import com.smartlock.smart_wb.View_Controller.Dialog.GuideDialog
import com.smartlock.smart_wb.databinding.FragmentMainTimerBinding
import kotlinx.android.synthetic.main.fragment_main_timer.view.*
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 21/05/31 yama 스크린타임 타이머 시간 설정하는 프래그먼트
 */
class FragmentMainTimer : Fragment(), View.OnClickListener {
    private val TAG = "FragmentMainTimer"
    private var param1: String? = null
    private var param2: String? = null

    private var sound_value: Int = -1

    private lateinit var mContext: Context

    //뷰바인딩 위한 변수
    private var _binding: FragmentMainTimerBinding? = null
    private val binding get() = _binding!!

    //sqlite
//    private lateinit var timerDbHelper: TimerDbHelper
//    private lateinit var database: SQLiteDatabase

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentMainTimer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        } else if (context is LockScreenActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //view를 구성
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainTimerBinding.inflate(inflater, container, false)
        val view = binding.root

        view.soundsetting.setBackgroundResource(R.drawable.mbell)
        view.m_back.setImageResource(PointItemSharedModel.getBg(mContext))
        view.m_timer.setImageResource(PointItemSharedModel.getTimer(mContext))

        //알림 상태 확인
        val audioManager =
            requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            Log.d(LockScreenActivity.TAG, "벨소리모드")
            view.soundsetting.setBackgroundResource(R.drawable.mbell)
            sound_value = 0
        } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            Log.d(LockScreenActivity.TAG, "진동모드")
            view.soundsetting.setBackgroundResource(R.drawable.mvibe)
            sound_value = 1
        } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            Log.d(LockScreenActivity.TAG, "무음모드")
            view.soundsetting.setBackgroundResource(R.drawable.mmute)
            sound_value = 2
        }

        //알림 버튼 탭할 때마다 모드 변경
        view.soundsetting.setOnClickListener {
            if (sound_value == 0) {
                view.soundsetting.setBackgroundResource(R.drawable.mvibe) //진동모드
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE // 진동 모드로 변경
                sound_value = 1
            } else if (sound_value == 1) {
                view.soundsetting.setBackgroundResource(R.drawable.mmute) //무음모드
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT // 무음 모드로 변경
                sound_value = 2
            } else if (sound_value == 2) {
                view.soundsetting.setBackgroundResource(R.drawable.mbell) //벨소리모드
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL // 벨소리 모드로 변경
                sound_value = 0
            }
        }

        //numberPicker 최소,최대값 지정
        binding.npHour.minValue = 0
        binding.npHour.maxValue = 23
        binding.npMin.minValue = 0
        binding.npMin.maxValue = 59
        binding.npSec.minValue = 0
        binding.npSec.maxValue = 59

        val noDialCheck = GuideShowCheckShared.getNoDialCheck(mContext)
        Log.d(TAG, "onCreateView: $noDialCheck")

        var settingTime = 0
        view.start.setOnClickListener {
            //액티비티에 따라 동작을 달리한다.
            if (context is MainActivity) {
                settingTime =
                    binding.npHour.value * 3600 + binding.npMin.value * 60 + binding.npSec.value
                if (settingTime == 0) {
                    toastCenter(R.string.toast_time_set_blank_warning)
                } else {
                    //안내 다이얼로그 보여주기 유무
                    if (!noDialCheck) {
                        val dialog =
                            GuideDialog(
                                mContext
                            )
                        dialog.showDialog()

                        dialog.setItemClickListener2(object :
                            GuideDialog.OnItemClickListener {
                            override fun onClickStart(flag: Boolean) {
                                Log.d(TAG, "onClickStart: $flag")

                                GuideShowCheckShared.setNoDialCheck(mContext, flag)

                                startScreenTime(settingTime)
                                Log.d(
                                    TAG,
                                    "onClickStart: ${GuideShowCheckShared.getNoDialCheck(mContext)}"
                                )
                            }

                        })
                        //안내 다이얼로그 x 스크린타임 바로 시작
                    } else {
                        startScreenTime(settingTime)
                    }

                }
            }
        }
        return view
    }

    //activity 에서 intent로 데이터 받을 때 사용
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    //온클릭
    override fun onClick(p0: View?) {
        when (p0?.id) {
        }
    }

    //프래그먼트는 뷰보다 더 오래살아남는다.
    //바인딩 클래스는 뷰에 대한 참조를 가지고 있는데
    //뷰가 제거될 떄 바인딩 클래스의 인스턴스도 같이 정리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //토스트 메세지 화면 중앙
    fun toastCenter(message: Int) {
        val toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL)
        toast.show()
    }

    //설정시간 sqlite 에 데이터 삽입&쉐어드 저장
    @SuppressLint("SimpleDateFormat")
    fun insertSettingTime(settingTime: Int) {
//        Log.d(TAG, "insertSettingTime: ")

        val timeStamp:Long = System.currentTimeMillis()
        // 현재 시간을 Date 타입으로 변환
        val dateType = Date(timeStamp)

        // 날짜, 시간을 가져오고 싶은 형태 선언
        val dateFormatDate = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatYear = SimpleDateFormat("yyyy")
        val dateFormatMonth = SimpleDateFormat("MM")
        val dateFormatDay = SimpleDateFormat("dd")
        val dateFormatTime = SimpleDateFormat("HH:mm:ss")

        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        // year, month, day 는 Int 로 변환
        val date = dateFormatDate.format(dateType)
        val year = dateFormatYear.format(dateType).toInt()
        val month = dateFormatMonth.format(dateType).toInt()
        val day = dateFormatDay.format(dateType).toInt()
        val time = dateFormatTime.format(dateType)

        //타이머 데이터 인서트
//        timerDbHelper = TimerDbHelper(mContext, "timerDb.db", null, 1)
//        database = timerDbHelper.writableDatabase
//        timerDbHelper.insert(date, time, settingTime)
       val screenTimeDbHelper = ScreenTimeDbHelper(mContext, "screenTimeDb.db",null,1)
        val database = screenTimeDbHelper.writableDatabase
        //데이터 삽입
        screenTimeDbHelper.insert(year, month, day, time, settingTime)

        //설정시간 쉐어드에 저장
        TimerSetShared.setDate(mContext, date)
        TimerSetShared.setTime(mContext, time)
        TimerSetShared.setSettingTime(mContext, settingTime)
    }

    //스크린 타임 시작
    private fun startScreenTime(settingTime: Int) {
        //스크린타임 동작유무 확인
        if(TimerSetShared.getRunning(mContext)){
            toastCenter(R.string.toast_screenTime_already_start_warning)
        }else{
            TimerSetShared.setRunning(mContext, true)
            val intent = Intent(mContext, LockScreenActivity::class.java)
            intent.putExtra("settingTime", settingTime.toString())
            startActivity(intent)
            //설정시간 데이터 삽입
            insertSettingTime(settingTime)
            //액티비티 종료
            activity?.finish()

        }

    }



}