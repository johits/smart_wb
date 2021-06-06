package com.example.smart_wb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_wb.LockScreenActivity.Companion.TAG
import kotlinx.android.synthetic.main.fragment_item.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
2021-05-31
joker
아이템 리사이클러뷰 연결
*/

class FragmentItem : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //아이템 어댑터 및 데이터 연결
    lateinit var itemAdapter: ItemAdapter
    val datas = mutableListOf<ItemData>()
    var bt_value: Boolean = true

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
        return inflater.inflate(R.layout.fragment_item, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()

        Log.d(TAG, "bt_value:" + bt_value)

        iv.setOnClickListener {
            Log.d(TAG, "bt_value:" + bt_value)
            if (bt_value) {
                irv.visibility = View.VISIBLE
                bt_value = false
            } else if (bt_value == false) {
                irv.visibility = View.GONE
                bt_value = true
            }

        }

    }


    private fun initRecycler() {

        val layoutManager = LinearLayoutManager(requireContext())
        irv.setLayoutManager(layoutManager)
        irv.layoutManager =
            LinearLayoutManager(requireContext()).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }

        //구분선 넣기 (Horizontal 인 경우 0, vertical인 경우 1 설정)
        irv.addItemDecoration(DividerItemDecoration(requireContext(), 0))


        var itemData = arrayListOf<ItemData>(

            //임시 아이템(더미데이터)
            ItemData(name = "bg1", item = R.drawable.bg1, price = 100, lock = false),
            ItemData(name = "bg2", item = R.drawable.bg2, price = 200, lock = true),
            ItemData(name = "timer1", item = R.drawable.timer1, price = 300, lock = false),
            ItemData(name = "timer2", item = R.drawable.timer2, price = 400, lock = true)
        )

     //Itemadapter 클릭 리스너
        val itemAdapter = ItemAdapter(requireContext(), itemData) { datas ->
            Toast.makeText(context, "클릭한 상품명은 ${datas.name} 입니다", Toast.LENGTH_SHORT)
                .show()

            if(datas.name.equals("bg1")){
                i_back.setImageResource(R.drawable.bg1)
            }else if(datas.name.equals("bg2")){
                i_back.setImageResource(R.drawable.bg2)
            }
            if(datas.name.equals("timer1")){
                i_timer.setImageResource(R.drawable.timer1)
            }else if(datas.name.equals("timer2")){
                i_timer.setImageResource(R.drawable.timer2)
            }

        }


        irv.adapter = itemAdapter

//        val lm = LinearLayoutManager(context)
//        irv.layoutManager = lm
        irv.setHasFixedSize(true)


    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentItem().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}