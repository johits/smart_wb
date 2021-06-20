package com.example.smart_wb

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.smart_wb.SQLite.ScreenTimeData
import com.example.smart_wb.SQLite.ScreenTimeDbHelper
import com.example.smart_wb.databinding.FragmentChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.DateFormat
import java.text.ParseException
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
    var sMonth: Int = 0 //이용자가 현재 보고 있는 주 시작 월
    var eMonth: Int = 0 //이용자가 현재 보고 있는 주 끝 월
    var sYear: Int = 0 //이용자가 현재 보고 있는 주 시작 년
    var eYear: Int = 0 //이용자가 현재 보고 있는 주 끝 년
    var sDay: Int = 0 //이용자가 현재 보고 있는 주 첫 날짜
    var eDay: Int = 0 //이용자가 현재 보고 있는 주 끝 날짜

    var value: Int = 0 //왼쪽, 오른쪽 버튼 비활성화시 데이터 남아있는지 확인하기 위해 주눈 구분값

    var lastDayF: Float = 0f //달의 마지막 날짜 float
    var lastDayI: Int = 0 //달의 마지막 날짜 Int

    //db에 들어있는 첫번째행 년,월,일
    var firstRowYear: Int = 0
    var firstRowMonth: Int = 0
    var firstRowDay: Int = 0

    //db에 들어있는 마지막행 년,월,일
    var lastRowYear: Int = 0
    var lastRowMonth: Int = 0
    var lastRowDay: Int = 0

    lateinit var startDt: String
    lateinit var endDt: String

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
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFirstLast()


//        date.text = toDays() + " ~ " + Days7(1) //기본 날짜 세팅 (주)
                toDays()?.let {calWeek(it)} //이번 주 시작일자 끝일자 구해주는 메서드
                date.text = startDt + " ~ " + endDt //기본 날짜 세팅 (주)
                weekParse()
                Refresh(type, year, month,start,end)
                leftVisible()
                rightVisible()

//        date.text =    toDays()?.let { calWeek(it) } + " ~ " + Days7(1) //기본 날짜 세팅 (주)



        chart_week.setOnClickListener(View.OnClickListener {
            chart_week.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
            type = "week"
            toDays()?.let {calWeek(it)} //이번 주 시작일자 끝일자 구해주는 메서드
            date.text = startDt + " ~ " + endDt
//            date.text = toDays() + " ~ " + Days7(1) //기본 날짜 세팅 (주)
            weekParse() // 주 날짜 파싱
            Refresh(type, year, month,start,end) // 그래프 새로고침
            leftVisible()
            rightVisible()

            //월, 년 인덱스값 초기화
            m=0
            y=0
            i=0
        })




        chart_month.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#2FA9FF"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
            type = "month"
            date.text = Month(0)
            monthParse() // 월 날짜 파싱
            val value = calLastDay(year, month)
            lastDayF = value.toFloat()
            Log.d(TAG, "lastDay:$lastDayF")
            Refresh(type, year, month, 0, 0) // 그래프 새로고침

            leftVisible()
            rightVisible()

            //일, 년 인덱스값 초기화
            y=0
            i=0
            m=0

            //sqlite 준비
            val screenTimeDbHelper =
                ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
            var database = screenTimeDbHelper.writableDatabase


