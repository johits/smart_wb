package com.example.smart_wb.Model

import android.content.Context
import com.example.smart_wb.SQLite.ScreenTimeData
import com.example.smart_wb.SQLite.ScreenTimeDbHelper
/**
 * 2021-06-22 yama 스크린타임관련
 * 데이터 업데이트, 업로드, 불러오기
 * */
class ScreenTime(private val context: Context) {

    //달력 날짜 상세보기 데이터 불러오기
    fun loadDate(year: Int, month: Int, day: Int): MutableList<ScreenTimeData> {
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        return screenTimeDbHelper.calendarSelect(year, month, day)
    }

    //달력 닷스판(날짜밑 점점)위한 데이터 불러오기
    fun loadDeco(): MutableList<ScreenTimeData> {
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        return screenTimeDbHelper.calendarSelect()
    }
}