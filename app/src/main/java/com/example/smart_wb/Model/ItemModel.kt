package com.example.smart_wb.Model

import android.content.Context
import android.widget.Toast
import com.example.smart_wb.Model.Data.ItemData
import com.example.smart_wb.Model.Shared.PointItemSharedModel
import kotlinx.android.synthetic.main.fragment_item.*
import java.text.SimpleDateFormat
import java.util.*

class ItemModel {
    val itemData= arrayListOf<ItemData>() // 아이템 배열
    //주 단위 계산 메서드


    fun reset() {
        for (i in 0 until itemData.size) {
            itemData[i].bg = false
            itemData[i].bcheck = false
            itemData[i].timer = false
            itemData[i].tcheck = false
            PointItemSharedModel.setBg(context, 0)
            PointItemSharedModel.setTimer(context, 0)
            Toast.makeText(context, "모든 적용이 해제되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun lockSet(){

    }
}