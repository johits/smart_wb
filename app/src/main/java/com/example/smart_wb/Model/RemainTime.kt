package com.example.smart_wb.Model

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * 2021-06-23 yama 남은시간 계산클래스
 * */
class RemainTime(
    val time:String,
    val date:String,
    val settingTime:Int) {

    private val calculator = Calculator()

    //종료시간 계산
    fun calEndTime():Int{
        return calculator.calSec(time)+settingTime
    }

    //남은시간 계산하기
    @SuppressLint("SimpleDateFormat")
    fun calRemainTime():Int{
        var result = 0
        val timeStamp = System.currentTimeMillis()
        // 현재 시간을 Date 타입으로 변환
        val dateType = Date(timeStamp)
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val dateFormatDate = SimpleDateFormat("yyyy-MM-dd")
        val dateFormatTime = SimpleDateFormat("HH:mm:ss")
        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        val nowDate: String = dateFormatDate.format(dateType) //현재 년 월 일
        val nowTime: Int = calculator.calSec(dateFormatTime.format(dateType))//현재시간

        val endTime = calEndTime()//종료시간

        //종료시간이 하루가 지난 상황 보정
        if (endTime > 86400) {
            if (nowDate == date) {
                result = endTime - nowTime
            } else {
                result = endTime - nowTime - 86400//보정필요하다
            }
        } else {
            result = endTime - nowTime//남은시간
        }

        return result
    }
}