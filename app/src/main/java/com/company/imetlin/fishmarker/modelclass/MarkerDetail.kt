package com.company.imetlin.fishmarker.modelclass

import java.io.Serializable

data class MarkerDetail(
    var uid: String?,
    var id_marker_key: String?,
    var latitude: Double,
    var longitude: Double,
    var title: String,
    var date: String,
    var depth: Double,
    var amount: Int,
    var note: String) : Serializable{


    constructor() :
            this("", "", 0.0, 0.0, "","", 0.0, 0, "")
}
