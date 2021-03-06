package com.smartlock.smart_wb.View_Controller.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartlock.smart_wb.Model.Data.ItemData
import com.smartlock.smart_wb.Model.ItemModel
import com.smartlock.smart_wb.Model.SQLite.ScreenTimeDbHelper.Companion.TAG
import com.smartlock.smart_wb.Model.Shared.PointItemSharedModel
import com.smartlock.smart_wb.R
import com.smartlock.smart_wb.View_Controller.Activity.MainActivity
import com.smartlock.smart_wb.View_Controller.Adapter.ItemAdapter
import kotlinx.android.synthetic.main.fragment_item.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val model =
    PointItemSharedModel //모델 선언

/**
2021-06-12 (업데이트)
joker
아이템 리사이클러뷰 연결
*/

@Suppress("UNREACHABLE_CODE")
class FragmentItem : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var itemModel = ItemModel()
    private lateinit var iContext: Context

    //아이템 어댑터 및 데이터 연결
    val itemData= arrayListOf<ItemData>() // 아이템 배열
    var bt_value: Boolean = false //아이템 미리보기 접기/펼치기
    var flower : Int = 0 //쉐어드 꽃 담을 변수
    var locker = ArrayList<String>() //쉐어드 보관함 담을 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            iContext = context
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

        flower = PointItemSharedModel.getFlower(iContext)
        point.text = flower.toString()
        locker = PointItemSharedModel.getLocker(iContext) as ArrayList<String>



        //쉐어드 적용된 아이템 불러오기(배경, 타이머)
        i_back.setImageResource(PointItemSharedModel.getBg(iContext))
        i_timer.setImageResource(PointItemSharedModel.getTimer(iContext))

        initRecycler()

        //아이템보기 접기/펼치기
        iv.setOnClickListener {
            if (bt_value) { //펼치기
                irv.visibility = View.VISIBLE
                arrow.rotation = 270f
                bt_value = false
                reset.visibility = View.VISIBLE
            } else if (bt_value == false) { //접기
                irv.visibility = View.GONE
                arrow.rotation = 90f
                bt_value = true
                reset.visibility = View.INVISIBLE
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
//        itemModel.itemadd() //기본 아이템 추가
        itemData.add(
            ItemData(
                name = "bg1",
                item = R.drawable.bg1,
                price = 100,
                type = "bg"
            )
        )
        itemData.add(
            ItemData(
                name = "bg2",
                item = R.drawable.bg2,
                price = 200,
                type = "bg"
            )
        )
        itemData.add(
            ItemData(
                name = "timer1",
                item = R.drawable.timer1,
                price = 300,
                type = "timer"
            )
        )
        itemData.add(
            ItemData(
                name = "timer2",
                item = R.drawable.timer2,
                price = 400,
                type = "timer"
            )
        )



        //자물쇠 구매여부에 따른 세팅
//        itemModel.lockSet(iContext)
        for(i in 0 until  locker.size){
            Log.d(TAG, "나열1: "+locker[i])
            for(j in 0 until itemData.size){
                if(locker[i].equals(itemData[j].name)|| "reset".equals(itemData[j].name)){
                    Log.d(TAG, "나열2: "+locker[i]+"  "+itemData[j].name)
                    itemData[j].lock =true
                }
            }
        }

        //적용 아이템에 따른 체크 표시 세팅
//        itemModel.itemSet(iContext)
            for(j in 0 until itemData.size){
                if(PointItemSharedModel.getBg(iContext)==(itemData[j].item)){
                    itemData[j].bcheck =true
                    itemData[j].bg =true
                }else if(PointItemSharedModel.getTimer(iContext)==(itemData[j].item)){
                    itemData[j].tcheck =true
                    itemData[j].timer =true
                }
            }



        //Itemadapter 클릭 리스너
        val itemAdapter =
            ItemAdapter(
                iContext,
                itemData,
                flower,
                locker
            )

        itemAdapter.setItemClickListener(object:
            ItemAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                if(itemData[position].name.equals("reset")){
                    i_back.setImageResource(0)
                    i_timer.setImageResource(0)
                }
                if(itemData[position].type.equals("bg")&&itemData[position].bg){
                    i_back.setImageResource(itemData[position].item)
                }else if(itemData[position].type.equals("bg")&&!itemData[position].bg){
                    i_back.setImageResource(0)
                }
                if(itemData[position].type.equals("timer")&&itemData[position].timer){
                    i_timer.setImageResource(itemData[position].item)
                }else if(itemData[position].type.equals("timer")&&!itemData[position].timer){
                    i_timer.setImageResource(0)
                }
                if(itemData[position].lock){
                    point.text =  PointItemSharedModel.getFlower(iContext).toString()
                }

            }
        })

        //아이템 모든 적용 초기화
        reset.setOnClickListener {
            itemModel.reset(iContext)
            for (i in 0 until itemData.size) {
                i_back.setImageResource(0)
                i_timer.setImageResource(0)
                itemData[i].bg = false
                itemData[i].bcheck = false
                itemData[i].timer = false
                itemData[i].tcheck = false
                PointItemSharedModel.setBg(iContext, 0)
                PointItemSharedModel.setTimer(iContext, 0)
                Toast.makeText(context, "모든 적용이 해제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            itemAdapter.notifyDataSetChanged()
        }

        irv.adapter = itemAdapter
        itemAdapter.notifyDataSetChanged()


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


