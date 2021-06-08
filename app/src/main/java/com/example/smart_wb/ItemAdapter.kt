package com.example.smart_wb

import android.content.Context
import android.graphics.Color
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

class ItemAdapter(private val context: Context, val itemList: ArrayList<ItemData>): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


    // (1) 아이템 레이아웃과 결합
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_item, parent, false)
        return ViewHolder(view)
    }

    // (2) 리스트 내 아이템 개수
    override fun getItemCount(): Int {
        return itemList.size
    }


    // (4) 레이아웃 내 View 연결
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product: ConstraintLayout = itemView.findViewById(R.id.product)
        val item: ImageView = itemView.findViewById(R.id.item)
        val pointIcon: ImageView = itemView.findViewById(R.id.pointIcon)
        val name: TextView = itemView.findViewById(R.id.name)
        val price: TextView = itemView.findViewById(R.id.price)
        val lock: ImageView = itemView.findViewById(R.id.lock)


    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        // View에 내용 입력
        holder.name.text = itemList[position].name
        holder.price.text = itemList[position].price.toString()
        holder.item.setImageResource(itemList[position].item)

        if(itemList[position].name.equals("reset")){
            holder.price.visibility = View.INVISIBLE
            holder.pointIcon.visibility = View.INVISIBLE
        }

        var bg = itemList[position].bg
        var timer = itemList[position].timer
        var type = itemList[position].type
        var lock = itemList[position].lock

        Log.d(TAG, "락값:"+lock)



        if (lock){ //구매한 아이템일 경우 item.lock = true
            holder.lock.visibility = View.INVISIBLE
            Log.d(TAG, "bind: 받아온 item.lock 값:"+itemList[position].lock)
        }else{
            holder.lock.visibility = View.VISIBLE
        }


        if (bg||timer){
            Log.d(TAG, "bg1:"+bg+"timer1"+timer)
            holder.product.setBackgroundColor(Color.parseColor("#81000000"))
        }else{
            Log.d(TAG, "bg2:"+bg+"timer2"+timer)
            holder.product.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        holder.lock.setOnClickListener {
            Log.d(TAG, "구매하기"+position+"아이템 이름"+itemList[position].item)
            itemClickListener.onClick(it, position)
            val dialog = PayDialog(context)
            dialog.myDig(itemList[position].item, itemList[position].price.toString())

            if(type.equals("bg")) {
                //선택 하나만 되게
                for (i in 0 until itemList.size) {
                    for (i in 0 until itemList.size) {
                        if (i == position) {
                            itemList[i].bg = true
                        } else {
                            itemList[i].bg = false
                        }
                    }
                }
            }else if(type.equals("timer")){
                for (i in 0 until itemList.size) {
                    if (i == position) {
                        itemList[i].timer = true
                    } else {
                        itemList[i].timer = false
                    }
                }
            }
            notifyDataSetChanged()

        }




        // (1) 리스트 내 항목 클릭 시 onClick() 호출
        holder.product.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: 리스너 작동")
            itemClickListener.onClick(it, position)

            if(type.equals("reset")){
                for (i in 0 until itemList.size) {
                        itemList[i].bg = false
                        itemList[i].timer = false
                }
            }
            if(type.equals("bg")) {
                //선택 하나만 되게
                for (i in 0 until itemList.size) {
                    if (i == position) {
                        itemList[i].bg = true
                    } else {
                        itemList[i].bg = false
                    }
                }
            }else if(type.equals("timer")){
                for (i in 0 until itemList.size) {
                    if (i == position) {
                        itemList[i].timer = true
                    } else {
                        itemList[i].timer = false
                    }
                }
            }
            notifyDataSetChanged()
        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener: OnItemClickListener

}

