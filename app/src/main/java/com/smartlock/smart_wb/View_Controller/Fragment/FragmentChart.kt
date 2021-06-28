package com.example.smart_wb.View_Controller.Fragment

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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.smartlock.smart_wb.Model.ChartModel
import com.smartlock.smart_wb.Model.Data.ChartData
import com.smartlock.smart_wb.R
import com.smartlock.smart_wb.View_Controller.Activity.MainActivity
import com.smartlock.smart_wb.databinding.FragmentChartBinding
import kotlinx.android.synthetic.main.fragment_chart.*


// TODO: Rename parameter arguments, choose names that match
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
    val chartmodel = ChartModel()
    val chartdata = ChartData()
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


        //차트 첫화면 세팅
        firstRowYear = chartmodel.loadFirstLast(cContext,"firstRowYear") //사용자 첫, 끝 데이터 가져오기
        firstRowMonth = chartmodel.loadFirstLast(cContext,"firstRowMonth")
        firstRowDay = chartmodel.loadFirstLast(cContext,"firstRowDay")
        lastRowYear = chartmodel.loadFirstLast(cContext,"lastRowYear")
        lastRowMonth = chartmodel.loadFirstLast(cContext,"lastRowMonth")
        lastRowDay = chartmodel.loadFirstLast(cContext,"lastRowDay")
        startDt = chartmodel.toDays()?.let { chartmodel.calWeek(it,"startDt") }.toString()
        endDt = chartmodel.toDays()?.let { chartmodel.calWeek(it,"endDt") }.toString()
        date.text = startDt + " ~ " + endDt //기본 날짜 세팅 (주)
        Weekparse()
        Refresh(type, year, month, start, end) //그래프 새로고침
        leftVisible() //이전 버튼 활성화
        rightVisible() //이후 버튼 활성화


        //출시 전까지 더미데이터 쌓는 버튼으로 사용
        chart_text.setOnClickListener {
        chartmodel.dummy(cContext) //더미데이터 쌓는 메서드
//            PointItemSharedModel.sumFlower(requireContext(),900)
        }

        //주 버튼 클릭 이벤트
        chart_week.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_month.setBackgroundResource(R.drawable.month0)
            chart_week.setTextColor(Color.parseColor("#FFFFFF"))
            chart_week.setBackgroundResource(R.drawable.week1)
            chart_year.setTextColor(Color.parseColor("#000000"))
            chart_year.setBackgroundResource(R.drawable.year0)


            type = "week"
            startDt = chartmodel.toDays()?.let { chartmodel.calWeek(it,"startDt") }.toString()
            endDt = chartmodel.toDays()?.let { chartmodel.calWeek(it,"endDt") }.toString()
            date.text = startDt + " ~ " + endDt
            Weekparse() //주 날짜 파싱한 변수 불러오기
            Refresh(type, year, month,start,end) //그래프 새로고침
            leftVisible()
            rightVisible()

            //월, 년 인덱스값 초기화
            m=0
            y=0
            i=0
        })




        chart_month.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#FFFFFF"))
            chart_month.setBackgroundResource(R.drawable.month1)
            chart_week.setTextColor(Color.parseColor("#000000"))
            chart_week.setBackgroundResource(R.drawable.week0)
            chart_year.setTextColor(Color.parseColor("#000000"))
            chart_year.setBackgroundResource(R.drawable.year0)



            type = "month"
            date.text = chartmodel.Month(0)
//            monthParse() // 월 날짜 파싱
            year = chartmodel.monthParse(date.text.toString(),"year")
            month = chartmodel.monthParse(date.text.toString(),"month")
//            val value = calLastDay(year, month)
            val value = chartmodel.calLastDay(year, month).toString()+"f"
            lastDayF = value.toFloat()
            Log.d(TAG, "lastDay:$lastDayF")
            Refresh(type, year, month, 0, 0) // 그래프 새로고침

            leftVisible()
            rightVisible()

            //일, 년 인덱스값 초기화
            y=0
            i=0
            m=0


        })

        chart_year.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_month.setBackgroundResource(R.drawable.month0)
            chart_week.setTextColor(Color.parseColor("#000000"))
            chart_week.setBackgroundResource(R.drawable.week0)
            chart_year.setTextColor(Color.parseColor("#FFFFFF"))
            chart_year.setBackgroundResource(R.drawable.year1)


            type = "year"
            date.text = chartmodel.Year(0)
            year = chartmodel.yearParse(date.text.toString())
            Refresh(type, year, 0,0,0) // 그래프 새로고침
            leftVisible()
            rightVisible()

            //일, 월, 년 인덱스값 초기화
            i=0
            m=0
            y=0
        })

