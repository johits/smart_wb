package com.smartlock.smart_wb.Model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.smartlock.smart_wb.Model.SQLite.ScreenTimeData
import com.smartlock.smart_wb.Model.SQLite.ScreenTimeDbHelper
import com.smartlock.smart_wb.Model.SQLite.ScreenTimeDbHelper.Companion.TAG
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChartModel {



    //주 단위 계산 메서드
    fun Days7(i: Int):String?{
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 7 * i)
        return SimpleDateFormat("yyyy-MM-dd").format(week.time)
    }

    //오늘 날짜 구하기 메서드
    fun toDays(): String? {
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 0)
        return SimpleDateFormat("yyyy-MM-dd").format(week.time)
    }

    // 월 단위 계산 메서드
    fun Month(m: Int): String? {
        val df: DateFormat = SimpleDateFormat("yyyy년 MM월")
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, m)
        return df.format(cal.time)
    }


    // 년 단위 계산 메서드
    fun Year(y: Int): String? {
        val df: DateFormat = SimpleDateFormat("yyyy년")
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, y)
        return df.format(cal.time)
    }


    //달 마지막날 구하기
    fun calLastDay(year: Int, month: Int): Int {
        Log.d(TAG, "calLastDay: 작동 완료")
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 15, 0, 0, 0)//month는 -1해줘야 해당월로 인식
//        var lastDay: String = cal.getActualMaximum(Calendar.DAY_OF_MONTH).toString() + "f"
//        var lastDay: String = cal.getActualMaximum(Calendar.DAY_OF_MONTH).toString() + "f"
        var lastDayI = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        return lastDayI
