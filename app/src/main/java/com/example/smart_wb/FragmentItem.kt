package com.example.smart_wb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_item.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentItem.newInstance] factory method to
 * create an instance of this fragment.
 */

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
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        irv.setLayoutManager(layoutManager)
        irv.layoutManager =
            LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL }


        itemAdapter = ItemAdapter(requireContext())
        irv.adapter = itemAdapter

        datas.apply {

            //임시 아이템(더미데이터)
            add(ItemData(item = R.drawable.alarm, price = 100, lock = false))
            add(ItemData(item = R.drawable.calendar, price = 200, lock = true))
            add(ItemData(item = R.drawable.flower, price = 300, lock = false))
            add(ItemData(item = R.drawable.chart, price = 400, lock = true))

            itemAdapter.datas = datas
            itemAdapter.notifyDataSetChanged()

        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentItem.
         */
        // TODO: Rename and change types and number of parameters
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