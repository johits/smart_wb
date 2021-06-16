package com.example.smart_wb

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.smart_wb.SQLite.ScreenTimeData
import com.example.smart_wb.SQLite.ScreenTimeDbHelper
import com.example.smart_wb.databinding.FragmentChartBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**2021-06-14
joker
막대 그래프*/

class FragmentChart : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var i = 0 // 주 단위 계산에 필요한 변수
    var m = 0 // 월 단위 계산에 필요한 변수
    var y = 0 // 년 단위 계산에 필요한 변수

    var type = "week" // 주, 월, 년 타입 변수 (default : "week")

    var year: Int = 0 //이용자가 현재 보고 있는 년
    var month: Int = 0 //이용자가 현재 보고 있는 월
    var start: Int = 0 //이용자가 현재 보고 있는 주 시작 날짜
    var end: Int = 0 //이용자가 현재 보고 있는 주 끝 날짜

    var mMonthSelect: Int? = null //year sqlite DATA 불러오기 월 변수
    var mTimeSeletc: Int? = null //year sqlite DATA 불러오기 설정시간 변수


    private val TAG = "FragmentChart"

    private lateinit var cContext: Context

    lateinit var yearlist: ArrayList<ScreenTimeData>
    lateinit var monthlist: ArrayList<ScreenTimeData>
    lateinit var weeklist: ArrayList<ScreenTimeData>

    //뷰바인딩 위한 변수
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            cContext = context
        }
    }


    inner class MyXAxisFormatter : ValueFormatter() {
        private val days = arrayOf("월", "화", "수", "목", "금", "토", "일")
        private val year =
            arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        private val month = arrayOf("")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            if (type.equals("week")) {
                return days.getOrNull(value.toInt() - 1) ?: value.toString()
            } else if (type.equals("month")) {
                return month.getOrNull(value.toInt() - 1) ?: value.toString()
            } else {
                return year.getOrNull(value.toInt() - 1) ?: value.toString()
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        date.text = toDays() + " ~ " + Days7(1) //기본 날짜 세팅 (주)


        chart_week.setOnClickListener(View.OnClickListener {
            chart_week.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
            type = "week"
            date.text = toDays() + " ~ " + Days7(1) //기본 날짜 세팅 (주)

            Log.d(TAG, "onViewCreated: ${date.text}")
            year = Integer.parseInt(date.text.toString().substring(0, date.text.toString().indexOf("년")))
            Log.d(TAG, "그래서 년도 뭐임 $year")
            var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
            month = Integer.parseInt(date.text.toString().slice(ran))
            Log.d(TAG, "그래서 월 뭐임 $month")
            var s = IntRange(10, 11) // ex 2021년 06월 <-인덱스 6,7값만 포함
            start = Integer.parseInt(date.text.toString().slice(s))
            Log.d(TAG, "그래서 시작 뭐임 $start")
            var e = IntRange(26, 27) // ex 2021년 06월 <-인덱스 6,7값만 포함
            end = Integer.parseInt(date.text.toString().slice(e))
            Log.d(TAG, "그래서 끝 뭐임 $end")

            Refresh(
                type, year, month, start, end
            ) // 그래프 새로고침 메서드

        })




        chart_month.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#2FA9FF"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
            type = "month"
            date.text = Month(0)

//            val result: String = str.substring(str.lastIndexOf("/") + 1)
            Log.d(TAG, "날짜 불러와!!!!!!!!!!!: ${date.text}")
            //특정 문자열 변경 실시

            year = Integer.parseInt(date.text.toString().substring(0, date.text.toString().indexOf("년")))
            Log.d(TAG, "그래서 년도 뭐임 $year")
            var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
            month = Integer.parseInt(date.text.toString().slice(ran))
            Log.d(TAG, "그래서 월 뭐임 $month")

            Refresh(type, year, month,0,0) // 그래프 새로고침 메서드


            //sqlite 준비
            val screenTimeDbHelper =
                ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
            var database = screenTimeDbHelper.writableDatabase
//
//
//
//            //반복문 이용 더미데이터 인서트
//
//                screenTimeDbHelper.chartInsert(2020, 1, 13, "18:06:00", 3600)
//                screenTimeDbHelper.chartInsert(2020, 2, 14, "18:06:00", 4800)
//                screenTimeDbHelper.chartInsert(2019, 3, 15, "18:06:00", 3600)
//                screenTimeDbHelper.chartInsert(2021, 5, 18, "18:06:00", 7200)
//                screenTimeDbHelper.chartInsert(2021, 10, 20, "18:06:00", 3600)
//                screenTimeDbHelper.chartInsert(2021, 10, 23, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 11, 13, "18:06:00", 100)
//            screenTimeDbHelper.chartInsert(2021, 12, 14, "18:06:00", 200)
//            screenTimeDbHelper.chartInsert(2021, 12, 15, "18:06:00", 300)
//            screenTimeDbHelper.chartInsert(2022, 5, 18, "18:06:00", 7200)
//            screenTimeDbHelper.chartInsert(2022, 10, 20, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2022, 10, 23, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 1, 13, "18:06:00", 100)
//            screenTimeDbHelper.chartInsert(2021, 2, 14, "18:06:00", 200)
//            screenTimeDbHelper.chartInsert(2021, 2, 15, "18:06:00", 300)
//            screenTimeDbHelper.chartInsert(2022, 1, 18, "18:06:00", 7200)
//            screenTimeDbHelper.chartInsert(2022, 3, 20, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2022, 4, 23, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 1, 13, "18:06:00", 200)
//            screenTimeDbHelper.chartInsert(2021, 1, 13, "18:06:00", 300)
//            screenTimeDbHelper.chartInsert(2021, 1, 13, "18:06:00", 200)

//                        screenTimeDbHelper.chartInsert(2021, 6, 5, "18:06:00", 5)
//            screenTimeDbHelper.chartInsert(2021, 6, 10, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 10, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 15, "18:06:00", 15)

//                                    screenTimeDbHelper.chartInsert(2021, 6, 16, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 16, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 16, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 17, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 17, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 19, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 22, "18:06:00", 10)
//            screenTimeDbHelper.chartInsert(2021, 6, 23, "18:06:00", 10)


        })

        chart_year.setOnClickListener(View.OnClickListener {
            chart_year.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            type = "year"
            date.text = Year(0)
            year = Integer.parseInt(date.text.toString().replace("년", "")) //ex)2021년 -> 년 제거
            Refresh(type, year, 0,0,0) // 그래프 새로고침 메서드

//            chart.xAxis.valueFormatter = MyXAxisFormatter() // X축 값 바꿔주기 위함 (ex- 월, 화, 수, 목)
//            chart.invalidate() // 새로 고침

        })


        left.setOnClickListener {
            if (type.equals("week")) {
                i -= 1
                date.text = Days7(i) + " ~ " + Days7(i + 1)
            } else if (type.equals("month")) {
                m -= 1
                date.text = Month(m)
            } else if (type.equals("year")) {
                y -= 1
                date.text = Year(y)
                year = Integer.parseInt(date.text.toString().replace("년", "")) //ex)2021년 -> 년 제거
                Refresh(type, year, 0,0,0) // 그래프 새로고침 메서드
            }

        }

        right.setOnClickListener {
            if (type == "week") {
                i += 1
                date.text = Days7(i) + " ~ " + Days7(i + 1)
            } else if (type == "month") {
                m += 1
                date.text = Month(m)
            } else if (type == "year") {
                y += 1
                date.text = Year(y)
                year = Integer.parseInt(date.text.toString().replace("년", "")) //ex)2021년 -> 년 제거
                Refresh(type, year, 0,0,0) // 그래프 새로고침 메서드
            }

        }


