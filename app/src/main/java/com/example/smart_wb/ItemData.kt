package com.example.smart_wb

import android.net.Uri

data class ItemData (
    val item : Uri,
    val price : Int,
    val lock : Boolean
)