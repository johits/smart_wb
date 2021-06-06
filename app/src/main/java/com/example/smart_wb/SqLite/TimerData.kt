package com.example.smart_wb.SqLite
/**
 * 2021-06-06 yama 타이머 테이블 데이터 클래스
 * */
data class TimerData(
    val id:Int, //pk
    val date:String,
    val time:String,
    val settingTime:Int,
    val success:Int //sqlite boolean 사용x. 성공 1, 실패 0
)
