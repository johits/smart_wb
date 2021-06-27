package com.smartlock.smart_wb.Model.Data

class ChartData() {
    val weeklabels = arrayOf(
        "월", "화", "수", "목", "금", "토", "일"
    )
    val yearlabels = arrayOf(
        "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
    )
    fun monthLabels(lastDayI:Int): String {
        val monthLabels = Array(lastDayI,{""})
        var i =0
        for(i in 0 until lastDayI){
            monthLabels[i]= (i+1).toString()
        }
        return monthLabels[i]
    }

}