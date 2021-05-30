package com.example.smart_wb.presenter

import android.os.Handler
import com.example.smart_wb.model.TimerData


class TimerPresenter : TimerContract.Presenter {

    private var searchView : TimerContract.View? = null

    override fun takeView(view: TimerContract.View) {
        searchView = view
    }

    override fun getTimer() {
        searchView?.showLoading()

        Handler().postDelayed({
            val TimerList = TimerData.getTimerData()
            searchView?.showTimerList(TimerList)
            searchView?.hideLoading()
        }, 1000)
    }

    override fun TimerView() {
        searchView = null
    }

}