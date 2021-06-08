package com.example.smart_wb

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.smart_wb.LockScreenActivity.Companion.TAG

class PayDialog (context: Context) {
    private val dialog = Dialog(context)

    fun myDig(i:Int,p:String) {
        dialog.setContentView(R.layout.pay_dialog) //커스텀 xml 불이기
        dialog.show()

        val item = dialog.findViewById<ImageView>(R.id.d_item)
        val price = dialog.findViewById<TextView>(R.id.d_price)
        val yes = dialog.findViewById<Button>(R.id.d_yes)
        val no = dialog.findViewById<Button>(R.id.d_no)

        price.text = p
        item.setImageResource(i)

        yes.setOnClickListener {
            Log.d(TAG, "예스")
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
        fun onClicked(myName: String)
    }

    private lateinit var onClickedListener: ButtonClickListener

    fun setOnClickedListener(listener: ButtonClickListener) {
        onClickedListener = listener
    }
}