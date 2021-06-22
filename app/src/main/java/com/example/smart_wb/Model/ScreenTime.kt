package com.example.smart_wb.Model

import android.content.Context
import com.example.smart_wb.SQLite.ScreenTimeData
import com.example.smart_wb.SQLite.ScreenTimeDbHelper
import java.time.Month

class ScreenTime(private val context: Context) {

    fun loadDate(year: Int, month: Int, day: Int): MutableList<ScreenTimeData> {
        val screenTimeDbHelper = ScreenTimeDbHelper(context, "screenTimeDb.db", null, 1)
        val database = screenTimeDbHelper.writableDatabase
        val result = screenTimeDbHelper.calendarSelect(year, month, day)
        database.close()
        return result
    }
}