package com.example.smart_wb

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smart_wb.LockScreenActivity.Companion.TAG
import com.example.smart_wb.Shared.PointItemShared
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

@Suppress("UNREACHABLE_CODE")
class FragmentItem : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var iContext: Context

    //아이템 어댑터 및 데이터 연결
    val itemData= arrayListOf<ItemData>()      // 아이템 배열
//    val itemAdapter = ItemAdapter(iContext,itemData)     // 어댑터
    var bt_value: Boolean = true //아이템 미리보기 접기/펼치기
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

        flower = PointItemShared.getFlower(iContext)
        point.text = flower.toString()
        locker = PointItemShared.getLocker(iContext) as ArrayList<String>
        Log.d(TAG, "쉐어드에서 가져온 꽃:"+flower)
        Log.d(TAG, "쉐어드에서 가져온 보관함:"+locker.toString())


//        for (i in 0 until locker.size) {
//            val lockeritem: String = locker[i]
//            if (lockeritem.equals(itemData[i].name)) {
//                itemData[i].lock = true
//                Log.d(TAG, "name: "+itemData[i].name+ " lock: "+ itemData[i].lock)
//            }
//        }

        initRecycler()

        Log.d(TAG, "bt_value:" + bt_value)

        iv.setOnClickListener {
            Log.d(TAG, "bt_value:" + bt_value)
            if (bt_value) {
                irv.visibility = View.VISIBLE
                arrow.rotation = 270f
                bt_value = false
            } else if (bt_value == false) {
                irv.visibility = View.GONE
                arrow.rotation = 90f
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
        itemData.add(ItemData(name = "reset", item = R.drawable.reset, price = 0, lock = true, type = "reset"))
        itemData.add(ItemData(name = "bg1", item = R.drawable.bg1, price = 100, type = "bg"))
        itemData.add(ItemData(name = "bg2", item = R.drawable.bg2, price = 200,  type = "bg"))
        itemData.add(ItemData(name = "timer1", item = R.drawable.timer1, price = 300,  type = "timer"))
        itemData.add(ItemData(name = "timer2", item = R.drawable.timer2, price = 400,  type = "timer"))



        //자물쇠 구매여부에 따른 세팅
        for(i in 0 until  locker.size){
            Log.d(TAG, "나열1: "+locker[i])
            for(j in 0 until itemData.size){
                if(locker[i].equals(itemData[j].name)|| "reset".equals(itemData[j].name)){
                    Log.d(TAG, "나열2: "+locker[i]+"  "+itemData[j].name)
                    itemData[j].lock =true
                }
            }
        }

        Log.d(TAG, "initRecycler: 최종결과!!!!!!!!!:    "+itemData.toString())

//        Log.d(TAG, "locker사이즈:"+locker.size)
//        for (i in 0 until locker.size) {
//            val lockeritem: String = locker[i]
//            if (lockeritem.equals(itemData[i].name)) {
//                itemData[i].lock = true
//                Log.d(TAG, "////////////////// name: "+itemData[i].name+ " lock: "+ itemData[i].lock)
//            }
//        }


        //Itemadapter 클릭 리스너
        val itemAdapter = ItemAdapter(iContext,itemData,flower,locker)     // 어댑터

        itemAdapter.setItemClickListener(object: ItemAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Log.d(TAG, "onClick리스너: "+itemData[position].name)
                // 클릭 시 이벤트 작성
//                Toast.makeText(view?.context,
//                    "${itemData[position].name}\n${itemData[position].number}",
//                    Toast.LENGTH_SHORT).show()
                if(itemData[position].name.equals("reset")){
                    i_back.setImageResource(0)
                    i_timer.setImageResource(0)
                }
                if(itemData[position].name.equals("bg1")){
                i_back.setImageResource(R.drawable.bg1)
            }else if(itemData[position].name.equals("bg2")){
                i_back.setImageResource(R.drawable.bg2)
            }

            if(itemData[position].name.equals("timer1")){
                i_timer.setImageResource(R.drawable.timer1)
            }else if(itemData[position].name.equals("timer2")){
                i_timer.setImageResource(R.drawable.timer2)
            }
            }
        })

        //Itemadapter 클릭 리스너
        val payDialog= PayDialog(requireContext())     // 어댑터
        payDialog.setOnClickedListener(object: PayDialog.ButtonClickListener{
            override fun onClick(myName: String) {
                TODO("Not yet implemented")
                point.text = myName
            }
        })


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


