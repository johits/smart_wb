package com.example.smart_wb

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_wb.SQLite.TimerData

class CalendarAdapter (private val context: Context) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CalendarAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
    class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        private val tvStartTime : TextView = itemView.findViewById(R.id.tvStartTime)
        private val tvSettingTime : TextView = itemView.findViewById(R.id.tvSettingTime)
        private val tvSuccess : TextView = itemView.findViewById(R.id.tvSuccess)
        private val tvFlower : TextView = itemView.findViewById(R.id.tvFlower)

        @SuppressLint("ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(item: TimerData){
            //성공 0->실패, 1->성공
            if(item.success==0){
                tvSuccess.setText(R.string.calendar_time_fail)
                tvSuccess.setTextColor(R.color.colorBlue)
            }else{
                tvSuccess.setText(R.string.calendar_time_success)
                tvSuccess.setTextColor(R.color.colorRed)
            }
            tvStartTime.text=item.time
            tvSettingTime.text= changeTime(item.settingTime)
            tvFlower.text=item.flower.toString()
        }

        //설정시간은 초 -> HH:mm:ss 로 변환
        @RequiresApi(Build.VERSION_CODES.N)
        fun changeTime(settingTime: Int): String {
            val result: String?
            val hour = Math.floorDiv(settingTime, 3600)
            val min = Math.floorMod(settingTime, 3600) / 60
            val sec = Math.floorMod(settingTime, 3600) % 60
            if (hour > 0) {
                result = "%1$02d:%2$02d:%3$02d".format(hour, min, sec)
            } else {
                result = "%1$02d:%2$02d".format(min, sec)
            }
            return result
        }
    }


//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//
//            private val txtName: TextView = itemView.findViewById(R.id.tv_rv_name)
//            private val txtAge: TextView = itemView.findViewById(R.id.tv_rv_age)
//            private val imgProfile: ImageView = itemView.findViewById(R.id.img_rv_photo)
//
//            fun bind(item: ProfileData) {
//                txtName.text = item.name
//                txtAge.text = item.age.toString()
////            Glide.with(itemView).load(item.img).into(imgProfile)
//
//            }
//    (private val context: Context) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
//
//        var datas = mutableListOf<ProfileData>()
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val view = LayoutInflater.from(context).inflate(R.layout.item_recycler_ex,parent,false)
//            return ViewHolder(view)
//        }
//
//        override fun getItemCount(): Int = datas.size
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            Log.d("tag","onBindViewHolder")
//            holder.bind(datas[position])
//        }
//
}