package com.example.smart_wb.SQLite
/**
 * 2021-06-06 yama 타이머 테이블 데이터 클래스
 * */
data class TimerData(
    val id:Int, //pk
    val date:String,//시작날짜
    val time:String,//시작시간
    val settingTime:Int, //설정시간
    val success:Int //sqlite boolean 사용x. 성공 1, 실패 0
)
