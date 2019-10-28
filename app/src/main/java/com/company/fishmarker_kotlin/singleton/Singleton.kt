package com.company.fishmarker_kotlin.singleton

import android.graphics.drawable.Drawable
import com.company.fishmarker_kotlin.modelclass.Marker
import com.google.android.gms.maps.GoogleMap

class Singleton {


    companion object {


        @JvmStatic
        var allmarkers: ArrayList<Marker> = arrayListOf()
        
        private var googlemap: GoogleMap? = null


        fun addMarker(marker: Marker) {
            allmarkers.add(marker)
        }

        fun createMarker(lat: Double?, lon: Double?, title: String?, icon: Drawable) {

            /*val marker : Marker  = googlemap.addMarker(new MarkerOptions()
            .position(new LatLng(_lat, _lon))
            .title(title_marker)
            .icon(BitmapDescriptorFactory.fromBitmap(icon_marker)));
        markers.add(marker)*/

        }


    }

}




