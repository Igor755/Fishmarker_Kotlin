package com.company.imetlin.fishmarker.model

import android.os.Parcelable
import com.company.imetlin.fishmarker.customview.spinner.DataSpinner
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class MarkerDetail(
    var uid: String?,
    var id_marker_key: String?,
    var latitude: Double,
    var longitude: Double,
    var title: String,
    var date: String,
    var idBait: MutableList<DataSpinner>?,
    var depth: Double,
    var amount: Int,
    var note: String,
    var idplace: String?) : Serializable, Parcelable{


    constructor() :
            this("", "", 0.0, 0.0, "","", null,0.0, 0, "", "")


}
