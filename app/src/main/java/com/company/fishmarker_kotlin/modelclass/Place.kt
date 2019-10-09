package com.company.fishmarker_kotlin.modelclass

data class Place(
    var id: String,
    var uid: String?,
    var name: String,
    var latitude: Double?,
    var longitude: Double?,
    var zoom: Float?,
    var bigwater: String?) {

    constructor(name: String, latitude: Double, longitude: Double, zoom: Float) :
            this("", "", name, latitude, longitude, zoom, "")

    constructor() :
            this("", "", "", 0.0, 0.0,0.0f, "")
}


