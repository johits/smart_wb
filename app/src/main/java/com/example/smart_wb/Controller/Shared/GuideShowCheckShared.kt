package com.example.smart_wb.Controller.Shared

import android.content.Context
import android.content.SharedPreferences
/**
 * 2021-06-07 yama 스크린타임 시작전 안내 다이얼로그 다시보기 유무

 * */
object GuideShowCheckShared {
    private val fileName : String = "guideShowCheck" //쉐어드 파일이름

    //안내 다시보지 않기 체크하기
    fun setNoDialCheck(context: Context, flag:Boolean){
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = prefs.edit()
        editor.putBoolean("noDialCheck", flag)
        editor.apply()
    }
    //안내 다시보지 않기 체크유무 불러오기
    fun getNoDialCheck(context: Context):Boolean{
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return prefs.getBoolean("noDialCheck", false)
    }
    //데이터 초기화
    fun clearTimerSet(context: Context) {
        val prefs : SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.commit()
    }
}