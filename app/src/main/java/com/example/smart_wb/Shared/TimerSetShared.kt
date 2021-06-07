package com.example.smart_wb.Shared

import android.content.Context
import android.content.SharedPreferences
/**
 * 2021-06-07 yama 스크린타임 시간 설정 저장
 * 스크린타임 시작날짜, 시작시간 , 설정시간
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
    //데이터 초기화
    fun clearTimerSet(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}