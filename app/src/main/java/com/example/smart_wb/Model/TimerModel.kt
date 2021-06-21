package com.example.smart_wb.Model

import java.util.*
import kotlin.collections.ArrayList


class TimerModel  : Observable() {
    // declaring a list of integer
    val List: MutableList<Int>

    // 목록을 초기화하는 생성자
    init {
        // 목록 요소를위한 공간 예약
        List = ArrayList(3)

        // 목록에 요소 추가
        List.add(0)
        List.add(0)
        List.add(0)
    }

    // getter 및 setter 함수 정의
    // 적절한 개수를 반환하는 함수
    // 올바른 인덱스에있는 값
    @Throws(IndexOutOfBoundsException::class)
    fun getValueAtIndex(the_index: Int): Int {
        return List[the_index]
    }

    // 사용자가 터치 할 때 액티비티 버튼의
    // 카운트 값을 변경하는 함수
    @Throws(IndexOutOfBoundsException::class)
    fun setValueAtIndex(the_index: Int) {
        List[the_index] = List[the_index] + 1
        setChanged()
        notifyObservers()
    }
}