//            //반복문 이용 더미데이터 인서트
//            screenTimeDbHelper.chartInsert(2020, 1, 14, "18:06:00", 3600*502)
//            screenTimeDbHelper.chartInsert(2020, 2, 14, "18:06:00", 3600*684)
//            screenTimeDbHelper.chartInsert(2020, 3, 14, "18:06:00", 3600*307)
//            screenTimeDbHelper.chartInsert(2020, 4, 14, "18:06:00", 3600*399)
//            screenTimeDbHelper.chartInsert(2020, 4, 26, "18:06:00", 3600*399)
//            screenTimeDbHelper.chartInsert(2020, 4, 27, "18:06:00", 3600*399)
//            screenTimeDbHelper.chartInsert(2020, 4, 30, "18:06:00", 3600*399)
//            screenTimeDbHelper.chartInsert(2020, 5, 14, "18:06:00", 3600*523)
//            screenTimeDbHelper.chartInsert(2020, 6, 14, "18:06:00", 3600*419)
//            screenTimeDbHelper.chartInsert(2020, 7, 14, "18:06:00", 3600*700)
//            screenTimeDbHelper.chartInsert(2020, 8, 14, "18:06:00", 3600*139)
//            screenTimeDbHelper.chartInsert(2020, 9, 14, "18:06:00", 3600*385)
//            screenTimeDbHelper.chartInsert(2020, 10, 14, "18:06:00", 3600*573)
//            screenTimeDbHelper.chartInsert(2020, 11, 14, "18:06:00", 3600*103)
//            screenTimeDbHelper.chartInsert(2020, 12, 14, "18:06:00", 3600*684)
//
//
//            screenTimeDbHelper.chartInsert(2021, 1, 14, "18:06:00", 3600*400)
//            screenTimeDbHelper.chartInsert(2021, 2, 14, "18:06:00", 3600*207)
//            screenTimeDbHelper.chartInsert(2021, 3, 14, "18:06:00", 3600*502)
//            screenTimeDbHelper.chartInsert(2021, 4, 14, "18:06:00", 3600*309)
//            screenTimeDbHelper.chartInsert(2021, 4, 26, "18:06:00", 3600*3)
//            screenTimeDbHelper.chartInsert(2021, 4, 27, "18:06:00", 3600*4)
//            screenTimeDbHelper.chartInsert(2021, 4, 30, "18:06:00", 3600*7)
//
//
//            screenTimeDbHelper.chartInsert(2021, 5, 17, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 5, 20, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 5, 24, "18:06:00", 7200)
//            screenTimeDbHelper.chartInsert(2021, 5, 28, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 5, 30, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 5, 31, "18:06:00", 7200)
//
//
//            screenTimeDbHelper.chartInsert(2021, 6, 1, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 1, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 3, "18:06:00", 7200)
//
//
//            screenTimeDbHelper.chartInsert(2021, 6, 7, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 9, "18:06:00", 7200)
//            screenTimeDbHelper.chartInsert(2021, 6, 11, "18:06:00", 10800)
//
//
//            screenTimeDbHelper.chartInsert(2021, 6, 14, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 15, "18:06:00", 3600*2)
//            screenTimeDbHelper.chartInsert(2021, 6, 16, "18:06:00", 3600*3)
//            screenTimeDbHelper.chartInsert(2021, 6, 17, "18:06:00", 3600*4)
//            screenTimeDbHelper.chartInsert(2021, 6, 18, "18:06:00", 3600*5)
//            screenTimeDbHelper.chartInsert(2021, 6, 19, "18:06:00", 3600*6)
//            screenTimeDbHelper.chartInsert(2021, 6, 20, "18:06:00", 3600*7)
//
//            screenTimeDbHelper.chartInsert(2021, 6, 21, "18:06:00", 3600*7)
//            screenTimeDbHelper.chartInsert(2021, 6, 22, "18:06:00", 3600*6)
//            screenTimeDbHelper.chartInsert(2021, 6, 23, "18:06:00", 3600*5)
//            screenTimeDbHelper.chartInsert(2021, 6, 24, "18:06:00", 3600*4)
//            screenTimeDbHelper.chartInsert(2021, 6, 25, "18:06:00", 3600*3)
//            screenTimeDbHelper.chartInsert(2021, 6, 26, "18:06:00", 3600*2)
//            screenTimeDbHelper.chartInsert(2021, 6, 27, "18:06:00", 3600*1)
//
//            screenTimeDbHelper.chartInsert(2021, 6, 28, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 29, "18:06:00", 3600*2)
//            screenTimeDbHelper.chartInsert(2021, 6, 30, "18:06:00", 3600*3)


