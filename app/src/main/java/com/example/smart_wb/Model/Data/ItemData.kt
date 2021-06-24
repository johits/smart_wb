package com.example.smart_wb.Model.Data

data class ItemData(
    var item : Int,
    var name : String,
    var price : Int,
    var lock : Boolean =false,
    var type : String,
    var bg : Boolean = false,
    var timer : Boolean = false,
    var bcheck :Boolean = false,
    var tcheck : Boolean = false

)

