package com.example.smart_wb.base

interface tbPresenter<T> {
    fun takeView(view: T)
    fun TimerView()
}