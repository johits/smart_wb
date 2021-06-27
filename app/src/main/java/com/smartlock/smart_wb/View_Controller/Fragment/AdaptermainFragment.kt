package com.smartlock.smart_wb.View_Controller.Fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.smart_wb.View_Controller.Fragment.FragmentCalendar
import com.example.smart_wb.View_Controller.Fragment.FragmentChart

class AdapterMainFragment(fm : FragmentManager, private val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int) : Fragment {
        return when(position){
            0 -> FragmentMainTimer()
            1 -> FragmentCalendar()
            2 -> FragmentChart()
            3 -> FragmentItem()
            else -> FragmentMainTimer()
        }
    }

    override fun getCount(): Int = fragmentCount


}