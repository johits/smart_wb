package com.smartlock.smart_wb.View_Controller.Dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.smartlock.smart_wb.Model.Data.ItemData
import com.smartlock.smart_wb.Model.Shared.PointItemSharedModel
import com.smartlock.smart_wb.R
import java.util.*


 class PayDialog (context: Context) {
     private val TAG = "PayDialog"
     private var dialog = Dialog(context)
    var present_flower: Int = 0
    var lockerlist = java.util.ArrayList<String>()
    var itemdata =  ArrayList<ItemData>()

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

        lockerlist = PointItemSharedModel.getLocker(c) as ArrayList<String>
        yes.setOnClickListener {
            Log.d(TAG, "현재 내 포인트:" + f)
            present_flower = f - p
            Log.d(TAG, "구매 후 내 포인트:" + present_flower)
            PointItemSharedModel.sumFlower(c, -p)
            Log.d(TAG, "꽃 쉐어드 저장")
            lockerlist.add(n)
            Log.d(TAG, "list 담긴 값은:" + lockerlist)
            PointItemSharedModel.sumLocker(c, lockerlist)
            Log.d(TAG, "상품 쉐어드 저장")

//            onClickedListener.onClick(present_flower.toString())
//            val intent = Intent (c, MainActivity::class.java)
//            intent.putExtra("dialog",true)
//            c.startActivity(intent)
            itemClickListener.onClick(present_flower)
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

//    interface ButtonClickListener {
//        fun onClick(myName: String)
//    }
//
//    private lateinit var onClickedListener: ButtonClickListener
//
//    fun setOnClickedListener(listener: ButtonClickListener) {
//        onClickedListener = listener
//    }


    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(flower : Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener2(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener: OnItemClickListener
}