//        val entries = ArrayList<BarEntry>()
//        entries.add(BarEntry(1f,20.0f)) //x:x축 값 놓이는 위치 y:성공시간량
//        entries.add(BarEntry(2f,70.0f))
//        entries.add(BarEntry(3f,30.0f))
//        entries.add(BarEntry(4f,90.0f))
//        entries.add(BarEntry(5f,70.0f))
//        entries.add(BarEntry(6f,30.0f))
//        entries.add(BarEntry(7f,90.0f))


//        val barDataSet = BarDataSet(visitors, "성공 시간")
//        barDataSet.setColors(Color.parseColor("#2FA9FF"))
//        barDataSet.valueTextColor = Color.BLACK
//        barDataSet.valueTextSize = 16f
//
//        val barData = BarData(barDataSet)
//
//        chart.setFitBars(true)
//        chart.data = barData
//        chart.description.text = ""
//        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM //x축을 하단으로 내린다
//        chart.axisRight.isEnabled = false //오른쪽 y축 숨기기


//        chart.run{
//            setDrawGridBackground(false) //격자 숨기기
//
//            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
//                setDrawGridLines(true) // 격자(가로줄) 라인 활용
//                setDrawAxisLine(false) // 축 그리기 설정
//            }
//
//            xAxis.run {
//                position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
//                setDrawAxisLine(true) // 축 그림
//                setDrawGridLines(false) // 격자
//                valueFormatter = MyXAxisFormatter() // 축 라벨 값 바꿔주기 위함
//                textSize = 14f // 텍스트 크기
//            }
//            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
//            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
//            legend.isEnabled = false //차트 범례 설정
//
//        }


//        var set = BarDataSet(entries,"DataSet")//데이터셋 초기화 하기
//        set.color = ContextCompat.getColor(requireContext(),R.color.mainclolor)
//
//        val dataSet :ArrayList<IBarDataSet> = ArrayList()
//        dataSet.add(set)
//        val data = BarData(dataSet)
//        data.barWidth = 0.3f//막대 너비 설정하기

