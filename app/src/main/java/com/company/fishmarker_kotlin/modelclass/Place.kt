package com.company.fishmarker_kotlin.modelclass

data class Place(
    var name_place: String,
    var latitude: Double,
    var longitude: Double,
    var zoom: Double,
    var uid: String?,
    var place_id: String?,
    var water_object: Int?){

    constructor(name_place: String, latitude: Double, longitude: Double, zoom: Double) :
            this(name_place, latitude, longitude, zoom, null,null, null)
}


