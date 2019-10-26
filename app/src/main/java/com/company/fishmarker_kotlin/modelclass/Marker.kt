package com.company.fishmarker_kotlin.modelclass

data class Marker(
    var uid : String,
    var id_marker_key : String,
    var latitude : Double,
    var longitude : Double,
    var title : String,
    var date : String,
    var depth : Double,
    var amount : Int,
    var note: String)
