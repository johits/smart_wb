package com.example.smart_wb.Model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.smart_wb.Shared.TimerSetShared
import java.text.SimpleDateFormat
import java.util.*

/**
 * 2021-06-23 yama 시간 변환기
 * */
class Calculator {

    //설정시간은 초 -> "HH시간 mm분" 으로 변환
    @RequiresApi(Build.VERSION_CODES.N)
     fun secToHourMin(settingTime: Int): String {
        val hour = Math.floorDiv(settingTime, 3600)
        val min = Math.floorMod(settingTime, 3600) / 60
        return "%1$02d시간 %2$02d분".format(hour, min)

    }

    //년, 월 기준 마지막 날짜 계산//달력, 차트에서 사용
    fun calLastDay(year: Int?, month: Int?): Int {
        val cal = Calendar.getInstance()
        if (year != null && month != null) {
            cal.set(year, month - 1, 15, 0, 0, 0)
        }//month는 -1해줘야 해당월로 인식

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    //시간 -> 초 변환 //String->Int //ex 01:01:00 -> 3660
    fun calSec(time: String): Int {
        val parts = time.split(":").toTypedArray()
        val hour: Int = parts[0].toInt()
        val min: Int = parts[1].toInt()
        val sec: Int = parts[2].toInt()
        return hour * 3600 + min * 60 + sec
    }

    //초-> 시간 변환 //ex 3660 -> 1시간1분
    @RequiresApi(Build.VERSION_CODES.N)
     fun calTime(setTime: Int): String {
        val result: String?
        val hour = Math.floorDiv(setTime, 3600)
        val min = Math.floorMod(setTime, 3600) / 60
        //  val sec = Math.floorMod(setTime, 3600) % 60
        if (hour > 0 && min > 0) {
            result = "%1$2d시간%2$2d분".format(hour, min)
        } else if (hour > 0 && min == 0) {
            result = "%1$2d시간".format(hour)
        } else {
            result = "%1$2d분".format(min)
        }
        return result
    }

    //시간변환기 //드로우서비스
    @RequiresApi(Build.VERSION_CODES.N)
    fun changeTime(setTime: Int): String? {
        val result: String?
        val hour = Math.floorDiv(setTime, 3600)
        val min = Math.floorMod(setTime, 3600) / 60
        val sec = Math.floorMod(setTime, 3600) % 60

        if (hour > 0) {//1시간 초과 남았을떄 ex 02시22분
            result = "%1$02d시 %2$02d분".format(hour, min)
        } else if (hour == 0 && min > 0) { //1시간 이하 남았을 때 ex 22분22초
            result = "%1$02d분 %2$02d초".format(min, sec)
        } else if (hour == 0 && min == 0) { // 1분 이하 남았을 때 ex 22초
            result = "%1$02d초".format(sec)
        } else { //리턴값 있어서 else 넣어야 한다. ex 22초
            result = "%1$02d초".format(sec)
        }
        return result
    }
}