//            screenTimeDbHelper.chartInsert(2021, 7, 1, "18:06:00", 3600*4)
//            screenTimeDbHelper.chartInsert(2021, 7, 6, "18:06:00", 3600*5)
//            screenTimeDbHelper.chartInsert(2021, 7, 10, "18:06:00", 3600*6)
//            screenTimeDbHelper.chartInsert(2021, 7, 18, "18:06:00", 3600*7)
//
//            screenTimeDbHelper.chartInsert(2021, 8, 14, "18:06:00", 3600*30)
//            screenTimeDbHelper.chartInsert(2021, 9, 14, "18:06:00", 3600*25)
//            screenTimeDbHelper.chartInsert(2021, 10, 14, "18:06:00", 3600*25)
//            screenTimeDbHelper.chartInsert(2021, 11, 14, "18:06:00", 3600*40)
//            screenTimeDbHelper.chartInsert(2021, 12, 14, "18:06:00", 3600*100)
//
//            screenTimeDbHelper.chartInsert(2022, 1, 14, "18:06:00", 3600*130)
//            screenTimeDbHelper.chartInsert(2022, 2, 14, "18:06:00", 3600*330)
//            screenTimeDbHelper.chartInsert(2022, 3, 14, "18:06:00", 3600*230)
//            screenTimeDbHelper.chartInsert(2022, 4, 14, "18:06:00", 3600*425)
//            screenTimeDbHelper.chartInsert(2022, 5, 14, "18:06:00", 3600*255)
//            screenTimeDbHelper.chartInsert(2022, 6, 14, "18:06:00", 3600*600)
//            screenTimeDbHelper.chartInsert(2022, 7, 14, "18:06:00", 3600*702)
//            screenTimeDbHelper.chartInsert(2022, 8, 14, "18:06:00", 3600*458)
//            screenTimeDbHelper.chartInsert(2022, 9, 14, "18:06:00", 3600*432)
//            screenTimeDbHelper.chartInsert(2022, 10, 14, "18:06:00", 3600*297)
//            screenTimeDbHelper.chartInsert(2022, 11, 14, "18:06:00", 3600*683)
//            screenTimeDbHelper.chartInsert(2022, 12, 14, "18:06:00", 3600*702)



        })

        chart_year.setOnClickListener(View.OnClickListener {
            chart_year.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            type = "year"
            date.text = Year(0)
            yearParse() // 년 날짜 파싱
            Refresh(type, year, 0,0,0) // 그래프 새로고침
            leftVisible()
            rightVisible()
//            chart.xAxis.valueFormatter = MyXAxisFormatter() // X축 값 바꿔주기 위함 (ex- 월, 화, 수, 목)
//            chart.invalidate() // 새로 고침

            //일, 월, 년 인덱스값 초기화
            i=0
            m=0
            y=0
        })

//이전 이후
        left.setOnClickListener {
            if (type.equals("week")) {
                i -= 1
                Days7(i)?.let { it1 -> calWeek(it1) }
                date.text = startDt + " ~ " + endDt //기본 날짜 세팅 (주)
                weekParse() // 주 날짜 파싱
                Refresh(type, year, month,start,end) // 그래프 새로고침
                //2021-06-17 기존코드 joker
//                i -= 1
//                date.text = Days7(i) + " ~ " + Days7(i + 1)
//                weekParse() // 주 날짜 파싱
//                Refresh(type, year, month,start,end) // 그래프 새로고침

            } else if (type.equals("month")) {
                m -= 1
                date.text = Month(m)
                Log.d(TAG, "lastDay:$lastDayF , year:$year , month:$month")
                monthParse()
                val value = calLastDay(year, month)
                lastDayF = value.toFloat()
                Log.d(TAG, "lastDay:$lastDayF , year:$year , month:$month")
                Refresh(type, year, month, 0, 0)
                leftVisible() //이전 데이터 없으면 왼쪽 버튼 비활성화
                rightVisible() //이후 데이트 없으면 오른쪽 버튼 비활성화

            } else if (type.equals("year")) {
                y -= 1
                date.text = Year(y)
                yearParse()
                Refresh(type, year, 0,0,0)
            }

            rightVisible()
            leftVisible()
        }

        right.setOnClickListener {
            if (type == "week") {
                i += 1
                Days7(i)?.let { it1 -> calWeek(it1) }
                date.text = startDt + " ~ " + endDt //기본 날짜 세팅 (주)
                weekParse() // 주 날짜 파싱
                Refresh(type, year, month,start,end) // 그래프 새로고침

                //2021-06-17 기존코드 joker
//                i += 1
//                date.text = Days7(i) + " ~ " + Days7(i + 1)
//                weekParse() // 주 날짜 파싱
//                Refresh(type, year, month,start,end) // 그래프 새로고침

            } else if (type == "month") {
                m += 1
                date.text = Month(m)
                Log.d(TAG, "lastDay:$lastDayF , year:$year , month:$month")
                monthParse()
                val value = calLastDay(year, month)
                lastDayF = value.toFloat()
                Log.d(TAG, "lastDay:$lastDayF , year:$year , month:$month")
                Refresh(type, year, month, 0, 0)



            } else if (type == "year") {
                y += 1
                date.text = Year(y)
                yearParse()
                Refresh(type, year, 0,0,0)

            }
            leftVisible() //이전 데이터 없으면 왼쪽 버튼 비활성화
            rightVisible() //이후 데이트 없으면 오른쪽 버튼 비활성화
        }


    }


