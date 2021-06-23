package com.example.smart_wb.etc

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.smart_wb.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

/**
 * 2021-06-?? yama 달력 스크린타임 데이터 있을 때 날짜 밑에 점
 * */
class CalendarDecoratorpDotSpan(context: Context, currentDay: CalendarDay) : DayViewDecorator {
//    @SuppressLint("UseCompatLoadingForDrawables")
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    private val drawable: Drawable = context.getDrawable(R.drawable.edge_gray)!!
    val mContext = context
    private var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun decorate(view: DayViewFacade) {
        //view.setSelectionDrawable(drawable!!)
//        view.setBackgroundDrawable(drawable)
//        view.setDaysDisabled(true)//클릭불가
        view.addSpan(DotSpan(15f, ContextCompat.getColor(mContext,
            R.color.colorRed
        )))
    }

    init {
        // You can set background for Decorator via drawable here
    }
}