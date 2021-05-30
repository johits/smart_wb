package com.example.smart_wb.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


abstract class tbActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    abstract fun initPresenter()

}