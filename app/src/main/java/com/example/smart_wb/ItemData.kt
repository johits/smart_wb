package com.example.smart_wb

data class ItemData(
    val item : Int,
    val name : String,
    val price : Int,
    var lock : Boolean =false,
    val type : String,
    var bg : Boolean = false,
    var timer : Boolean = false
)