package com.example.smart_wb.presenter

import com.example.smart_wb.base.tbPresenter
import com.example.smart_wb.base.tbView
import com.example.smart_wb.model.TimerName


interface TimerContract {

    interface View : tbView {
        fun showLoading()
        fun hideLoading()
        fun showTimerList(TimerList : List<TimerName>)
    }

    interface Presenter : tbPresenter<View> {
        fun getTimer()
    }

}