//    fun Days7(i: Int): String? {
//        //주 단위 계산 메서드
//        val week = Calendar.getInstance()
//        week.add(Calendar.DATE, 7 * i)
//        return SimpleDateFormat("yyyy년 MM월 dd일").format(week.time)
//    }

    fun Days7(i: Int):String?{
        //주 단위 계산 메서드
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 7 * i)
        return SimpleDateFormat("yyyy-MM-dd").format(week.time)
    }




    fun toDays(): String? {
        //오늘 날짜 메서드
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 0)
        return SimpleDateFormat("yyyy-MM-dd").format(week.time)
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


    //그래프 새로고침 메서드

    fun Refresh(type: String, year: Int, month: Int, start:Int, end:Int) {


        val weeklabels = arrayOf(
           "월", "화","수","목","금","토","일"
        )
        val yearlabels = arrayOf(
            "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
        )
        val monthLabels = Array(lastDayI,{""})
        for(i in 0 until lastDayI){
            monthLabels[i]= (i+1).toString()
        }


        val entries = ArrayList<BarEntry>()
        var md : String
        var wd : String
        var sm : String
        var em : String


        if (type.equals("week")) {


            //x축 value 고정 시키기 위함 (x축 세팅)
//            for (i in 0 until 7){
//                entries.add(BarEntry(0f*i, null))
//                Log.d(TAG, "Refresh: 횟수 $i")
//            }
            entries.add(BarEntry(0f, null))
            entries.add(BarEntry(1f, null))
            entries.add(BarEntry(2f, null))
            entries.add(BarEntry(3f, null))
            entries.add(BarEntry(4f, null))
            entries.add(BarEntry(5f, null))
            entries.add(BarEntry(6f, null))


            if(sMonth!=eMonth){
                Log.d(TAG, "Refresh: 불일치!!!!")

                if (0 < sMonth && sMonth < 10) {
                    sm = "0" + sMonth
                } else {
                    sm = sMonth.toString()
                }
                for (sweek in SmonthSelectData(sYear, sMonth, start)) {
                    var w = sweek.day //날짜
                    var t = sweek.settingTime?.let { changeTime(it) } //성공시간

                    if (0 < w!! && w < 10) {
                        wd = "0" + w
                    } else {
                        wd = w.toString()
                    }


                    if (whatDay("$sYear$sm$wd").equals("월")) {
                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$sYear$sm$wd").equals("화")) {
                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$sYear$sm$wd").equals("수")) {
                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$sYear$sm$wd").equals("목")) {
                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$sYear$sm$wd").equals("금")) {
                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$sYear$sm$wd").equals("토")) {
                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$sYear$sm$wd").equals("일")) {
                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    }

                }


                if (0 < eMonth && eMonth < 10) {
                    em = "0" + eMonth
                } else {
                    em = eMonth.toString()
                }
                for (eweek in  WeekSelectData(eYear, eMonth, 1, end)) {
                    var w = eweek.day //날짜
                    var t = eweek.settingTime?.let { changeTime(it) } //성공시간

                    if (0 < w!! && w < 10) {
                        wd = "0" + w
                    } else {
                        wd = w.toString()
                    }

                    if (whatDay("$eYear$em$wd").equals("월")) {
                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$eYear$em$wd").equals("화")) {
                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$eYear$em$wd").equals("수")) {
                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$eYear$em$wd").equals("목")) {
                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$eYear$em$wd").equals("금")) {
                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$eYear$em$wd").equals("토")) {
                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$eYear$em$wd").equals("일")) {
                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    }

                    Log.d(TAG, "Refresh: 이게 제일 중요 $entries")
                }



            }else if(sMonth==eMonth){
                if (0 < month && month < 10) {
                    md = "0" + month
                } else {
                    md = month.toString()
                }
                for (week in WeekSelectData(year, month, start, end)) {
                    var w = week.day //날짜
                    var t = week.settingTime?.let { changeTime(it) } //성공시간

                    if (0 < w!! && w < 10) {
                        wd = "0" + w
                    } else {
                        wd = w.toString()
                    }


                    if (whatDay("$year$md$wd").equals("월")) {
                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$year$md$wd").equals("화")) {
                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$year$md$wd").equals("수")) {
                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$year$md$wd").equals("목")) {
                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$year$md$wd").equals("금")) {
                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$year$md$wd").equals("토")) {
                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (whatDay("$year$md$wd").equals("일")) {
                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    }

                }

            }
        } else if (type.equals("month")) {


            for (month in MonthSelectData(year, month)) {
                var t: Float? = month.settingTime?.let { changeTime(it) }
                val y: Float = t as Float

                val day: Int? = month.day
                for (i in 1 until lastDayF.toInt() + 1) {
                    if (day == i) {
                        entries.add(BarEntry(i * 1f, y))
                    } else {
                        entries.add(BarEntry(i * 1f, 0f))
                    }
                }

            }
        } else if (type.equals("year")) {
            //x축 고정 세팅하기 위함
            entries.add(BarEntry(0f, null))
            entries.add(BarEntry(1f, null))
            entries.add(BarEntry(2f, null))
            entries.add(BarEntry(3f, null))
            entries.add(BarEntry(4f, null))
            entries.add(BarEntry(5f, null))
            entries.add(BarEntry(6f, null))
            entries.add(BarEntry(7f, null))
            entries.add(BarEntry(8f, null))
            entries.add(BarEntry(9f, null))
            entries.add(BarEntry(10f, null))
            entries.add(BarEntry(11f, null))

            for (year in YearSelectData(year)) {
                var m = year.month
                var t = year.settingTime?.let { changeTime(it) }

                for(i in 1 until 13){
                    if(i==m){
                        entries.add(BarEntry(1f*(i-1), 1f* t!!))
//                        Log.d(TAG, "Refresh: 있음 월 $i t $t")
                    }else{

                    }
                }

//                entries.add(BarEntry(1f* m!!, 1f *t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }

        }
        var set = BarDataSet(entries, "DataSet")//데이터셋 초기화 하기
        set.color = ContextCompat.getColor(requireContext(), R.color.mainclolor)

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)

        val data = BarData(dataSet)
        data.setDrawValues(false)

        chart.run {
            this.data = data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            setDrawGridBackground(false) //격자 숨기기
            description.isEnabled = false //차트 옆에 별도로 표기되는 description이다. false로 설정하여 안보이게 했다.

            if(type.equals("week")){
                barData.setBarWidth(0.7f) //막대너비
            }else if(type.equals("year")){
                barData.setBarWidth(0.4f)
            }else if(type.equals("month")){
                barData.setBarWidth(0.5f)
            }
            axisLeft.run { // Y축에 대한 속성
                if(type.equals("week")||type.equals("month")){
                    axisMaximum = 24f //24시x31일(한달 최대일수) =744시간이라는 시간이 나와 최대 시간 750으로 설정해줌
                    axisMinimum = 0f // 최소값 0
                    granularity = 1f // 1 단위마다 선을 그리려고 granularity 설정 해 주었다.
                }else if(type.equals("year")){
                    axisMaximum = 750f //24시x31일(한달 최대일수) =744시간이라는 시간이 나와 최대 시간 750으로 설정해줌
                    axisMinimum = 0f // 최소값 0
                    granularity = 50f // 50 단위마다 선을 그리려고 granularity 설정 해 주었다.

                }


            }

            xAxis.run {// X축에 대한 속성
                position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
//                valueFormatter = MyXAxisFormatter() // X축 값 바꿔주기 위함

                if(type.equals("week")){
                    axisMaximum = 7f
//                    axisMinimum = 0f
//                    granularity = 0.3f //1일 간격
//                    labelCount = 7  //x축 라벨 나타내는 개수


                    setValueFormatter(IndexAxisValueFormatter(weeklabels)) //x축에 들어가는 week 값
//                    setGranularity(1f) //간격
//                    setGranularityEnabled(true)
                } else if (type.equals("month")) {
                    axisMaximum = lastDayF
//                    granularity = 1f
//                    labelCount =  30//x축 라벨 나타내는 개수
                    Log.d(TAG, "라벨수: $labelCount")
                    setValueFormatter(IndexAxisValueFormatter(monthLabels))
//                    setGranularity(1f)
//                    setGranularityEnabled(true)
                } else {
                    axisMaximum = 12f
//                    granularity = 1f
//                    labelCount = 12 //x축 라벨 나타내는 개수
                    setValueFormatter(IndexAxisValueFormatter(yearlabels)) //x축에 들어가는 week 값
                    setGranularity(0.5f)
               }
            }

            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정(차트 밑에 막대가 무엇인지 설명하는 것)
            chart.invalidate();                                 // 새로 고침
        }
    }

    //달 마지막날 구하기
    fun calLastDay(year: Int, month: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 15, 0, 0, 0)//month는 -1해줘야 해당월로 인식
        var lastDay: String = cal.getActualMaximum(Calendar.DAY_OF_MONTH).toString() + "f"
        lastDayI = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        return lastDay
    }

    //첫번째행 년,월,일 , 마지막행 년,월,일 불러오기
    fun loadFirstLast() {
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)

