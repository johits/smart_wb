package com.example.smart_wb

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_wb.SQLite.TimerData
import com.example.smart_wb.SQLite.TimerDbHelper
import com.example.smart_wb.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import kotlinx.android.synthetic.main.fragment_calendar.*

import java.util.*
import kotlin.collections.ArrayList


/**
 * 21-05-31 yama 달력
 * 달력에 스크린타임 성공 실패 표시
 * */
class FragmentCalendar : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentCalendar().apply {

            }
    }
    lateinit var calendarAdapter: CalendarAdapter
    var dataList = mutableListOf<TimerData>()

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root




//        binding.calendar.setHeaderTextAppearance(getCurrentDay())
//        binding.calendar.state().edit()
//            .setMaximumDate(
//                CalendarDay.from(
//                    getCurrentYear(),
//                    getCurrentMonth(),
//                    getCurrentDay()
//                )
//            )
//            .commit()

        binding.calendar.setOnDateChangedListener { widget, date, selected ->
            binding.calendar.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit()
            val year:String = date.year.toString()
            val month:String
            val day:String
            //ex String 06 과 6 은 다르다
            if(date.month<10){
                month="0"+date.month
            }else{
                month=date.month.toString()
            }
            if(date.day<10){
                day="0"+date.day
            }else{
                day=date.day.toString()
            }
            val result:String = year+"-"+month+"-"+day
            dataList.clear()
            dataList=selectDate(result)
            calendarAdapter.replaceList(dataList)
            binding.linear.visibility=View.VISIBLE
            calculateSum()
        }

        //타이틀을 누르면 월간단위로 보여지게 변경
        binding.calendar.setOnTitleClickListener {
            binding.calendar.state().edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()
            binding.linear.visibility=View.GONE
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        screenTimeData()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.setLayoutManager(layoutManager)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.VERTICAL }

//        구분선 넣기 (Horizontal 인 경우 0, vertical인 경우 1 설정)
        binding.recycler.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

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

    //스크린타임 결과 데이터 가져오기
    fun screenTimeData() {
        Log.d(TAG, "screenTimeData: ")
//        var date = TimerSetShared.getDate(mContext)
//        var time = TimerSetShared.getTime(mContext)

        var timerDbHelper = TimerDbHelper(mContext, "timerDb.db", null, 1)
        var database = timerDbHelper.writableDatabase

        //   데이터 불러오기
        timerDataList = timerDbHelper.select()
//
        var dateList = ArrayList<String>()
        // 데이터 확인용 로그
        for (data in timerDataList) {
            var date: String = data.date
            dateList.add(date)
            Log.d(
                TAG,
                "id:" + data.id + " date:" + data.date + " time:" + data.time + " settingTime:" + data.settingTime + " success:" + data.success
            )
        }
        //중복값 지우기
        val linkedHashSet = LinkedHashSet<String>()
        for (item in dateList) {
            linkedHashSet.add(item)
        }

        for (item in linkedHashSet) {
            var parts = item.split("-").toTypedArray()
            Log.d(TAG, "중복 제거된 날짜:" + item)
            var year: Int = parts[0].toInt()
            var month: Int = parts[1].toInt()
            var date: Int = parts[2].toInt()
            var calDay = CalendarDay.from(year, month, date)
            binding.calendar.addDecorator(CalendarDecorator(requireActivity(), calDay))
        }

    }

    //날짜 클릭시 데이터 가져오기
    fun selectDate(date:String):MutableList<TimerData>{
        Log.d(TAG, "selectDate: ")
        dataList.clear()
        val timerDbHelper = TimerDbHelper(mContext, "timerDb.db", null, 1)
        var database = timerDbHelper.writableDatabase

        var arr:MutableList<TimerData> = timerDbHelper.select(date)
        val settingTimeSum:String
        return arr;
    }

    //총도전시간,성공시간,꽃 계산기
    fun calculateSum(){
        var settingTimeSum=0
        var successTimeSum=0
        var flowerSum=0
        for(data in dataList){
            settingTimeSum+=data.settingTime
            if(data.success==1){
                successTimeSum+=data.settingTime
                flowerSum+=data.flower
            }
        }
        Log.d(TAG, "총도전시간:$settingTimeSum 총성공시간:$successTimeSum 획득꽃:$flowerSum")
    }
}
