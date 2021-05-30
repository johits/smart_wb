package com.example.smart_wb.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import com.example.smart_wb.R
import com.example.smart_wb.base.tbActivity
import com.example.smart_wb.model.TimerName
import com.example.smart_wb.presenter.TimerContract
import com.example.smart_wb.presenter.TimerPresenter
import kotlinx.android.synthetic.main.fragment_main_timer.*


class TimerActivity : tbActivity(), TimerContract.View {

        private lateinit var TimerPresenter: TimerPresenter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.fragment_main_timer)

            TimerPresenter.takeView(this)

            setButton()

        }

    private fun setButton() {
        start.setOnClickListener {
            TimerPresenter.getTimer()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showTimerist(timerList : List<TimerName>) {
        time.text = "${timerList[0].time}"
    }

    override fun onDestroy() {
        super.onDestroy()
        TimerPresenter.TimerView()
    }


    override fun initPresenter() {
        TimerPresenter = TimerPresenter()
    }

    override fun showError(error: String) {
        Toast.makeText(this@TimerActivity, error, Toast.LENGTH_SHORT).show()
    }


}