//        return when(value){
//            "lastDay" -> return lastDay
//            "lastDayI" -> return lastDayI
//        }
    }

    //첫번째행 년,월,일 , 마지막행 년,월,일 불러오기
    fun loadFirstLast(context: Context , value: String): Int {
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        val firstRow = screenTimeDbHelper.firstRow()
        val lastRow = screenTimeDbHelper.lastRow()
        var firstRowYear=0
        var firstRowMonth =0
        var firstRowDay = 0
        var lastRowYear =0
        var lastRowMonth =0
        var lastRowDay=0
        if (firstRow.size > 0 && lastRow.size > 0) {
            firstRowYear = firstRow[0].year!!
            firstRowMonth = firstRow[0].month!!
            firstRowDay = firstRow[0].day!!
            lastRowYear = lastRow[0].year!!
            lastRowMonth = lastRow[0].month!!
            lastRowDay = lastRow[0].day!!

        }
        return when (value) {
            "firstRowYear" -> return firstRowYear
            "firstRowMonth" -> return firstRowMonth
            "firstRowDay" -> return firstRowDay
            "lastRowYear" -> return lastRowYear
            "lastRowMonth" -> return lastRowMonth
            "lastRowDay" -> return lastRowDay
            else -> return 0

        }

    }


    //년 날짜 파싱
    fun yearParse(date: String): Int {
        Log.d(TAG, "yearParse: 작동완료")
        var year = Integer.parseInt(date.replace("년", "")) //ex)2021년 -> 년 제거
        return year
    }

    //월 날짜 파싱
    fun monthParse(date: String, value: String): Int{
        Log.d(TAG, "monthParse: 작동완료")
        var year = Integer.parseInt(date.substring(0, date.indexOf("년")))
        var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
        var month = Integer.parseInt(date.slice(ran))

        return when(value){
            "year" -> return year
            "month" -> return month
            else ->return 0
        }
    }

    //주 날짜 파싱
    fun weekParse(date: String, value: String): Int {
        Log.d(TAG, "weekParse: 작동 완료")
        //파싱전 예시 2021년 06월 14일 ~ 2021년 06월 20일
        var year = Integer.parseInt(date.substring(0, date.indexOf("년")))
        var ran = IntRange(6, 7) // ex 2021년 06월 <-인덱스 6,7값만 포함
        var month = Integer.parseInt(date.slice(ran))
        var s = IntRange(10, 11)
        var start = Integer.parseInt(date.slice(s))
        var e = IntRange(26, 27)
        var end = Integer.parseInt(date.slice(e))
        var sm = IntRange(6, 7)
        var sMonth = Integer.parseInt(date.slice(sm))
        var em = IntRange(22, 23)
        var eMonth = Integer.parseInt(date.slice(em))
        var sy = IntRange(0, 3)
        var sYear = Integer.parseInt(date.slice(sy))
        var ey = IntRange(16, 19)
        var eYear = Integer.parseInt(date.slice(ey))
        var sd = IntRange(10, 11)
        var sDay = Integer.parseInt(date.slice(sd))
        var ed = IntRange(26, 27)
        var eDay = Integer.parseInt(date.slice(ed))

        return when (value) {
            "year" -> return year
            "month" -> return month
            "start" -> return start
            "end" -> return end
            "sMonth" -> return sMonth
            "eMonth" -> return eMonth
            "sYear" -> return sYear
            "eYear" -> return eYear
            "sDay" -> return sDay
            "eDay" -> return eDay
            else -> return 0
        }
    }

    //시간 변환


    fun changeTime(settingTime: Int): Float {
        val result: Float?
        var test = settingTime / 60
        result = test.toFloat()/ 60

        return result
    }


    //년도 sqlite data 불러오기 메서드
    fun YearSelectData(context: Context, y: Int): ArrayList<ScreenTimeData> {
        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        var yearlist = screenTimeDbHelper.year(y) // y=year 불러올 연도 입력
        return yearlist
    }


    //월별 sqlite data 불러오기 메서드
    fun MonthSelectData(context: Context, y: Int, m: Int): ArrayList<ScreenTimeData> {
        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        var monthlist = screenTimeDbHelper.month(y, m) // y=year 불러올 연도 입력, m=month 불러올 월 입력
        return monthlist

    }



    //주별 sqlite data 불러오기 메서드
    fun WeekSelectData(context: Context,y: Int, m: Int, s:Int, e:Int): ArrayList<ScreenTimeData> { // y=year, m=month, s=start(시작날짜), e=end(끝날짜)

        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        var weeklist = screenTimeDbHelper.week(y,m,s,e)
        return weeklist

    }

    //주별 시작 달 끝 달 다를 경우 시작 날짜에 대한 데이터 불러오기 메서드
    fun SmonthSelectData(context: Context, y: Int, m: Int, s:Int): ArrayList<ScreenTimeData> { // y=year, sm=start month, s=start(시작날짜)

        val ad = Calendar.getInstance()
        ad.add(Calendar.MONTH, m-1)
        var ld:Int = ad.getActualMaximum(Calendar.DAY_OF_MONTH);    // 마지막 날짜 반환 (2018년 9월 30일)




        //sqlite 준비
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase
        //  timer 테이블 데이터 불러오기
        var weeklist = screenTimeDbHelper.sMonthweek(y,m,s,ld)
        return weeklist

    }



    //일주일 계산하기(eventDate = "2021-06-07")
    fun calWeek(eventDate: String, value: String): String {
        val dateArray = eventDate.split("-").toTypedArray()
        val cal = Calendar.getInstance()
        cal [dateArray[0].toInt(), dateArray[1].toInt() - 1] =
            dateArray[2].toInt()
        var inputDt = cal.getTime() //입력된 날짜

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        var inputSunday = cal.getTime()
//        System.out.println("입력된 날짜의 일요일  : " + cal.getTime());
        if(inputDt==inputSunday){
            cal.add(Calendar.DATE, -7);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//            System.out.println("입력된 날짜의 이전주의 일요일 : " + cal.getTime());
        }
        // 일주일의 첫날을 월요일로 지정한다
        cal.firstDayOfWeek = Calendar.MONDAY
        // 시작일과 특정날짜의 차이를 구한다
        val dayOfWeek = cal[Calendar.DAY_OF_WEEK] - cal.firstDayOfWeek
        // 해당 주차의 첫째날을 지정한다
        cal.add(Calendar.DAY_OF_MONTH, -dayOfWeek)
        val sf = SimpleDateFormat("yyyy년 MM월 dd일")
        // 해당 주차의 첫째 날짜
        var startDt = sf.format(cal.time)
        // 해당 주차의 마지막 날짜 지정
        cal.add(Calendar.DAY_OF_MONTH, 6)
        // 해당 주차의 마지막 날짜
        var endDt = sf.format(cal.time)
//         Log.d(TAG, "특정 날짜 = [$eventDate] >> 시작 날짜 = [$startDt], 종료 날짜 = [$endDt]")

        return when(value){
            "startDt" -> return startDt
            "endDt" -> return endDt
            else -> return ""
        }
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


//    //그래프 새로고침 메서드
//    fun Refresh(context: Context, type: String, year: Int, month: Int, start:Int, end:Int, lastDayI:Int, lastDayF: Float, sYear:Int,eYear:Int, sMonth:Int,eMonth:Int) {
////        val weeklabels = arrayOf(
////           "월", "화","수","목","금","토","일"
////        )
////        val yearlabels = arrayOf(
////            "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
////        )
//        val monthLabels = Array(lastDayI,{""})
//        for(i in 0 until lastDayI){
//            monthLabels[i]= (i+1).toString()
//        }
//
//        val entries = ArrayList<BarEntry>()
//        var md : String
//        var wd : String
//        var sm : String
//        var em : String
//
//
//        if (type.equals("week")) {
//
//            //x축 value 고정 시키기 위함 (x축 세팅)
//            entries.add(BarEntry(0f, null))
//            entries.add(BarEntry(1f, null))
//            entries.add(BarEntry(2f, null))
//            entries.add(BarEntry(3f, null))
//            entries.add(BarEntry(4f, null))
//            entries.add(BarEntry(5f, null))
//            entries.add(BarEntry(6f, null))
//
//
//            if(sMonth!=eMonth){
//                if (0 < sMonth && sMonth < 10) {
//                    sm = "0" + sMonth
//                } else {
//                    sm = sMonth.toString()
//                }
//                for (sweek in SmonthSelectData(context,sYear, sMonth, start)) {
//                    var w = sweek.day //날짜
//                    var t = sweek.settingTime?.let { changeTime(it) } //성공시간
//
//                    if (0 < w!! && w < 10) {
//                        wd = "0" + w
//                    } else {
//                        wd = w.toString()
//                    }
//
//                    if (whatDay("$sYear$sm$wd").equals("월")) {
//                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$sYear$sm$wd").equals("화")) {
//                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$sYear$sm$wd").equals("수")) {
//                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$sYear$sm$wd").equals("목")) {
//                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$sYear$sm$wd").equals("금")) {
//                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$sYear$sm$wd").equals("토")) {
//                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$sYear$sm$wd").equals("일")) {
//                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    }
//
//                }
//
//
//                if (0 < eMonth && eMonth < 10) {
//                    em = "0" + eMonth
//                } else {
//                    em = eMonth.toString()
//                }
//                for (eweek in WeekSelectData(context, eYear, eMonth, 1, end)) {
//                    var w = eweek.day //날짜
//                    var t = eweek.settingTime?.let { changeTime(it) } //성공시간
//
//                    if (0 < w!! && w < 10) {
//                        wd = "0" + w
//                    } else {
//                        wd = w.toString()
//                    }
//
//                    if (whatDay("$eYear$em$wd").equals("월")) {
//                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$eYear$em$wd").equals("화")) {
//                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$eYear$em$wd").equals("수")) {
//                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$eYear$em$wd").equals("목")) {
//                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$eYear$em$wd").equals("금")) {
//                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$eYear$em$wd").equals("토")) {
//                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$eYear$em$wd").equals("일")) {
//                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    }
//
//                }
//
//
//
//            }else if(sMonth==eMonth){
//                if (0 < month && month < 10) {
//                    md = "0" + month
//                } else {
//                    md = month.toString()
//                }
//                for (week in WeekSelectData(context, year, month, start, end)) {
//                    var w = week.day //날짜
//                    var t = week.settingTime?.let {changeTime(it) } //성공시간
//
//                    if (0 < w!! && w < 10) {
//                        wd = "0" + w
//                    } else {
//                        wd = w.toString()
//                    }
//
//
//                    if (whatDay("$year$md$wd").equals("월")) {
//                        entries.add(BarEntry(0f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$year$md$wd").equals("화")) {
//                        entries.add(BarEntry(1f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$year$md$wd").equals("수")) {
//                        entries.add(BarEntry(2f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$year$md$wd").equals("목")) {
//                        entries.add(BarEntry(3f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$year$md$wd").equals("금")) {
//                        entries.add(BarEntry(4f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$year$md$wd").equals("토")) {
//                        entries.add(BarEntry(5f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    } else if (whatDay("$year$md$wd").equals("일")) {
//                        entries.add(BarEntry(6f, 1f * t!!)) //x:x축 값 놓이는 위치 y:성공시간량
//                    }
//
//                }
//
//            }
//        } else if (type.equals("month")) {
//
//
//            for (month in MonthSelectData(context, year, month)) {
//                var t: Float? = month.settingTime?.let {changeTime(it) }
//                val y: Float = t as Float
//
//                val day: Int? = month.day
//                for (i in 0 until lastDayF.toInt() + 1) {
//                    if (day == i+1) {
//                        entries.add(BarEntry(i*1f * 1f, y))
//                    } else {
//                        entries.add(BarEntry(i * 1f, 0f))
//                    }
//                }
//
//            }
//        } else if (type.equals("year")) {
//            //x축 고정 세팅하기 위함
//            entries.add(BarEntry(0f, null))
//            entries.add(BarEntry(1f, null))
//            entries.add(BarEntry(2f, null))
//            entries.add(BarEntry(3f, null))
//            entries.add(BarEntry(4f, null))
//            entries.add(BarEntry(5f, null))
//            entries.add(BarEntry(6f, null))
//            entries.add(BarEntry(7f, null))
//            entries.add(BarEntry(8f, null))
//            entries.add(BarEntry(9f, null))
//            entries.add(BarEntry(10f, null))
//            entries.add(BarEntry(11f, null))
//
//            for (year in YearSelectData(context,year)) {
//                var m = year.month
//                var t = year.settingTime?.let { changeTime(it) }
//
//                for(i in 1 until 13){
//                    if(i==m){
//                        entries.add(BarEntry(1f*(i-1), 1f* t!!))
//                    }else{
//
//                    }
//                }
//            }
//
//        }
//        var set = BarDataSet(entries, "DataSet")//데이터셋 초기화 하기
//        set.color = ContextCompat.getColor(context, R.color.mainclolor)
//
//        val dataSet: ArrayList<IBarDataSet> = ArrayList()
//        dataSet.add(set)
//
//        val data = BarData(dataSet)
//        data.setDrawValues(false) //막대 위에 숫자 표시
//
//
//    }



    fun dummy(context: Context){
        //sqlite 준비
        val screenTimeDbHelper =
            ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        var database = screenTimeDbHelper.writableDatabase


        //반복문 이용 더미데이터 인서트
        screenTimeDbHelper.chartInsert(2020, 1, 14, "18:06:00", 3600*502)
        screenTimeDbHelper.chartInsert(2020, 2, 14, "18:06:00", 3600*684)
        screenTimeDbHelper.chartInsert(2020, 3, 14, "18:06:00", 3600*307)
        screenTimeDbHelper.chartInsert(2020, 4, 14, "18:06:00", 3600*399)
        screenTimeDbHelper.chartInsert(2020, 4, 26, "18:06:00", 3600*399)
        screenTimeDbHelper.chartInsert(2020, 4, 27, "18:06:00", 3600*399)
        screenTimeDbHelper.chartInsert(2020, 4, 30, "18:06:00", 3600*399)
        screenTimeDbHelper.chartInsert(2020, 5, 14, "18:06:00", 3600*523)
        screenTimeDbHelper.chartInsert(2020, 6, 14, "18:06:00", 3600*419)
        screenTimeDbHelper.chartInsert(2020, 7, 14, "18:06:00", 3600*700)
        screenTimeDbHelper.chartInsert(2020, 8, 14, "18:06:00", 3600*139)
        screenTimeDbHelper.chartInsert(2020, 9, 14, "18:06:00", 3600*385)

        screenTimeDbHelper.chartInsert(2020, 10, 14, "18:06:00", 3600*573)
        screenTimeDbHelper.chartInsert(2020, 11, 14, "18:06:00", 3600*103)
        screenTimeDbHelper.chartInsert(2020, 12, 14, "18:06:00", 3600*684)


        screenTimeDbHelper.chartInsert(2021, 1, 14, "18:06:00", 3600*400)
        screenTimeDbHelper.chartInsert(2021, 2, 14, "18:06:00", 3600*207)
        screenTimeDbHelper.chartInsert(2021, 3, 14, "18:06:00", 3600*502)
        screenTimeDbHelper.chartInsert(2021, 4, 14, "18:06:00", 3600*309)
        screenTimeDbHelper.chartInsert(2021, 4, 26, "18:06:00", 3600*3)
        screenTimeDbHelper.chartInsert(2021, 4, 27, "18:06:00", 3600*4)
        screenTimeDbHelper.chartInsert(2021, 4, 30, "18:06:00", 3600*7)


        screenTimeDbHelper.chartInsert(2021, 5, 17, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 5, 20, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 5, 24, "18:06:00", 7200)
        screenTimeDbHelper.chartInsert(2021, 5, 28, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 5, 30, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 5, 31, "18:06:00", 7200)


        screenTimeDbHelper.chartInsert(2021, 6, 1, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 6, 1, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 6, 3, "18:06:00", 7200)


        screenTimeDbHelper.chartInsert(2021, 6, 7, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 6, 9, "18:06:00", 7200)
        screenTimeDbHelper.chartInsert(2021, 6, 11, "18:06:00", 10800)


        screenTimeDbHelper.chartInsert(2021, 6, 14, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 6, 15, "18:06:00", 3600*2)
        screenTimeDbHelper.chartInsert(2021, 6, 16, "18:06:00", 3600*3)
        screenTimeDbHelper.chartInsert(2021, 6, 17, "18:06:00", 3600*4)
        screenTimeDbHelper.chartInsert(2021, 6, 18, "18:06:00", 3600*5)
        screenTimeDbHelper.chartInsert(2021, 6, 19, "18:06:00", 3600*6)
        screenTimeDbHelper.chartInsert(2021, 6, 20, "18:06:00", 3600*7)

        screenTimeDbHelper.chartInsert(2021, 6, 21, "18:06:00", 3600*7)
        screenTimeDbHelper.chartInsert(2021, 6, 22, "18:06:00", 3600*6)
        screenTimeDbHelper.chartInsert(2021, 6, 23, "18:06:00", 3600*5)
        screenTimeDbHelper.chartInsert(2021, 6, 24, "18:06:00", 3600*4)
        screenTimeDbHelper.chartInsert(2021, 6, 25, "18:06:00", 3600*3)
        screenTimeDbHelper.chartInsert(2021, 6, 26, "18:06:00", 3600*2)
        screenTimeDbHelper.chartInsert(2021, 6, 27, "18:06:00", 3600*1)

        screenTimeDbHelper.chartInsert(2021, 6, 28, "18:06:00", 3600)
        screenTimeDbHelper.chartInsert(2021, 6, 29, "18:06:00", 3600*2)
        screenTimeDbHelper.chartInsert(2021, 6, 30, "18:06:00", 3600*3)


        screenTimeDbHelper.chartInsert(2021, 7, 1, "18:06:00", 3600*4)
        screenTimeDbHelper.chartInsert(2021, 7, 6, "18:06:00", 3600*5)
        screenTimeDbHelper.chartInsert(2021, 7, 10, "18:06:00", 3600*6)
        screenTimeDbHelper.chartInsert(2021, 7, 18, "18:06:00", 3600*7)

        screenTimeDbHelper.chartInsert(2021, 8, 14, "18:06:00", 3600*30)
        screenTimeDbHelper.chartInsert(2021, 9, 14, "18:06:00", 3600*25)
        screenTimeDbHelper.chartInsert(2021, 10, 14, "18:06:00", 3600*25)
        screenTimeDbHelper.chartInsert(2021, 11, 14, "18:06:00", 3600*40)
        screenTimeDbHelper.chartInsert(2021, 12, 14, "18:06:00", 3600*100)

        screenTimeDbHelper.chartInsert(2022, 1, 14, "18:06:00", 3600*130)
        screenTimeDbHelper.chartInsert(2022, 2, 14, "18:06:00", 3600*330)
        screenTimeDbHelper.chartInsert(2022, 3, 14, "18:06:00", 3600*230)
        screenTimeDbHelper.chartInsert(2022, 4, 14, "18:06:00", 3600*425)
        screenTimeDbHelper.chartInsert(2022, 5, 14, "18:06:00", 3600*255)
        screenTimeDbHelper.chartInsert(2022, 6, 14, "18:06:00", 3600*600)
        screenTimeDbHelper.chartInsert(2022, 7, 14, "18:06:00", 3600*702)
        screenTimeDbHelper.chartInsert(2022, 8, 14, "18:06:00", 3600*458)
        screenTimeDbHelper.chartInsert(2022, 9, 14, "18:06:00", 3600*432)
        screenTimeDbHelper.chartInsert(2022, 10, 14, "18:06:00", 3600*297)
        screenTimeDbHelper.chartInsert(2022, 11, 14, "18:06:00", 3600*683)
        screenTimeDbHelper.chartInsert(2022, 12, 14, "18:06:00", 3600*702)

    }

}