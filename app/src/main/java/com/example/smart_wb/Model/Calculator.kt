package com.example.smart_wb.Model

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

/**
 * 2021-06-23 yama 시간 변환기
 * */
class Calculator {

    //설정시간은 초 -> "HH시간 mm분" 으로 변환
    @RequiresApi(Build.VERSION_CODES.N)
     fun secToHourMin(settingTime: Int): String {
        val result: String?
        val hour = Math.floorDiv(settingTime, 3600)
        val min = Math.floorMod(settingTime, 3600) / 60
        result = "%1$02d시간 %2$02d분".format(hour, min)

        return result
    }

    //년, 월 기준 마지막 날짜 계산
    fun calLastDay(year: Int?, month: Int?):Int{
        val cal = Calendar.getInstance()
        if (year != null&&month!=null) {
            cal.set(year,month-1, 15, 0,0,0)
        }//month는 -1해줘야 해당월로 인식
        var lastDay:Int= cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        return lastDay
    }
}