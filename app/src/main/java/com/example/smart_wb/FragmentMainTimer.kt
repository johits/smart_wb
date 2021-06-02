package com.example.smart_wb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smart_wb.databinding.FragmentMainTimerBinding
import kotlinx.android.synthetic.main.fragment_main_timer.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 21/05/31 yama 스크린타임 타이머 시간 설정하는 프래그먼트
 */
class FragmentMainTimer : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mContext: Context

    //뷰바인딩 위한 변수
    private var _binding: FragmentMainTimerBinding? = null
    private val binding get() = _binding!!


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentMainTimer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        } else if (context is LockScreenActivity) {
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

        //numberPicker 최소,최대값 지정
        binding.npHour.minValue = 0
        binding.npHour.maxValue = 23
        binding.npMin.minValue = 0
        binding.npMin.maxValue = 59
        binding.npSec.minValue = 0
        binding.npSec.maxValue = 59

        var settingTime = 0
        view.start.setOnClickListener {
            //액티비티에 따라 동작을 달리한다.
            if (context is MainActivity) {
                settingTime = binding.npHour.value*3600+ binding.npMin.value*60+binding.npSec.value
                if(settingTime==0){
                    toastCenter(R.string.toast_time_set_blank_warning)
                }else{
                    val intent = Intent(mContext, LockScreenActivity::class.java)
                    intent.putExtra("settingTime", settingTime.toString())
                    startActivity(intent)
                }
            }
        }
        return view
    }

    //activity 에서 intent로 데이터 받을 때 사용
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    //온클릭
    override fun onClick(p0: View?) {
        when (p0?.id) {
        }
    }

    //프래그먼트는 뷰보다 더 오래살아남는다.
    //바인딩 클래스는 뷰에 대한 참조를 가지고 있는데
    //뷰가 제거될 떄 바인딩 클래스의 인스턴스도 같이 정리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //토스트 메세지 화면 중앙
    fun toastCenter(message: Int){
        val toast = Toast.makeText(mContext,message,Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER,Gravity.CENTER_HORIZONTAL,Gravity.CENTER_VERTICAL)
        toast.show()
    }
}