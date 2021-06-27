package com.smartlock.smart_wb.View_Controller.etc

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
/**
 * 21-06-14 yama swipe 막는 뷰페이저 커스텀
 * */
internal class NoSwipeViewPager : ViewPager {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false //true -> swipe 가능, false -> swipe 불가능
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return false //true -> swipe 가능, false -> swipe x
    }
}