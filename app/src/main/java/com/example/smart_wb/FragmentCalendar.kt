package com.example.smart_wb

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.format
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_wb.SQLite.ScreenTimeData
import com.example.smart_wb.SQLite.ScreenTimeDbHelper
import com.example.smart_wb.SQLite.TimerData
import com.example.smart_wb.databinding.FragmentCalendarBinding
import com.google.gson.internal.bind.util.ISO8601Utils.format
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * 21-05-31 yama 달력
 * 달력에 스크린타임 성공 실패 표시
 * */
class FragmentCalendar : Fragment() {

    lateinit var calendarAdapter: CalendarAdapter //상세기록 표시 리사이클러뷰 어답터
    var dataList = mutableListOf<ScreenTimeData>() //상세기록 데이터 리스트
    var decoList = mutableListOf<ScreenTimeData>() //도전 기록 있는 날짜 데코용 데이터 리스트

    lateinit var timerDataList: ArrayList<TimerData>

    private val TAG = "FragmentCalendar"
    private lateinit var mContext: Context

    //뷰바인딩 위한 변수
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root

        initRecycler()//상세보기 리사이클러뷰

        //오늘날짜 표시
        decorateToday()
        //도전 기록 있는 날짜 데코하기
        screenTimeDataDeco()
//        val format = SimpleDateFormat("yyyy MM")
//        val titleFormatter:TitleFormatter
//        binding.calendar.setTitleFormatter()

        binding.calendar.setOnDateChangedListener { widget, date, selected ->
//            binding.calendar.state().edit()
//                .setCalendarDisplayMode(CalendarMode.MONTHS)
//                .commit()
            val year: Int = date.year
            val month: Int = date.month
            val day: Int = date.day

            dataList = selectDate(year, month, day)
            if (dataList.size == 0) {//데이터 없을 때
                binding.linearParent.visibility = View.GONE//8
                binding.tvNoData.visibility = View.VISIBLE//0
                binding.linearExplain.visibility = View.GONE
                if (binding.btnShowDetail.text.toString()
                        .equals(getString(R.string.calendar_btn_detail_close))
                ) {
                    binding.calendar.state().edit()
                        .setCalendarDisplayMode(CalendarMode.MONTHS)
                        .commit()
                    binding.btnShowDetail.text = getString(R.string.calendar_btn_detail_show)
                }
            } else { //데이터 있을 때
                binding.linearParent.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                binding.recycler.smoothScrollToPosition(0)
//                binding.recycler.visibility = View.VISIBLE
            }
            calendarAdapter.replaceList(dataList)
            calculateSum() //날짜에 해당하는 총도전시간, 성공시간, 획득꽃 계산
        }

        //상세보기버튼 클릭 이벤트. 리사이클러뷰 비저블
        binding.btnShowDetail.setOnClickListener {
            if (binding.btnShowDetail.text.toString()
                    .equals(getString(R.string.calendar_btn_detail_show))
            ) {
                binding.calendar.state().edit()
                    .setCalendarDisplayMode(CalendarMode.WEEKS)
                    .commit()
                binding.btnShowDetail.text = getString(R.string.calendar_btn_detail_close)
                binding.recycler.visibility = View.VISIBLE
                binding.linearExplain.visibility = View.VISIBLE
            } else {
                binding.calendar.state().edit()
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit()
                binding.btnShowDetail.text = getString(R.string.calendar_btn_detail_show)
                binding.recycler.visibility = View.GONE
                binding.linearExplain.visibility = View.GONE
            }
        }

