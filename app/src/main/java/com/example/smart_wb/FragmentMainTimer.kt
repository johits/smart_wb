package com.example.smart_wb

import android.content.Context
import android.os.Bundle
import android.renderscript.ScriptGroup
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.smart_wb.databinding.FragmentMainTimerBinding
import kotlinx.android.synthetic.main.fragment_main_timer.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 21/05/31 yama 스크린타임 작업중
 */
class FragmentMainTimer : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mContext: Context
    //뷰바인딩 위한 변수
    private var _binding: FragmentMainTimerBinding?=null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //view를 구성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainTimerBinding.inflate(inflater, container, false)
        val view = binding.root

        view.start.setOnClickListener {
            Toast.makeText(mContext,"시작",Toast.LENGTH_LONG).show()
        }
        return view
    }

    //activity 에서 intent로 데이터 받을 때 사용
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentMainTimer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentMainTimer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //온클릭
    override fun onClick(p0: View?) {
       when(p0?.id) {

       }
    }

    //프래그먼트는 뷰보다 더 오래살아남는다.
    //바인딩 클래스는 뷰에 대한 참조를 가지고 있는데
    //뷰가 제거될 떄 바인딩 클래스의 인스턴스도 같이 정리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}