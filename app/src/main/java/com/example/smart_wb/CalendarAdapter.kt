package com.example.smart_wb

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_wb.SQLite.ScreenTimeData

class CalendarAdapter(private val context: Context) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    val TAG = "CalendarAdapter"
    var dataList = mutableListOf<ScreenTimeData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarAdapter.ViewHolder {
       // Log.d(TAG, "onCreateViewHolder: ")
            val view = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false)
            return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: CalendarAdapter.ViewHolder, position: Int) {
       // Log.d(TAG, "onBindViewHolder: ")
        holder.bind(dataList[position])
        if(dataList[position].success==0){
            holder.tvSuccess.setTextColor(ContextCompat.getColor(context,R.color.colorRed))
        }else{
            holder.tvSuccess.setTextColor(ContextCompat.getColor(context,R.color.colorBlue))
        }
    }

    fun replaceList(newList: MutableList<ScreenTimeData>) {
        dataList.clear()
        dataList = newList.toMutableList()
        //어댑터의 데이터가 변했다는 notify를 날린다
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int{
        //Log.d(TAG, "getItemCount: "+dataList.size)
        return dataList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         val tvStartTime: TextView = itemView.findViewById(R.id.tvStartTime)
        val tvSettingTime: TextView = itemView.findViewById(R.id.tvSettingTime)
        val tvSuccess: TextView = itemView.findViewById(R.id.tvSuccess)
        val tvFlower: TextView = itemView.findViewById(R.id.tvFlower)


        @SuppressLint("ResourceAsColor")
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(item: ScreenTimeData) {
            //성공 0->실패, 1->성공
            if (item.success == 0) {
                tvSuccess.setText(R.string.calendar_time_fail)
            } else {
                tvSuccess.setText(R.string.calendar_time_success)
            }
            tvStartTime.text = item.time
            tvSettingTime.text = item.settingTime?.let { changeTime(it) }
            tvFlower.text = item.flower.toString()
        }

//        //설정시간은 초 -> HH:mm:ss 로 변환
        @RequiresApi(Build.VERSION_CODES.N)
        fun changeTime(settingTime: Int): String {
            val result: String?
            val hour = Math.floorDiv(settingTime, 3600)
            val min = Math.floorMod(settingTime, 3600) / 60
            val sec = Math.floorMod(settingTime, 3600) % 60
//            if (hour > 0) {
                result = "%1$02d:%2$02d:%3$02d".format(hour, min, sec)
//            } else {
//                result = "%1$02d:%2$02d".format(min, sec)
//            }
            return result
        }
    }

}

