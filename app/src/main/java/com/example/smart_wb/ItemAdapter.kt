package com.example.smart_wb

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_wb.LockScreenActivity.Companion.TAG
import com.example.smart_wb.Shared.PointItemShared


/**
2021-05-31
joker
아이템 미리보기(아이템 리사이클러뷰) 어댑터
 */

class ItemAdapter(private val context: Context, val itemList: ArrayList<ItemData>, var flower:Int, var lockerlist:ArrayList<String>): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


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
        val check: ImageView = itemView.findViewById(R.id.check)


    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {


        var bg = itemList[position].bg
        var timer = itemList[position].timer
        var bcheck = itemList[position].bcheck //배경 적용 버튼
        var tcheck = itemList[position].tcheck //타이머 적용 버튼
        var type = itemList[position].type  //배경, 타이머 유형
        var lock = itemList[position].lock  //자물쇠
        var item = itemList[position].item  //상품 아이템 이미지


        // View에 내용 입력
        holder.name.text = itemList[position].name
        holder.price.text = itemList[position].price.toString()
        holder.item.setImageResource(item)



        //초기화 버튼 세팅
        if(itemList[position].name.equals("reset")){
            holder.price.visibility = View.INVISIBLE
            holder.pointIcon.visibility = View.INVISIBLE
            holder.lock.visibility = View.INVISIBLE
        }else{
            holder.price.visibility = View.VISIBLE
            holder.pointIcon.visibility = View.VISIBLE
            holder.lock.visibility = View.VISIBLE
        }


        // 구매한 아이템 여부에 따른 자물쇠, 적용버튼 보여지기
        if (lock){ //구매한 아이템일 경우 item.lock = true
            holder.lock.visibility = View.INVISIBLE
            if(itemList[position].name.equals("reset")){
                holder.check.visibility = View.INVISIBLE
            }else {
                holder.check.visibility = View.VISIBLE
            }
        }else{
            holder.lock.visibility = View.VISIBLE
            holder.check.visibility = View.INVISIBLE
        }



        // 체크 버튼 적용 여부 구분
        if (bcheck||tcheck){
            holder.check.setImageResource(R.drawable.ok_check)
            if(type.equals("bg")) {
                PointItemShared.setBg(context, item)
            }else if(type.equals("timer")){
                PointItemShared.setTimer(context,item)
            }
        }else{
            holder.check.setImageResource(R.drawable.no_check)
        }


        holder.check.setOnClickListener{
//            itemClickListener.onClick(it, position) //적용버튼 클릭시 미리보기 적용됨
            if(type.equals("bg")&&bcheck){
                bcheck = false
                holder.check.setImageResource(R.drawable.no_check)
                PointItemShared.setBg(context, 0)
                Toast.makeText(context,"적용 해제되었습니다.",Toast.LENGTH_SHORT).show()
            }else if(type.equals("timer")&&tcheck){
                tcheck = false
                holder.check.setImageResource(R.drawable.no_check)
                PointItemShared.setTimer(context, 0)
                Toast.makeText(context,"적용 해제되었습니다.",Toast.LENGTH_SHORT).show()
            }else{
                ck(type,position)
//                loop(type, position) //선택란 배경 체크됨
                Toast.makeText(context,"적용되었습니다.",Toast.LENGTH_SHORT).show()
            }
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
//            itemClickListener.onClick(it, position)
            //테스트
//            val dialog = PayDialog(context)
//                dialog.myDig(itemList[position].item, itemList[position].price, flower, itemList[position].name,context)

            //실제코드
            if(flower>=itemList[position].price) { //현재 보유 꽃송이와 구매하려는 아이템 꽃송이 비교
                val dialog = PayDialog(context)
                dialog.myDig(itemList[position].item, itemList[position].price, flower,itemList[position].name, context)
                //Itemadapter 클릭 리스너
                dialog.setItemClickListener2(object: PayDialog.OnItemClickListener {
                    override fun onClick(p: Int) {
                        itemList[position].lock = true //이거 설정해줘야 금액 변경됨
                        holder.lock.visibility = View.GONE
                        holder.check.setImageResource(R.drawable.no_check)
                        holder.check.visibility = View.VISIBLE
                        itemClickListener.onClick(it, position)


                    }
                })


            }else{ //꽃송이가 부족할 경우
                val builder = AlertDialog.Builder(context)
                builder.setMessage("꽃송이가 부족합니다.")
                builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->

                }
                builder.setCancelable(false) //뒷배경 터치 막기
                builder.show()

            }
//            loop(type,position) // 자물쇠 누르면 미리보기 적용 동시에 됨


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
            loop(type,position)
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



    //아이템 타입별(배경, 타이머) 하나만 선택되게하는 메서드
    fun loop(type:String, position:Int){
        Log.d(TAG, "loop: 루프 작동")
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


    //아이템 타입별(배경, 타이머) 하나만 선택되게하는 메서드
    fun ck(type:String, position:Int){
        Log.d(TAG, "loop: 루프 작동")
        if(type.equals("bg")) {
            //선택 하나만 되게
            for (i in 0 until itemList.size) {
                for (i in 0 until itemList.size) {
                    if (i == position) {
                        itemList[i].bcheck = true
                    } else {
                        itemList[i].bcheck = false
                    }
                }
            }
        }else if(type.equals("timer")){
            for (i in 0 until itemList.size) {
                if (i == position) {
                    itemList[i].tcheck = true
                } else {
                    itemList[i].tcheck = false
                }
            }
        }
        notifyDataSetChanged()
    }
}