//이전 이후
        left.setOnClickListener {
            if (type.equals("week")) {
                i -= 1

                startDt = chartmodel.Days7(i)?.let { chartmodel.calWeek(it,"startDt") }.toString()
                endDt = chartmodel.Days7(i)?.let { chartmodel.calWeek(it,"endDt") }.toString()
                date.text = startDt + " ~ " + endDt //기본 날짜 세팅 (주)
                Weekparse()
                Refresh(type, year, month, start, end) //그래프 새로고침


            } else if (type.equals("month")) {
                m -= 1
                date.text = chartmodel.Month(m)
                year = chartmodel.monthParse(date.text.toString(),"year")
                month = chartmodel.monthParse(date.text.toString(),"month")
                val value = chartmodel.calLastDay(year, month).toString()+"f"
                lastDayF = value.toFloat()
                Refresh(type, year, month, 0, 0)
                leftVisible() //이전 데이터 없으면 왼쪽 버튼 비활성화
                rightVisible() //이후 데이트 없으면 오른쪽 버튼 비활성화

            } else if (type.equals("year")) {
                y -= 1
                date.text = chartmodel.Year(y)
                year = chartmodel.yearParse(date.text.toString())
                Refresh(type, year, 0,0,0)
            }

            rightVisible()
            leftVisible()
        }

        right.setOnClickListener {
            if (type == "week") {
                i += 1
                startDt = chartmodel.Days7(i)?.let { chartmodel.calWeek(it,"startDt") }.toString()
                endDt = chartmodel.Days7(i)?.let { chartmodel.calWeek(it,"endDt") }.toString()
                date.text = startDt + " ~ " + endDt //기본 날짜 세팅 (주)
                Weekparse()
                Refresh(type, year, month,start,end) // 그래프 새로고침


            } else if (type == "month") {
                m += 1
                date.text = chartmodel.Month(m)
//                monthParse()
                year = chartmodel.monthParse(date.text.toString(),"year")
                month = chartmodel.monthParse(date.text.toString(),"month")
                val value = chartmodel.calLastDay(year, month).toString()+"f"
                lastDayF = value.toFloat()
                Refresh(type, year, month, 0, 0)



            } else if (type == "year") {
                y += 1
                date.text = chartmodel.Year(y)
                year = chartmodel.yearParse(date.text.toString())
                Refresh(type, year, 0,0,0)

            }
            leftVisible()
            rightVisible()
        }


    }




    //그래프 새로고침 메서드
    fun Refresh(type: String, year: Int, month: Int, start:Int, end:Int) {

        lastDayI = chartmodel.calLastDay(year,month) //현재 보고있는 달의 마지막 날짜 구하기
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
            entries.add(BarEntry(0f, null))
            entries.add(BarEntry(1f, null))
            entries.add(BarEntry(2f, null))
            entries.add(BarEntry(3f, null))
            entries.add(BarEntry(4f, null))
            entries.add(BarEntry(5f, null))
            entries.add(BarEntry(6f, null))


            if(sMonth!=eMonth){
                if (0 < sMonth && sMonth < 10) {
                    sm = "0" + sMonth
                } else {
                    sm = sMonth.toString()
                }
                for (sweek in chartmodel.SmonthSelectData(cContext,sYear, sMonth, start)) {
                    var w = sweek.day //날짜
                    var t = sweek.settingTime?.let { chartmodel.changeTime(it) }!! //성공시간

                    if (0 < w!! && w < 10) {
                        wd = "0" + w
                    } else {
                        wd = w.toString()
                    }

//                    weekSame(sm,wd,t)
                    if (chartmodel.whatDay("$sYear$sm$wd").equals("월")) {
                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$sYear$sm$wd").equals("화")) {
                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$sYear$sm$wd").equals("수")) {
                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$sYear$sm$wd").equals("목")) {
                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$sYear$sm$wd").equals("금")) {
                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$sYear$sm$wd").equals("토")) {
                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$sYear$sm$wd").equals("일")) {
                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    }

                }


                if (0 < eMonth && eMonth < 10) {
                    em = "0" + eMonth
                } else {
                    em = eMonth.toString()
                }
                for (eweek in  chartmodel.WeekSelectData(cContext, eYear, eMonth, 1, end)) {
                    var w = eweek.day //날짜
                    var t = eweek.settingTime?.let { chartmodel.changeTime(it) }!! //성공시간

                    if (0 < w!! && w < 10) {
                        wd = "0" + w
                    } else {
                        wd = w.toString()
                    }

//                    weekSame(em,wd,t) //날짜별 요일 매치 메서드
                    if (chartmodel.whatDay("$eYear$em$wd").equals("월")) {
                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$eYear$em$wd").equals("화")) {
                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$eYear$em$wd").equals("수")) {
                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$eYear$em$wd").equals("목")) {
                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$eYear$em$wd").equals("금")) {
                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$eYear$em$wd").equals("토")) {
                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$eYear$em$wd").equals("일")) {
                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    }

                }



            }else if(sMonth==eMonth){
                if (0 < month && month < 10) {
                    md = "0" + month
                } else {
                    md = month.toString()
                }
                for (week in chartmodel.WeekSelectData(cContext, year, month, start, end)) {
                    var w = week.day //날짜
                    var t = week.settingTime?.let { chartmodel.changeTime(it) }!! //성공시간

                    if (0 < w!! && w < 10) {
                        wd = "0" + w
                    } else {
                        wd = w.toString()
                    }

//                    weekSame(md,wd,t) //날짜별 요일 매치 메서드
                    if (chartmodel.whatDay("$year$md$wd").equals("월")) {
                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$year$md$wd").equals("화")) {
                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$year$md$wd").equals("수")) {
                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$year$md$wd").equals("목")) {
                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$year$md$wd").equals("금")) {
                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$year$md$wd").equals("토")) {
                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    } else if (chartmodel.whatDay("$year$md$wd").equals("일")) {
                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
                    }

                }

            }
        } else if (type.equals("month")) {


            for (month in chartmodel.MonthSelectData(cContext, year, month)) {
                var t: Float? = month.settingTime?.let { chartmodel.changeTime(it) }
                val y: Float = t as Float

                val day: Int? = month.day
                for (i in 0 until lastDayF.toInt() + 1) {
                    if (day == i+1) {
                        entries.add(BarEntry(i*1f * 1f, y))
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

            for (year in chartmodel.YearSelectData(cContext,year)) {
                var m = year.month
                var t = year.settingTime?.let { chartmodel.changeTime(it) }

                for(i in 1 until 13){
                    if(i==m){
                        entries.add(BarEntry(1f*(i-1), 1f* t!!))
                    }else{

                    }
                }
            }

        }
        var set = BarDataSet(entries, "DataSet")//데이터셋 초기화 하기
        set.color = ContextCompat.getColor(requireContext(),
            R.color.mainclolor
        )

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)

        val data = BarData(dataSet)
        data.setDrawValues(false) //막대 위에 숫자 표시

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

                if(type.equals("week")){
                    axisMaximum = 7f
                    granularity = 1f
                    setValueFormatter(IndexAxisValueFormatter(chartdata.weeklabels)) //x축에 들어가는 week 값
                } else if (type.equals("month")) {
                    axisMaximum = lastDayF
                        labelCount = 8 //x축 라벨 나타내는 개수
                        granularity = 4f // x축 라벨 간격 4f면 (ex: 1,5,9,13 ....)
                    setValueFormatter(IndexAxisValueFormatter(monthLabels))

                } else if(type.equals("year")) {
                    axisMaximum = 12f //최대 나타낼 막대수
                    granularity = 1f // 간격 설정
                    labelCount = 12 //x축 라벨 나타내는 개수
                    setValueFormatter(IndexAxisValueFormatter(chartdata.yearlabels)) //x축에 들어가는 week 값
               }
            }

            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정(차트 밑에 막대가 무엇인지 설명하는 것)

            chart.invalidate();                                 // 새로 고침
        }
    }




    //왼쪽버튼 데이터 유무에 따른 VISIBLE or INVISIBLE
    fun leftVisible() {

        if(type.equals("week")){
            Log.d(TAG, "leftVisible: 버튼왼쪽1 // $firstRowMonth // $firstRowDay // $sMonth // $sDay // $eDay")
            if (firstRowYear != 0 && firstRowDay != 0 && firstRowMonth != 0) {
                left.visibility = View.VISIBLE
                left2.visibility = View.INVISIBLE
                        if (firstRowMonth == sMonth) {
                            if(firstRowDay>sDay-7 &&firstRowDay<sDay&& value==0){ //fRD=28일 sDAY-7= 24 sDay=31
                                left.visibility = View.VISIBLE
                                left2.visibility = View.INVISIBLE
                                value=1
                            }else if(firstRowDay>sDay&& value==1){
                                left.visibility = View.INVISIBLE
                                left2.visibility = View.VISIBLE
                                value=0
                            } else if(firstRowDay==eDay){
                                left.visibility = View.INVISIBLE
                                left2.visibility = View.VISIBLE
                            }
                        }
            }else{
                Log.d(TAG, "leftVisible: 로그7// $firstRowMonth // $firstRowDay // $sMonth // $sDay")
                left.visibility = View.INVISIBLE
                left2.visibility = View.VISIBLE
            }
        }
        else if(type.equals("year")){
            Log.d(TAG, "leftVisible: 버튼왼쪽1 년 $firstRowYear // $year" )
            if (firstRowYear != 0 && firstRowDay != 0 && firstRowMonth != 0) {
                Log.d(TAG, "leftVisible: 버튼왼쪽2 년 $firstRowYear // $year" )
            if (firstRowYear == year) {
                Log.d(TAG, "leftVisible: 버튼왼쪽3 년 $firstRowYear // $year" )
                        left.visibility = View.INVISIBLE
                left2.visibility = View.VISIBLE
                }else{
                Log.d(TAG, "leftVisible: 버튼왼쪽4 년 $firstRowYear // $year" )
                left.visibility = View.VISIBLE
                left2.visibility = View.INVISIBLE
            }
            }
        }else if(type.equals("month")){
            Log.d(TAG, "leftVisible: 버튼왼쪽 월")
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
                    Log.d(TAG, "rightVisible: 오른쪽1 // $lastRowYear // $year")
                    right.visibility = View.INVISIBLE
                    right2.visibility = View.VISIBLE
                }else{
                    Log.d(TAG, "rightVisible: 오른쪽2 // $lastRowYear // $year")
                    right.visibility = View.VISIBLE
                    right2.visibility = View.INVISIBLE
                }
            }
        }else if(type.equals("month")){
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

    //주 파싱한 변수 값 불러오기
fun Weekparse(){
    year = chartmodel.weekParse(date.text.toString(),"year")
    month = chartmodel.weekParse(date.text.toString(),"month")
    start = chartmodel.weekParse(date.text.toString(),"start")
    end = chartmodel.weekParse(date.text.toString(),"end")
    sMonth = chartmodel.weekParse(date.text.toString(),"sMonth")
    eMonth = chartmodel.weekParse(date.text.toString(),"eMonth")
    sYear = chartmodel.weekParse(date.text.toString(),"sYear")
    eYear = chartmodel.weekParse(date.text.toString(),"eYear")
    sDay = chartmodel.weekParse(date.text.toString(),"sDay")
    eDay = chartmodel.weekParse(date.text.toString(),"eDay")
}

//    //날짜별 요일에 분배해주는 메서드
//    fun weekSame(sm: String, wd: String, t: Float){
//        if (chartmodel.whatDay("$sYear$sm$wd").equals("월")) {
//            entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//        } else if (chartmodel.whatDay("$sYear$sm$wd").equals("화")) {
//            entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//        } else if (chartmodel.whatDay("$sYear$sm$wd").equals("수")) {
//            entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//        } else if (chartmodel.whatDay("$sYear$sm$wd").equals("목")) {
//            entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//        } else if (chartmodel.whatDay("$sYear$sm$wd").equals("금")) {
//            entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//        } else if (chartmodel.whatDay("$sYear$sm$wd").equals("토")) {
//            entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//        } else if (chartmodel.whatDay("$sYear$sm$wd").equals("일")) {
//            entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//        }
//
//    }

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









