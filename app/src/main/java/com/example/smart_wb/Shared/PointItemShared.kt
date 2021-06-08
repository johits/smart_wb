package com.example.smart_wb.Shared

import android.content.Context
import android.content.SharedPreferences
/**
 * 2021-06-07 yama 아이템 flower, bg, timer, lock
 * 꽃 총 갯수, 배경화면, 타이머, 잠금화면?
 * */
object PointItemShared {
    private val fileName : String = "pointItem" //쉐어드 파일이름

    //꽃 받기 사용하기, 양수 받기, 음수 사용
    fun sumFlower(context: Context, count:Int) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        var result = getFlower(context)+count
        editor.putInt("flower", result)
        editor.commit()
    }
    //꽃 불러오기
    fun getFlower(context: Context): Int {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getInt("flower", 0)
    }
    //배경화면 변경
    fun setBg(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("bg", input)
        editor.commit()
    }
    //배경화면 불러오기
    fun getBg(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getString("bg", "").toString()
    }
    //타이머 변경
    fun setTimer(context: Context, input: String) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString("timer", input)
        editor.commit()
    }
    //타이머 불러오기
    fun getTimer(context: Context): String {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getString("timer", "").toString()
    }

    fun setLock(){
        //TODO
    }
    fun getLock(){
        //TODO
    }

    //데이터 초기화
    fun clearPointItem(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}