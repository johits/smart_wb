package com.example.smart_wb

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.smart_wb.LockScreenActivity.Companion.TAG
import com.example.smart_wb.Shared.PointItemShared


class PayDialog (context: Context) {
    private val dialog = Dialog(context)
    var present_flower : Int = 0


    fun myDig(i:Int,p:Int,f:Int, n:String, c:Context) { //i=image, p=price, f=flower, n=name, c=cotext

            dialog.setContentView(R.layout.pay_dialog) //커스텀 xml 불이기
            dialog.setCancelable(false) //뒷배경 터치 막기
            dialog.show()




        val item = dialog.findViewById<ImageView>(R.id.d_item)
        val price = dialog.findViewById<TextView>(R.id.d_price)
        val yes = dialog.findViewById<Button>(R.id.d_yes)
        val no = dialog.findViewById<Button>(R.id.d_no)

        price.text = p.toString()
        item.setImageResource(i)

        yes.setOnClickListener {
            Log.d(TAG, "현재 내 포인트:"+f)
            present_flower = f-p
            Log.d(TAG, "구매 후 내 포인트:"+present_flower)
            PointItemShared.sumFlower(c,present_flower)
            Log.d(TAG, "꽃 쉐어드 저장")
            PointItemShared.sumLocker(c,n)
            Log.d(TAG, "상품 쉐어드 저장")
            val intent = Intent (c, MainActivity::class.java)
            intent.putExtra("dialog",true)
            c.startActivity(intent)


//            onClickedListener.onClicked(present_flower)
            dialog.dismiss()

        }

        no.setOnClickListener {
            dialog.dismiss()
        }
//        btnDone.setOnClickListener {				②
//            onClickedListener.onClicked(edit.text.toString())	③
//            dialog.dismiss()					④
//        }

    }

    interface ButtonClickListener {
        fun onClicked(flower: Int)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickedListener = listener
    }
}