package com.example.smart_wb.Model

import android.annotation.SuppressLint
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

    //남은시간 계산기 //남은시간 리턴
    //시작시간+설정시간=종료시간
    //종료시간-현재시간=남은시간
    //남은시간 양수 스크린타임 계속
    //남은시간 0or음수 스크린타임 이미 종료
    //날짜가 바뀌면 보정을 해야한다.
    @SuppressLint("SimpleDateFormat")
    fun calRemainTime(startDate:String, time:String, settingTime:Int): Int {
        var result = 0
        val timeStamp = System.currentTimeMillis()
        // 현재 시간을 Date 타입으로 변환
        val dateType = Date(timeStamp)
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val dateFormatDate = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatTime = SimpleDateFormat("HH:mm:ss")
        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        val nowDate: String = dateFormatDate.format(dateType) //현재 년 월 일
        val nowTime: Int = calSec(dateFormatTime.format(dateType))//현재시간
        val startTime: Int = calSec(time) //시작시간
//        val settingTime: Int = TimerSetShared.getSettingTime(this)//설정시간
        var endTime = startTime + settingTime// 종료시간

        //종료시간이 하루가 지난 상황 보정
        if (endTime > 86400) {
            if (nowDate == startDate) {
                result = endTime - nowTime
            } else {
                result = endTime - nowTime - 86400//보정필요하다
            }
        } else {
            result = endTime - nowTime//남은시간
        }

        return result
    }

    //시간 -> 초 변환 //String->Int //ex 01:01:00 -> 3660
    private fun calSec(time: String): Int {
        val parts = time.split(":").toTypedArray()
        val hour: Int = parts[0].toInt()
        val min: Int = parts[1].toInt()
        val sec: Int = parts[2].toInt()
        return hour * 3600 + min * 60 + sec
    }

    //초-> 시간 변환 //ex 3660 -> 1시간1분
    @RequiresApi(Build.VERSION_CODES.N)
    private fun calTime(setTime: Int): String {
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
}