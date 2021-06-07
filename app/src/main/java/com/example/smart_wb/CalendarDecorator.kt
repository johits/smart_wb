package com.example.smart_wb

import android.app.Activity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.*

class CalendarDecorator(context: Activity?, currentDay: CalendarDay) : DayViewDecorator {
//    private val drawable: Drawable = context?.getMyDrawable(R.drawable.ic_menu_close_clear_cancel)!!
    private var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        //view.setSelectionDrawable(drawable!!)
//        view.setBackgroundDrawable(drawable)
//        view.setDaysDisabled(true)//클릭불가
        view.addSpan(DotSpan(20f, R.color.colorAccent))
    }

    init {
        // You can set background for Decorator via drawable here
    }
}