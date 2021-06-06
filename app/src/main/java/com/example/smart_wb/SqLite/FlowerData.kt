package com.example.smart_wb.SqLite
/**
 * 2021-06-06 yama 꽃 테이블 데이터 클래스
 * */
data class FlowerData(
    val id:Int, //pk
    val date:String,
    val time:String,
    val getCount:Int //양수 꽃 획득, 음수 꽃 사용
)