        //타이틀을 누르면 월간단위로 보여지게 변경
        binding.calendar.setOnTitleClickListener {
            binding.calendar.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
           decorateToday()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initRecycler()
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.setLayoutManager(layoutManager)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext()).also {
            it.orientation = LinearLayoutManager.VERTICAL
        }

//        구분선 넣기 (Horizontal 인 경우 0, vertical인 경우 1 설정)
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        calendarAdapter = CalendarAdapter(mContext)
        binding.recycler.adapter = calendarAdapter

    }

    //프래그먼트는 뷰보다 더 오래살아남는다.
    //바인딩 클래스는 뷰에 대한 참조를 가지고 있는데
    //뷰가 제거될 떄 바인딩 클래스의 인스턴스도 같이 정리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 현재 Year
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    // 현재 Month
    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    // 현재 Day
    fun getCurrentDay(): Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)


    // 도전 기록이 있는 날짜에 점찍기
    //달력 표시 제한
    fun screenTimeDataDeco() {
        val screenTimeDbHelper = ScreenTimeDbHelper(mContext, "screenTimeDb.db", null, 1)
        decoList = screenTimeDbHelper.calendarSelect()

        //도전 기록이 있는 날짜에 점찍기
        for (item in decoList) {
            val year = item.year
            val month = item.month
            val day = item.day
            if(year!=null&&month!=null&&day!=null){
                val calDay = CalendarDay.from(year, month, day)
                binding.calendar.addDecorator(CalendarDecoratorpDotSpan(requireActivity(), calDay))
            }
        }

        //달력표시제한
        if(decoList.size>0){
             Log.d(TAG, "달력크기제한")
            val firstYear: Int? = decoList[0].year
            val firstMonth: Int? = decoList[0].month
            val firstDay: Int = 1
            val lastYear: Int? = decoList[decoList.size-1].year
            val lastMonth: Int? = decoList[decoList.size-1].month
            val lastDay: Int = calLastDay(lastYear, lastMonth)

            //첫번쨰 데이터 기준 최소 날짜 제한한다
            binding.calendar.state().edit()
                .setMinimumDate(
                    CalendarDay.from(
                        firstYear!!,
                        firstMonth!!,
                        firstDay
                    )
                )
                .commit()

            //마지막 데이터 기준 최대 날짜 제한한다.
            binding.calendar.state().edit()
                .setMaximumDate(
                    CalendarDay.from(
                        lastYear!!,
                        lastMonth!!,
                        lastDay
                    )
                )
                .commit()
        }

    }

    //그 달의 마지막 날짜 구하기
    fun calLastDay(year: Int?, month: Int?):Int{
        val cal = Calendar.getInstance()
        if (year != null&&month!=null) {
            cal.set(year,month-1, 15, 0,0,0)
        }//month는 -1해줘야 해당월로 인식
        var lastDay:Int= cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        return lastDay
    }

    //선택한 날짜 데이터 불러오기
    private fun selectDate(year: Int, month: Int, day: Int): MutableList<ScreenTimeData> {
        Log.d(TAG, "selectDate: ")
        dataList.clear()
        val screenTimeDbHelper = ScreenTimeDbHelper(mContext, "screenTimeDb.db", null, 1)

        var result: MutableList<ScreenTimeData> =
            screenTimeDbHelper.calendarSelect(year, month, day)
        return result;
    }

    //날짜에 해당하는 총도전시간,성공시간,꽃 계산기 및 setText
    @RequiresApi(Build.VERSION_CODES.N)
    private fun calculateSum() {
        var settingTimeSum = 0 //총 설정시간
        var successTimeSum = 0 //총 성공시간
        var flowerSum = 0 //받은 꽃 합계
        for (data in dataList) {
            settingTimeSum += data.settingTime!!
            if (data.success == 1) {
                successTimeSum += data.settingTime
                flowerSum += data.flower!!
            }
        }
        Log.d(TAG, "총도전시간:$settingTimeSum 총성공시간:$successTimeSum 획득꽃:$flowerSum")
        binding.tvSettingTimeSum.text = changeTime(settingTimeSum)
        binding.tvSuccessTimeSum.text = changeTime(successTimeSum)
        binding.tvFlowerSum.text = flowerSum.toString()
    }

    //설정시간은 초 -> "HH시간 mm분" 으로 변환
    @RequiresApi(Build.VERSION_CODES.N)
    private fun changeTime(settingTime: Int): String {
        val result: String?
        val hour = Math.floorDiv(settingTime, 3600)
        val min = Math.floorMod(settingTime, 3600) / 60
        result = "%1$02d시간 %2$02d분".format(hour, min)

        return result
    }

    //오늘날짜 표시&현재날짜 데이터 불러오기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun decorateToday() {
        val timeStamp = System.currentTimeMillis()
        // 현재 시간을 Date 타입으로 변환
        val dateType = Date(timeStamp)
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val dateFormatDate = SimpleDateFormat("yyyy-MM-dd")
        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        val today: String = dateFormatDate.format(dateType) //현재 년 월 일
        val parts = today.split("-").toTypedArray()
        val year: Int = parts[0].toInt()
        val month: Int = parts[1].toInt()
        val day: Int = parts[2].toInt()
        val calDay = CalendarDay.from(year, month, day)

        binding.calendar.addDecorator(CalendarDecoratorToday(requireActivity(), calDay))

        //현재날짜 데이터 불러오기
        dataList = selectDate(year,month,day)
        if (dataList.size == 0) {//데이터 없을 때
            binding.linearParent.visibility = View.GONE//8
            binding.tvNoData.visibility = View.VISIBLE//0
            binding.linearExplain.visibility = View.GONE
            if (binding.btnShowDetail.text.toString()
                    .equals(getString(R.string.calendar_btn_detail_close))
            ) {
                binding.calendar.state().edit()
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit()
                binding.btnShowDetail.text = getString(R.string.calendar_btn_detail_show)
            }
        } else { //데이터 있을 때
            binding.linearParent.visibility = View.VISIBLE
            binding.tvNoData.visibility = View.GONE
            binding.recycler.smoothScrollToPosition(0)
//                binding.recycler.visibility = View.VISIBLE
        }
        calendarAdapter.replaceList(dataList)
        calculateSum() //날짜에 해당하는 총도전시간, 성공시간, 획득꽃 계산
    }
}
