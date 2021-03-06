package com.smartlock.smart_wb.View_Controller.etc

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import com.smartlock.smart_wb.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

/**
 * 2021-06-?? yama 달력에 오늘날짜 표시
 * */
class CalendarDecoratorToday(context: Context, currentDay: CalendarDay) : DayViewDecorator {
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val drawable: Drawable = context.getDrawable(R.drawable.caledar_today)!!
    val mContext = context
    private var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun decorate(view: DayViewFacade) {
        //view.setSelectionDrawable(drawable!!)
        view.setBackgroundDrawable(drawable)
    }

    init {
        // You can set background for Decorator via drawable here
    }
}