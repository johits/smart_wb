package com.example.smart_wb

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


/**
2021-05-31
joker
아이템 미리보기(아이템 리사이클러뷰) 어댑터
 */


class ItemAdapter(private val context: Context) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    var datas = mutableListOf<ItemData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("tag", "onCreateViewHolder: ")
        val view = LayoutInflater.from(context).inflate(R.layout.item_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("tag", "getItemCount: "+datas.size)
        return datas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("tag", "onBindViewHolder: ")
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val itemItem: ImageView = itemView.findViewById(R.id.item)
        private val itemPrice: TextView = itemView.findViewById(R.id.price)
        private val itemLock: ImageView = itemView.findViewById(R.id.lock)

        fun bind(item: ItemData) {
            itemItem.setImageResource(item.item)
            itemPrice.text = item.price.toString()

            if (item.lock){ //구매한 아이템일 경우 item.lock = true
                itemLock.visibility = View.INVISIBLE
                Log.d(TAG, "bind: 받아온 item.lock 값:"+item.lock)
            }

        }
    }


}