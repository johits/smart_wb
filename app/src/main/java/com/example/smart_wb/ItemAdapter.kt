package com.example.smart_wb

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_wb.LockScreenActivity.Companion.TAG


/**
2021-05-31
joker
아이템 미리보기(아이템 리사이클러뷰) 어댑터
 */


class ItemAdapter(val context: Context, val datas: ArrayList<ItemData>, val itemClick: (ItemData) -> Unit) : RecyclerView.Adapter<ItemAdapter.Holder>() {
/* (1) Adapter의 파라미터에 람다식 itemClick을 넣는다. */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_item, parent, false)
        return Holder(view, itemClick)
        /* (4) Holder의 파라미터가 하나 더 추가됐으므로, 이곳에도 추가해준다. */
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(datas[position], context)
    }

//    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val itemproduct: ConstraintLayout = itemView.findViewById(R.id.product)
//        private val itemItem: ImageView = itemView.findViewById(R.id.item)
//        private val itemPrice: TextView = itemView.findViewById(R.id.price)
//        private val itemLock: ImageView = itemView.findViewById(R.id.lock)
//
//        fun bind(item: ItemData) {
//            itemItem.setImageResource(item.item)
//            itemPrice.text = item.price.toString()
//
//            if (item.lock){ //구매한 아이템일 경우 item.lock = true
//                itemLock.visibility = View.INVISIBLE
//                Log.d(TAG, "bind: 받아온 item.lock 값:"+item.lock)
//            }
//
//            itemproduct.setOnClickListener {
//                Toast.makeText(context,item.name,Toast.LENGTH_SHORT).show()
//                if(item.name.equals("bg1")){
//                }
//            }
//
//
//        }
//    }



    inner class Holder(itemView: View?, itemClick: (ItemData) -> Unit) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        val itemname = itemView?.findViewById<TextView>(R.id.name)
        val itemProduct = itemView?.findViewById<ConstraintLayout>(R.id.product)
        val itemItem = itemView?.findViewById<ImageView>(R.id.item)
        val itemPrice = itemView?.findViewById<TextView>(R.id.price)
        val itemLock = itemView?.findViewById<ImageView>(R.id.lock)



        fun bind(item: ItemData, context: Context) {

            itemItem?.setImageResource(item.item)
            itemPrice?.text = item.price.toString()

            if (item.lock){ //구매한 아이템일 경우 item.lock = true
                itemLock?.visibility = View.INVISIBLE
                Log.d(TAG, "bind: 받아온 item.lock 값:"+item.lock)
            }


            itemname?.text = item.name
            itemView.setOnClickListener { itemClick(item) }

        }
    }



}