package com.example.smart_wb

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**2021-06-14
joker
막대 그래프*/

class FragmentChart : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var i = 0 // 주 단위 계산에 필요한 변수
    var m = 0 // 월 단위 계산에 필요한 변수
    var y = 0 // 년 단위 계산에 필요한 변수

    var type = "week" // 주, 월, 년 타입 변수 (default : "week")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        date.text = toDays() + " ~ " + Days7(1) //기본 날짜 세팅 (주)

        chart_week.setOnClickListener(View.OnClickListener {
            chart_week.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
            type = "week"
            date.text = toDays() + " ~ " + Days7(1) //기본 날짜 세팅 (주)
        })

        chart_month.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#2FA9FF"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
            type = "month"
            date.text = Month(0)
        })

        chart_year.setOnClickListener(View.OnClickListener {
            chart_year.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            type = "year"
            date.text = Year(0)
        })


        left.setOnClickListener {
            if (type.equals("week")){
                i -= 1
                date.text = Days7(i) + " ~ " + Days7(i + 1)
            }else if(type.equals("month")){
                m -= 1
                date.text = Month(m)
            }else if(type.equals("year")){
                y -= 1
                date.text = Year(y)
            }

        }

        right.setOnClickListener {
            if (type == "week") {
                i += 1
                date.text = Days7(i) + " ~ " + Days7(i + 1)
            } else if (type == "month") {
                m += 1
                date.text = Month(m)
            } else if (type == "year") {
                y += 1
                date.text = Year(y)
            }

        }


        val visitors = ArrayList<BarEntry>()

        //예시 더미데이터
        visitors.add(BarEntry(2015f,10f))
        visitors.add(BarEntry(2016f,30f))
        visitors.add(BarEntry(2017f,89f))
        visitors.add(BarEntry(2018f,92f))
        visitors.add(BarEntry(2019f,73f))

        val barDataSet = BarDataSet(visitors, "사용량")
        barDataSet.setColors(*ColorTemplate.PASTEL_COLORS)
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 16f

        val barData = BarData(barDataSet)

        chart.setFitBars(true)
        chart.data = barData
        chart.description.text = ""
        chart.animateY(2000)

    }


    fun Days7(i: Int): String? {
        //주 단위 계산 메서드
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 7 * i)
        return SimpleDateFormat("yyyy년 MM월 dd일").format(week.time)
    }

    fun toDays(): String? {
        //오늘 날짜 메서드
        val week = Calendar.getInstance()
        week.add(Calendar.DATE, 0)
        return SimpleDateFormat("yyyy년 MM월 dd일").format(week.time)
    }

    fun Month(m: Int): String? {
        // 월 단위 계산 메서드
        val df: DateFormat = SimpleDateFormat("yyyy년 MM월")
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, m)
        return df.format(cal.time)
    }

    fun Year(y: Int): String? {
        // 년 단위 계산 메서드
        val df: DateFormat = SimpleDateFormat("yyyy년")
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, y)
        return df.format(cal.time)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentChart().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


