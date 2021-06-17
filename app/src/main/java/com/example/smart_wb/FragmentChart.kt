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
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
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

    lateinit var startDt : String
    lateinit var endDt : String

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

//            var a = Integer.parseInt(date.text.toString().substring(0, date.text.toString().indexOf("년")))
//        var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
//        var b = Integer.parseInt(date.text.toString().slice(ran))

//            for (i in 1 until alldate)arrayOf(""){}
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            if (type.equals("week")) {
                return days.getOrNull(value.toInt()-1) ?: value.toString()
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
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        date.text = toDays() + " ~ " + Days7(1) //기본 날짜 세팅 (주)
                toDays()?.let {calWeek(it)} //이번 주 시작일자 끝일자 구해주는 메서드
                date.text = startDt + " ~ " + endDt //기본 날짜 세팅 (주)
                weekParse()
                Refresh(type, year, month,start,end)
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

        })




        chart_month.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#2FA9FF"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
            type = "month"
            date.text = Month(0)
            monthParse() // 월 날짜 파싱
            Refresh(type, year, month,0,0) // 그래프 새로고침

            //sqlite 준비
            val screenTimeDbHelper =
                ScreenTimeDbHelper(requireContext(), "screenTimeDb.db", null, 1)
            var database = screenTimeDbHelper.writableDatabase

//            whatDay("2021년 06월 23일")
//            Log.d(TAG, "그래서 몇요일이냐고 "+whatDay("2021년 06월 23일"))
//
//
//
            //반복문 이용 더미데이터 인서트

