package com.example.smart_wb

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.timer_dialog.*


/**2021-06-02
joker
타이머 알림 세팅*/


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class FragmentSetting : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


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
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    //activity 에서 intent로 데이터 받을 때 사용
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //알림 상태 확인
        val audioManager =
            requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            Log.d(LockScreenActivity.TAG, "벨소리모드")
            tss.text = "벨소리"
        } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            Log.d(LockScreenActivity.TAG, "진동모드")
            tss.text = "진동"
        } else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            Log.d(LockScreenActivity.TAG, "무음모드")
            tss.text = "무음"
        }

        ts.setOnClickListener(){
            var builder = AlertDialog.Builder(context)

            var v1 = layoutInflater.inflate(R.layout.timer_dialog, null)
            builder.setView(v1)

            // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
            var listener = DialogInterface.OnClickListener { p0, p1 ->
                var alert = p0 as AlertDialog

                when(checkedId) {
                    R.id.bell1 -> r1Result.text = "오늘 저녁은? ${rb1.text}!"
                    R.id.bell2 -> r1Result.text = "오늘 저녁은? ${rb2.text}!"
                    R.id.bell3 -> r1Result.text = "오늘 저녁은? ${rb3.text}!"
                }

                val bell_result : String
                bell_result =  ${edit1?.text}
                bell1.isChecked = ${?.text}
                if(bell1.isChecked){
                    Toast.makeText(context,"벨소리로 변경되었습니다.",Toast.LENGTH_SHORT).show()
                }else if(bell2.isChecked){
                    Toast.makeText(context,"진동으로 변경되었습니다.",Toast.LENGTH_SHORT).show()
                }else if(bell3.isChecked){
                    Toast.makeText(context,"무음으로 변경되었습니다.",Toast.LENGTH_SHORT).show()
                }
//                var edit1: EditText? = alert.findViewById<EditText>(R.id.editText)
//                var edit2: EditText? = alert.findViewById<EditText>(R.id.editText2)
//
//                tv1.text = "${edit1?.text}"
//                tv1.append("${edit2?.text}")
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", null)

            builder.show()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSetting.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentSetting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}