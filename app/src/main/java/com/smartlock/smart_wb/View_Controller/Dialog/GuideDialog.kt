package com.smartlock.smart_wb.View_Controller.Dialog

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import com.smartlock.smart_wb.R

/**
 * 20-06-12 yama 스크린타임 시작시 경고 다이얼로그
 * */
class GuideDialog(context: Context) {
    private var dialog = Dialog(context)

    fun showDialog(){
        dialog.setContentView(R.layout.guide_dialog)
        dialog.setCancelable(false)
        dialog.show()

        val closeDialog = dialog.findViewById<ImageButton>(R.id.ib_close)
        val noShow = dialog.findViewById<CheckBox>(R.id.cb_noShow)
        val startScreenTime = dialog.findViewById<Button>(R.id.btn_start)

        //시작버튼
        startScreenTime.setOnClickListener{
            itemClickListener.onClickStart(noShow.isChecked)
            dialog.dismiss()
        }
        //닫기버튼
        closeDialog.setOnClickListener{
            dialog.dismiss()
        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClickStart(flag:Boolean) //flag 다시보지 않기 확인용
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener2(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener: OnItemClickListener
}