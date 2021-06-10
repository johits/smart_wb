package com.example.smart_wb

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class AdapterMainFragment(fm : FragmentManager, private val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int) : Fragment {
        return when(position){
            0 -> FragmentMainTimer()
            1 -> FragmentCalendar()
            2 -> FragmentChart()
            3 -> FragmentItem()
//            4 -> FragmentSetting()
            else -> FragmentMainTimer()
        }
    }

    override fun getCount(): Int = fragmentCount


}