//        chart.run{
//            this.data = data //차트의 데이터를 data로 설정해줌.
//            setFitBars(true)
//            invalidate()
//            setDrawGridBackground(false) //격자 숨기기
//            description.text = "" //라벨 숨기기
//
//            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
//                setDrawGridLines(true) // 격자(가로줄) 라인 활용
//                setDrawAxisLine(false) // 축 그리기 설정
//            }
//
//            xAxis.run {
//                position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
//                setDrawAxisLine(true) // 축 그림
//                setDrawGridLines(false) // 격자
//                    valueFormatter = MyXAxisFormatter() // X축 값 바꿔주기 위함
//
////                textSize = 14f // 텍스트 크기
//            }
//            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
//            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
//            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
//            legend.isEnabled = false //차트 범례 설정(차트 밑에 막대가 무엇인지 설명하는 것)
//            chart.invalidate();                                 // 새로 고침
//        }

    }


    fun Days7(i: Int): String? {
        //주 단위 계산 메서드
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 7 * i)
        return SimpleDateFormat("yyyy년 MM월 dd일").format(week.time)
    }

    fun toDays(): String? {
        //오늘 날짜 메서드
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 0)
        return SimpleDateFormat("yyyy년 MM월 dd일").format(week.time)
    }

    fun Month(m: Int): String? {
        // 월 단위 계산 메서드
        val df: DateFormat = SimpleDateFormat("yyyy년 MM월")
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, m)
        return df.format(cal.time)
    }

    fun Year(y: Int): String? {
        // 년 단위 계산 메서드
        val df: DateFormat = SimpleDateFormat("yyyy년")
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, y)
        return df.format(cal.time)
    }

    fun Refresh(type: String, year: Int, month: Int, start:Int, end:Int) {

        val entries = ArrayList<BarEntry>()

        if (type.equals("week")) {
            Log.d(TAG, "이거 작동되냐? 위크")
            entries.add(BarEntry(1f, 20.0f)) //x:x축 값 놓이는 위치 y:성공시간량
            entries.add(BarEntry(2f, 70.0f))
            entries.add(BarEntry(3f, 30.0f))
            entries.add(BarEntry(4f, 90.0f))
            entries.add(BarEntry(5f, 70.0f))
            entries.add(BarEntry(6f, 30.0f))
            entries.add(BarEntry(7f, 90.0f))
            WeekSelectData(year,month,start,end)//DB 데이터 가져오기
        } else if (type.equals("month")) {
            MonthSelectData(year, month) //DB 데이터 가져오기

        } else if (type.equals("year")) {

            YearSelectData(year) //DB 데이터 가져오기

            var dateList = ArrayList<String>()
            // 데이터 확인용 로그

//            var mMonthSelect: Int?
//            var mTimeSeletc: Int?
            for (year in YearSelectData(year)) {
                mMonthSelect = year.month
                mTimeSeletc = year.settingTime

                Log.d(TAG, "중간 $mMonthSelect : $mTimeSeletc")

                entries.add(
                    BarEntry(
                        1f * this!!.mMonthSelect!!,
                        1f * mTimeSeletc!!
                    )
                ) //x:x축 값 놓이는 위치 y:성공시간량
//                for (mm in 1 until 12){
//                    if(mMonthSelect==mm) {
//                        Log.d(TAG, "month는: $mMonthSelect")
//                        entries.add(BarEntry(1f, 1f * mTimeSeletc!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    }
//                }
            }


//                dateList.add(date)
//            Log.d(
//                TAG,
//                "id:" + data.id + " date:" + data.date + " time:" + data.time + " settingTime:" + data.settingTime + " success:" + data.success
//            )


        }
        var set = BarDataSet(entries, "DataSet")//데이터셋 초기화 하기
        set.color = ContextCompat.getColor(requireContext(), R.color.mainclolor)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        val data = BarData(dataSet)

        chart.run {
            this.data = data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
            setDrawGridBackground(false) //격자 숨기기
            description.text = "" //라벨 숨기기

            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                setDrawGridLines(true) // 격자(가로줄) 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
            }

            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                valueFormatter = MyXAxisFormatter() // X축 값 바꿔주기 위함

//                textSize = 14f // 텍스트 크기
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정(차트 밑에 막대가 무엇인지 설명하는 것)
            chart.invalidate();                                 // 새로 고침
        }
    }

    //년도 sqlite data 불러오기 메서드
    fun YearSelectData(y: Int): ArrayList<ScreenTimeData> {

        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        yearlist = screenTimeDbHelper.year(y) // y=year 불러올 연도 입력
//        Log.d(TAG, "결과: $yearlist")
        return yearlist

    }

    //월별 sqlite data 불러오기 메서드
    fun MonthSelectData(y: Int, m: Int): ArrayList<ScreenTimeData> {

        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        monthlist = screenTimeDbHelper.month(y, m) // y=year 불러올 연도 입력, m=month 불러올 월 입력
//        Log.d(TAG, "결과: $yearlist")
        return monthlist

    }

    //주별 sqlite data 불러오기 메서드
    fun WeekSelectData(y: Int, m: Int, s:Int, e:Int): ArrayList<ScreenTimeData> { // y=year, m=month, s=start(시작날짜), e=end(끝날짜)

        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase

        //  timer 테이블 데이터 불러오기
        weeklist = screenTimeDbHelper.week(y,m,s,e)
//        Log.d(TAG, "결과: $yearlist")
        return weeklist

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentChart().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


