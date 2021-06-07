package com.example.smart_wb

data class ItemData(
    val item : Int,
    val name : String,
    val price : Int,
    val lock : Boolean,
    var bg : Boolean = false
)