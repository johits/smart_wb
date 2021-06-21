package com.example.smart_wb

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.smart_wb.Controller.Shared.TimerSetShared

/**
 * 21-06-12 yama 스크린타임 성공시 성공축하 다이얼로그
 * android.view.WindowLeaked 에러
 * 핸드폰 잠금화면 상태에서 다이얼로그가 뜬 상태에서
 * 앱이 강제종료 되는 에러.
 * 해결책 찾지 못함.
 * */
class SuccessDialog(context: Context) {
    private var dialog = Dialog(context)

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDialog(context: Context){
        dialog.setContentView(R.layout.success_dialog)
        dialog.setCancelable(false)
        dialog.show()

        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val tvFlower = dialog.findViewById<TextView>(R.id.tvFlower)
        val tvMissedCall = dialog.findViewById<TextView>(R.id.tvMissedCall)
        val tvSettingTime = dialog.findViewById<TextView>(R.id.tvSettingTime)

        val settingTime:Int = TimerSetShared.getSettingTime(context)
        tvSettingTime.text=calTime(settingTime)
        tvFlower.text = (settingTime/600).toString()
        tvMissedCall.text=(TimerSetShared.getMissedCall(context)).toString()

        btnConfirm.setOnClickListener {
            itemClickListener.onClick()
            dialog.dismiss()
        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick() //flag 다시보지 않기 확인용
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener: OnItemClickListener

    //초-> 시간 변환환
    @RequiresApi(Build.VERSION_CODES.N)
    private fun calTime(setTime: Int): String {
        val result: String?
        val hour = Math.floorDiv(setTime, 3600)
        val min = Math.floorMod(setTime, 3600) / 60
        //  val sec = Math.floorMod(setTime, 3600) % 60
        //if (hour > 0) {
        result="%1$02d:%2$02d".format(hour,min)
        // } else {
        //   result="%1$02d:%2$02d".format(min,sec)
        //}
        return result
    }

    fun dismiss() {
        dismiss()
    }


}