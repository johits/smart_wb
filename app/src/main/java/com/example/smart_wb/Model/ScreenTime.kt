package com.example.smart_wb.Model

import android.content.Context
import com.example.smart_wb.Model.SQLite.ScreenTimeData
import com.example.smart_wb.Model.SQLite.ScreenTimeDbHelper
import com.example.smart_wb.Model.Shared.PointItemSharedModel


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

    //성공시 sqlite screenTimeDb table 에 success 0->1, flower 업데이트
    // 쉐어드에 받은 꽃 더하기
    fun successUpdate(flower: Int) {
        /* 데이터 업데이트 */
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        screenTimeDbHelper.update(flower)

        //받은 꽃 쉐어드에 더한다.
//        PointItemSharedModel.sumFlower(context, flower) //실제
        PointItemSharedModel.sumFlower(context, 3000) //테스트
    }
}