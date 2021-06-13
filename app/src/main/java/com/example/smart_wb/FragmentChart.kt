package com.example.smart_wb

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_chart.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentChart.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentChart : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var labelList = ArrayList<String>()
    var valList = ArrayList<Int>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        //현재 시간 가져오기.
//        long now = System.currentTimeMillis();
//        // Date 생성하기
//        Date date = new Date(now);
//
//        //가져오고 싶은 형식으로 가져오기
//        SimpleDateFormat sdf = new SimpleDateFormat("MM월");
//        String getTime = sdf.format(date);

        chart_week.setOnClickListener(View.OnClickListener {
            chart_week.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
        })

        chart_month.setOnClickListener(View.OnClickListener {
            chart_month.setTextColor(Color.parseColor("#2FA9FF"))
            chart_week.setTextColor(Color.parseColor("#000000"))
            chart_year.setTextColor(Color.parseColor("#000000"))
        })

        chart_year.setOnClickListener(View.OnClickListener {
            chart_year.setTextColor(Color.parseColor("#2FA9FF"))
            chart_month.setTextColor(Color.parseColor("#000000"))
            chart_week.setTextColor(Color.parseColor("#000000"))
        })



        labelList.add("성공")
        labelList.add("실패")
        valList.add(10)
        valList.add(90)

        // PieChart 메소드
//        val pieChart = findViewById<View>(R.id.chart) as PieChart
        val entries =
            ArrayList<Entry>()
        for (i : Int in valList.indices) {
            entries.add(Entry(valList[i].toFloat(), i))
        }

        val depenses = PieDataSet(entries, "스마트 웰빙 평균 사용량 체크")
        depenses.axisDependency = YAxis.AxisDependency.LEFT
        val labels = ArrayList<String>()
        for (i in labelList.indices) {
            labels.add(labelList[i])
        }
        val data = BarChartData(labels, depenses) // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS) //
        chart.data = data
        chart.animateXY(1000, 1000)
        chart.invalidate()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentChart.
         */
        // TODO: Rename and change types and number of parameters
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