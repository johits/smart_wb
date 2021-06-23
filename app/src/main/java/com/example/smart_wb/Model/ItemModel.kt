package com.example.smart_wb.Model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.smart_wb.LockScreenActivity
import com.example.smart_wb.Model.Data.ItemData
import com.example.smart_wb.Model.Shared.PointItemSharedModel
import com.example.smart_wb.R

class ItemModel {
    val itemData= arrayListOf<ItemData>() // 아이템 배열

//초기화
    fun reset(context: Context) {
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


    //자물쇠 구매여부에 따른 세팅
    fun lockSet(context: Context){
        var locker = PointItemSharedModel.getLocker(context) as ArrayList<String>
        for(i in 0 until  locker.size){
            Log.d(LockScreenActivity.TAG, "나열1: "+locker[i])
            for(j in 0 until itemData.size){
                if(locker[i].equals(itemData[j].name)|| "reset".equals(itemData[j].name)){
                    Log.d(LockScreenActivity.TAG, "나열2: "+locker[i]+"  "+itemData[j].name)
                    itemData[j].lock =true
                }
            }
        }
    }

    //적용 아이템에 따른 체크 표시 세팅
    fun itemSet(context: Context){
        for(j in 0 until itemData.size){
            if(PointItemSharedModel.getBg(context)==(itemData[j].item)){
                itemData[j].bcheck =true
                itemData[j].bg =true
            }else if(PointItemSharedModel.getTimer(context)==(itemData[j].item)){
                itemData[j].tcheck =true
                itemData[j].timer =true
            }
        }
    }

    //아이템 추가
    fun itemadd(){
        Log.d("아이템추가", "itemadd: 실행됨")
        itemData.add(
            ItemData(
                name = "bg1",
                item = R.drawable.bg1,
                price = 100,
                type = "bg"
            )
        )
        itemData.add(
            ItemData(
                name = "bg2",
                item = R.drawable.bg2,
                price = 200,
                type = "bg"
            )
        )
        itemData.add(
            ItemData(
                name = "timer1",
                item = R.drawable.timer1,
                price = 300,
                type = "timer"
            )
        )
        itemData.add(
            ItemData(
                name = "timer2",
                item = R.drawable.timer2,
                price = 400,
                type = "timer"
            )
        )
    }


}