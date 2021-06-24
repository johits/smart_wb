package com.smartwb.smart_wb.Model.SQLite
/**
 * 2021-06-06 yama 타이머 테이블 데이터 클래스
 * */
data class TimerData(
    val id:Int, //pk
    val date:String, //시작날짜 ex 2021-06-13
    val time:String, //시작시간 ex 02:22:22
    val settingTime:Int,//설정시간 3600
    val success:Int, //sqlite boolean 사용x. 성공 1, 실패 0. default 0
    val flower:Int //받은 꽃 갯수 , default 0
)
