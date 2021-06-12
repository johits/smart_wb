package com.example.smart_wb

import android.app.Dialog
import android.content.Context

/**
 * 20-06-12 yama 스크린타임 시작시 경고 다이얼로그
 * */
class GuideDialog(context: Context) {
    private var dialog = Dialog(context)

    fun showDialog(){
        dialog.setContentView(R.layout.guide_dialog)
        dialog.setCancelable(false)
    }
}