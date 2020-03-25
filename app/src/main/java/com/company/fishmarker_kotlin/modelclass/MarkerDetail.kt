package com.company.fishmarker_kotlin.modelclass

data class MarkerDetail(
    var uid: String?,
    var id_marker_key: String?,
    var latitude: Double,
    var longitude: Double,
    var title: String,
    var date: String,
    var depth: Double,
    var amount: Int,
    var note: String){


    constructor() :
            this("", "", 0.0, 0.0, "","", 0.0, 0, "")





}

/*public fun  getCoordinates() : Double[] {

    double[] cats = {latitude, longitude};

    return cats;
}*/
