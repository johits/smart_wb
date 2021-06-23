package com.example.smart_wb.Model.SQLite
/**
 * 2021-06-14 yama 스크린타이머 테이블 데이터 클래스
 * 타이머 테이블 구조 변경
 * */
data class ScreenTimeData(
    val id:Int?, //pk
    val year:Int?, //시작년도 ex 2021
    val month:Int?, //시작달 ex 6
    val day:Int?, //시작날 ex 14
    val time:String?, //시작시간 ex 02:22:22
    val settingTime:Int?,//설정시간 3600
    val success:Int?, //sqlite boolean 사용x. 성공 1, 실패 0. default 0
    val flower:Int? //받은 꽃 갯수 , default 0
)