//            screenTimeDbHelper.chartInsert(2021, 6, 13, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 13, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 14, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 15, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 17, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 18, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 20, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 20, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 20, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 24, "18:06:00", 3600)
//            screenTimeDbHelper.chartInsert(2021, 6, 27, "18:06:00", 14400)
//


        })

        chart_year.setOnClickListener(View.OnClickListener {
            chart_year.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            type = "year"
            date.text = Year(0)
            yearParse() // 년 날짜 파싱
            Refresh(type, year, 0,0,0) // 그래프 새로고침

//            chart.xAxis.valueFormatter = MyXAxisFormatter() // X축 값 바꿔주기 위함 (ex- 월, 화, 수, 목)
//            chart.invalidate() // 새로 고침

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
                monthParse()
                Refresh(type, year, month,0,0)

            } else if (type.equals("year")) {
                y -= 1
                date.text = Year(y)
                yearParse()
                Refresh(type, year, 0,0,0)
            }

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
                monthParse()
                Refresh(type, year, month,0,0)
            } else if (type == "year") {
                y += 1
                date.text = Year(y)
                yearParse()
                Refresh(type, year, 0,0,0)
            }

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



        val entries = ArrayList<BarEntry>()
        var md : String
        var wd : String
        if (type.equals("week")) {
//            entries.add(BarEntry(1f, 20.0f)) //x:x축 값 놓이는 위치 y:성공시간량

            if(0<month && month<10){
                md = "0"+month
            }else{
                md = month.toString()
            }
            for (week in WeekSelectData(year,month,start,end)) {
                var w = week.day //날짜
                var t = week.settingTime?.let { changeTime(it) } //성공시간

                if(0< w!! && w<10){
                    wd = "0"+w
                }else{
                    wd = w.toString()
                }

            if(whatDay("$year$month$w").equals("월")){
                entries.add(BarEntry(0f, 1f* t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }else if(whatDay("$year$month$w").equals("화")){
                entries.add(BarEntry(1f, 1f* t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }else if(whatDay("$year$month$w").equals("수")){
                entries.add(BarEntry(2f, 1f* t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }else if(whatDay("$year$month$w").equals("목")){
                entries.add(BarEntry(3f, 1f* t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }else if(whatDay("$year$month$w").equals("금")){
                entries.add(BarEntry(4f, 1f* t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }else if(whatDay("$year$month$w").equals("토")){
                entries.add(BarEntry(5f, 1f* t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }else if(whatDay("$year$month$w").equals("일")){
                entries.add(BarEntry(6f, 1f* t!!)) //x:x축 값 놓이는 위치 y:성공시간량
            }



            }


        } else if (type.equals("month")) {
            MonthSelectData(year, month) //DB 데이터 가져오기
            for (month in MonthSelectData(year,month)) {
                var t = month.settingTime?.let { changeTime(it) }
                Log.d(TAG, "Refresh 티값: $t")
            }
        } else if (type.equals("year")) {

            for (year in YearSelectData(year)) {
                var m = year.month
                var t = year.settingTime?.let { changeTime(it) }

                for(i in 1 until 12){
                    if(i==m){
                        entries.add(BarEntry(1f*(i-1), 1f* t!!))
                        Log.d(TAG, "Refresh: 있음 월 $i t $t")
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

        chart.run {
            this.data = data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            setDrawGridBackground(false) //격자 숨기기
            description.isEnabled = false //차트 옆에 별도로 표기되는 description이다. false로 설정하여 안보이게 했다.

            if(type.equals("week")){
                barData.setBarWidth(0.7f) //막대너비
            }else if(type.equals("year")){
                barData.setBarWidth(0.3f)
            }else if(type.equals("month")){
                barData.setBarWidth(0.1f)
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
//                    axisMaximum = 8f
//                    axisMinimum = 0f
//                    granularity = 0.3f //1일 간격
//                    labelCount = 7  //x축 라벨 나타내는 개수

                    setValueFormatter(IndexAxisValueFormatter(weeklabels)) //x축에 들어가는 week 값
                    setGranularity(1f)
                    setGranularityEnabled(true)
                }else if(type.equals("month")){
                    axisMaximum = 31f
                    granularity = 1f
                    labelCount = 31 //x축 라벨 나타내는 개수
                }else if(type.equals("year")) {
                    Log.d(TAG, "Refresh: 축바꾸자 엑스")
//                    axisMaximum = 12f
//                    granularity = 1f
//                    labelCount = 12 //x축 라벨 나타내는 개수
                    setValueFormatter(IndexAxisValueFormatter(yearlabels)) //x축에 들어가는 week 값
                    setGranularity(1f)
               }
//                textSize = 14f // 텍스트 크기
//                valueFormatter = MyXAxisFormatter() // X축 값 바꿔주기 위함
            }




            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정(차트 밑에 막대가 무엇인지 설명하는 것)
            chart.invalidate();                                 // 새로 고침
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
        cal[dateArray[0].toInt(), dateArray[1].toInt() - 1] =
            dateArray[2].toInt() // 일주일의 첫날을 일요일로 지정한다
        cal.firstDayOfWeek = Calendar.MONDAY // 시작일과 특정날짜의 차이를 구한다
        val dayOfWeek = cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek // 해당 주차의 첫째날을 지정한다
        cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek)
        val sf = SimpleDateFormat("yyyy년 MM월 dd일") // 해당 주차의 첫째 날짜
        startDt = sf.format(cal.time) // 해당 주차의 마지막 날짜 지정
        cal.add(Calendar.DAY_OF_MONTH, 6) // 해당 주차의 마지막 날짜
        endDt = sf.format(cal.time) //일주일의 마지막 날짜
        val dayWeek = cal.get(Calendar.DAY_OF_WEEK)
        //달력 마지막 날짜 구하기
        cal.set(2021, 1, 17, 0, 0, 0)
        var lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
//        Log.d(TAG, "마지막 날짜:$lastDay  특정 날짜 = [$eventDate] >> 시작 날짜 = [$startDt], 종료 날짜 = [$endDt]")

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
            1 -> dayresult = "월"
            2 -> dayresult = "화"
            3 -> dayresult = "수"
            4 -> dayresult = "목"
            5 -> dayresult = "금"
            6 -> dayresult = "토"
            7 -> dayresult = "일"
        }
        println("몇요일" + dayresult)
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







