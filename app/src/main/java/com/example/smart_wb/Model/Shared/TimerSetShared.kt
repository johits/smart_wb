package com.example.smart_wb.Model.Shared

import android.content.Context
import android.content.SharedPreferences
/**
 * 2021-06-07 yama 스크린타임 시간 설정 저장
 * 스크린타임 시작날짜, 시작시간 , 설정시간, 부재중전화수
 * */
object TimerSetShared {
    private val fileName : String = "timerSet" //쉐어드 파일이름

    //시작날짜 저장
    fun setDate(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("date", input)
        editor.commit()
    }
    //시작날짜 불러오기
    fun getDate(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getString("date", "").toString()
    }
    //시작시간 저장
    fun setTime(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("time", input)
        editor.commit()
    }
    //시작시간 불러오기
    fun getTime(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getString("time", "").toString()
    }
    //설정시간 저장
    fun setSettingTime(context: Context, input: Int) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putInt("settingTime", input)
        editor.commit()
    }
    //설정시간 불러오기
    fun getSettingTime(context: Context): Int {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getInt("settingTime", 0)
    }
    //부재중전화 횟수 더하기
    fun sumMissedCall(context: Context){
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = prefs.edit()
        editor.putInt("missedCall", getMissedCall(context)+1)
        editor.commit()
    }
    //부재중전화 클리어
    fun clearMissedCall(context: Context){
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = prefs.edit()
        editor.putInt("missedCall", 0)
        editor.commit()
    }
    //부재중전화 불러오기
    fun getMissedCall(context: Context):Int{
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getInt("missedCall", 0)
    }
    //안내 다시보지 않기 체크하기
    // 불린값 저장은 잘 됨. 불러오면 false 값만 불러옴. 일단 인트로 대체
    //스크린타임 종료되면 쉐어드 클리어 한다. 젠장
    //GuidShowCheckShared 생성
//    fun setNoDialCheck(context: Context){
//        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
//        var editor : SharedPreferences.Editor = prefs.edit()
//        editor.putInt("noDialCheck", 1)
//        editor.apply()
//    }
//    //안내 다시보지 않기 체크유무 불러오기
//    fun getNoDialCheck(context: Context):Int{
//        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
//        return prefs.getInt("noDialCheck", 0)
//    }
    //데이터 초기화 //스크린타임이 끝나면 호출한다.
    fun clearTimerSet(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}