//        screenTimeDbHelper.chartInsert(2020,7,15, "22:00:00", 20000)
//        screenTimeDbHelper.chartInsert(2021,5,15, "22:00:00", 20000)
//        screenTimeDbHelper.chartInsert(2021,6,20, "22:00:00", 20000)
//        screenTimeDbHelper.chartInsert(2021,10,21, "22:00:00", 20000)

        Log.d(TAG, "첫,끝 데이터 가져오기 로드")
        //첫번째 데이터 , 마지막 데이터 불러오기
        val firstRow = screenTimeDbHelper.firstRow()
        val lastRow = screenTimeDbHelper.lastRow()
        if (firstRow.size > 0 && lastRow.size > 0) {
            firstRowYear = firstRow[0].year!!
            firstRowMonth = firstRow[0].month!!
            firstRowDay = firstRow[0].day!!
            lastRowYear = lastRow[0].year!!
            lastRowMonth = lastRow[0].month!!
            lastRowDay = lastRow[0].day!!
        }
        //Log.d(TAG, "첫번째행:${firstRow.size} , 마지막행:${lastRow.size}")
    }

    //왼쪽버튼 데이터 유무에 따른 visible or gone
    fun leftVisible() {

        if(type.equals("week")){
            if (firstRowYear != 0 && firstRowDay != 0 && firstRowMonth != 0) {
                left.visibility = View.VISIBLE
                left2.visibility = View.INVISIBLE
                        if (firstRowMonth == sMonth) {
                            if(firstRowDay>sDay-7 &&firstRowDay<sDay&& value==0){ //fRD=28일 sDAY-7= 24 sDay=31
                                Log.d(TAG, "작동2")
                                left.visibility = View.VISIBLE
                                left2.visibility = View.INVISIBLE
                                value=1
                                Log.d(TAG, "활성 fRD $firstRowDay sDay $sDay sDay-7 ${sDay-7} value $value")
                            }else if(firstRowDay>sDay&& value==1){
                                left.visibility = View.INVISIBLE
                                left2.visibility = View.VISIBLE
                                value=0
                                Log.d(TAG, "비활성 fRD $firstRowDay sDay $sDay sDay-7 ${sDay-7} value $value")
                            } else if(firstRowDay==sDay){
                                left.visibility = View.INVISIBLE
                                left2.visibility = View.VISIBLE
                                Log.d(TAG, "leftVisible: 비활성")
                            }
                        }
            }else{
                left.visibility = View.INVISIBLE
                left2.visibility = View.VISIBLE
            }
        }
        else if(type.equals("year")){
            if (firstRowYear != 0 && firstRowDay != 0 && firstRowMonth != 0) {
            if (firstRowYear == year) {
                        left.visibility = View.INVISIBLE
                left2.visibility = View.VISIBLE
                Log.d(TAG, "leftVisible: 비활성 년도 왼쪽")
                }else{
                left.visibility = View.VISIBLE
                left2.visibility = View.INVISIBLE
            }
            }
        }else if(type.equals("month")){
            Log.d(TAG, "왼쪽 버튼// 첫번째 달:$firstRowMonth , 현재 달:$month")
            if (firstRowYear != 0 && firstRowDay != 0 && firstRowMonth != 0) {
                if (firstRowYear < year) {
                    left.visibility = View.VISIBLE
                    left2.visibility = View.INVISIBLE
                } else if (firstRowYear == year) {
                    if (firstRowMonth < month) {
                        left.visibility = View.VISIBLE
                        left2.visibility = View.INVISIBLE
                    } else if (firstRowMonth == month) {
                        left.visibility = View.INVISIBLE
                        left2.visibility = View.VISIBLE
                    }
                }

            }else{
                left.visibility = View.GONE
                left2.visibility = View.VISIBLE
            }
        }

    }

    //오른쪽버튼 데이터 유무에 따른 visible or gone
    fun rightVisible() {

        if(type.equals("week")){
            if (lastRowYear != 0 && lastRowDay != 0 && lastRowMonth != 0) {
                right.visibility = View.VISIBLE
                right2.visibility = View.INVISIBLE
                if (lastRowYear == eYear) {
                    if (lastRowMonth < eMonth) {
                        right.visibility = View.VISIBLE
                        right2.visibility = View.INVISIBLE
                    } else if (lastRowMonth == eMonth) {
                        if (lastRowDay < eDay||lastRowDay==eDay) { //ex eDay =11일 eDay+7 = 18일 lRD= 14일
                            right.visibility = View.INVISIBLE
                            right2.visibility = View.VISIBLE
                        }else{
                            right.visibility = View.VISIBLE
                            right2.visibility = View.INVISIBLE
                        }
                    }
                }

            }else{
                right.visibility = View.INVISIBLE
                right2.visibility = View.VISIBLE
            }
        }else if(type.equals("year")){
            if (lastRowYear != 0) {
                if (lastRowYear == year) {
                    right.visibility = View.INVISIBLE
                    right2.visibility = View.VISIBLE
                    Log.d(TAG, "leftVisible: 비활성 년도 오른쪽")
                }else{
                    right.visibility = View.VISIBLE
                    right2.visibility = View.INVISIBLE
                }
            }
        }else if(type.equals("month")){
            Log.d(TAG, "오른쪽 버튼 // 마지막 달:$lastRowMonth , 현재 달:$month")
            if (lastRowYear != 0) {
                if (lastRowYear > year) {
                    right.visibility = View.VISIBLE
                    right2.visibility = View.INVISIBLE
                } else {
                    if (lastRowMonth > month) {
                        right.visibility = View.VISIBLE
                        right2.visibility = View.INVISIBLE
                    } else {
                        right.visibility = View.INVISIBLE
                        right2.visibility = View.VISIBLE
                    }
                }
            }else{
                right.visibility=View.INVISIBLE
                right2.visibility = View.VISIBLE
            }
        }




    }

    //년 날짜 파싱
    fun yearParse(){
        year = Integer.parseInt(date.text.toString().replace("년", "")) //ex)2021년 -> 년 제거
    }

    //월 날짜 파싱
    fun monthParse(){
        Log.d(TAG, "날짜 파싱 작동")
        year = Integer.parseInt(date.text.toString().substring(0, date.text.toString().indexOf("년")))
        var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
        month = Integer.parseInt(date.text.toString().slice(ran))
    }

    //주 날짜 파싱
    fun weekParse(){
        //파싱전 예시 2021년 06월 14일 ~ 2021년 06월 20일
        Log.d(TAG, "weekParse: "+date.text.toString())
        year = Integer.parseInt(date.text.toString().substring(0, date.text.toString().indexOf("년")))
        var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
        month = Integer.parseInt(date.text.toString().slice(ran))
        var s = IntRange(10, 11)
        start = Integer.parseInt(date.text.toString().slice(s))
        var e = IntRange(26, 27)
        end = Integer.parseInt(date.text.toString().slice(e))
        var sm = IntRange(6, 7)
        sMonth = Integer.parseInt(date.text.toString().slice(sm))
        var em = IntRange(22, 23)
        eMonth = Integer.parseInt(date.text.toString().slice(em))
        var sy = IntRange(0, 3)
        sYear = Integer.parseInt(date.text.toString().slice(sy))
        var ey = IntRange(16, 19)
        eYear = Integer.parseInt(date.text.toString().slice(ey))
        var sd = IntRange(10, 11)
        sDay = Integer.parseInt(date.text.toString().slice(sd))
        var ed = IntRange(26, 27)
        eDay = Integer.parseInt(date.text.toString().slice(ed))
    }

    //시간 변환


    fun changeTime(settingTime: Int): Float {
        val result: Float?
        var test = settingTime / 60
        result = test.toFloat()/ 60

        return result
    }


    //년도 sqlite data 불러오기 메서드
    fun YearSelectData(y: Int): ArrayList<ScreenTimeData> {

        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        yearlist = screenTimeDbHelper.year(y) // y=year 불러올 연도 입력
        return yearlist

    }


    //월별 sqlite data 불러오기 메서드
    fun MonthSelectData(y: Int, m: Int): ArrayList<ScreenTimeData> {
        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        monthlist = screenTimeDbHelper.month(y, m) // y=year 불러올 연도 입력, m=month 불러올 월 입력
        return monthlist

    }



    //주별 sqlite data 불러오기 메서드
    fun WeekSelectData(y: Int, m: Int, s:Int, e:Int): ArrayList<ScreenTimeData> { // y=year, m=month, s=start(시작날짜), e=end(끝날짜)

        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        weeklist = screenTimeDbHelper.week(y,m,s,e)
        return weeklist

    }

    //주별 시작 달 끝 달 다를 경우 시작 날짜에 대한 데이터 불러오기 메서드
    fun SmonthSelectData(y: Int, m: Int, s:Int): ArrayList<ScreenTimeData> { // y=year, sm=start month, s=start(시작날짜)

        val ad = Calendar.getInstance()
        ad.add(Calendar.MONTH, m-1)
        var ld:Int = ad.getActualMaximum(Calendar.DAY_OF_MONTH);    // 마지막 날짜 반환 (2018년 9월 30일)

        Log.d(TAG, "막날 $ld")



        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        weeklist = screenTimeDbHelper.sMonthweek(y,m,s,ld)
        return weeklist

    }

    //일수 구하기 메서드
    fun alldate(): ArrayList<String> {
        var result : Int = 0
        var a = Integer.parseInt(date.text.toString().substring(0, date.text.toString().indexOf("년")))
        var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
        var b = Integer.parseInt(date.text.toString().slice(ran))


        val ad = Calendar.getInstance()
        ad.add(Calendar.MONTH, b-1)
        var dayOfMonth:Int = ad.getActualMaximum(Calendar.DAY_OF_MONTH);    // 마지막 날짜 반환 (2018년 9월 30일)

        Log.d(TAG, "막날 $dayOfMonth")
//        reseult = Integer.parseInt(ad.set(Calendar.DAY_OF_MONTH, dayOfMonth))

        val month = arrayListOf<String>()
        for(i in 1 until dayOfMonth){
           month[i-1]=i.toString()
        }
        return month
    }




    //일주일 계산하기(eventDate = "2021-06-07")
    fun calWeek(eventDate: String){
        val dateArray = eventDate.split("-").toTypedArray()
        val cal = Calendar.getInstance()
        cal [dateArray[0].toInt(), dateArray[1].toInt() - 1] =
            dateArray[2].toInt()
        var inputDt = cal.getTime() //입력된 날짜
        Log.d(TAG, "calWeek: 입력된 날짜 ${cal.getTime()}")

        cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        var inputSunday = cal.getTime()
        System.out.println("입력된 날짜의 일요일  : " + cal.getTime());
        if(inputDt==inputSunday){
            cal.add(Calendar.DATE, -7);
            cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            System.out.println("입력된 날짜의 이전주의 일요일 : " + cal.getTime());
        }
        // 일주일의 첫날을 월요일로 지정한다
         cal.firstDayOfWeek = Calendar.MONDAY
        // 시작일과 특정날짜의 차이를 구한다
         val dayOfWeek = cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek
        // 해당 주차의 첫째날을 지정한다
         cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek)
        val sf = SimpleDateFormat("yyyy년 MM월 dd일")
        // 해당 주차의 첫째 날짜
         startDt = sf.format(cal.time)
        // 해당 주차의 마지막 날짜 지정
         cal.add(Calendar.DAY_OF_MONTH, 6)
        // 해당 주차의 마지막 날짜
         endDt = sf.format(cal.time)
         Log.d(TAG, "특정 날짜 = [$eventDate] >> 시작 날짜 = [$startDt], 종료 날짜 = [$endDt]")


    }



    //요일 구하기
    @SuppressLint("SimpleDateFormat")
    fun whatDay(d:String):String{ //ex) date = 20170418
        var dayresult : String =""
        val df: DateFormat = SimpleDateFormat("yyyyMMdd")
        var date: Date? = null
        try {
            date = df.parse(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val calendar = Calendar.getInstance()
        calendar.time = date


        when (calendar[Calendar.DAY_OF_WEEK]) {
            1 -> dayresult = "일"
            2 -> dayresult = "월"
            3 -> dayresult = "화"
            4 -> dayresult = "수"
            5 -> dayresult = "목"
            6 -> dayresult = "금"
            7 -> dayresult = "토"
        }
//        println("몇요일" + dayresult)
        return dayresult

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







