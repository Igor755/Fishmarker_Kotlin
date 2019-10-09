package com.company.fishmarker_kotlin.fragments_place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_place.view.*

class MapPlaceFragment : Fragment(), OnMapReadyCallback {


    private var googlemap: GoogleMap? = null

    private var mUiSettings: UiSettings? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_add_place_map, container, false)

        val mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view


    }
    override fun onMapReady(mMap: GoogleMap?) {

        googlemap = mMap
        googlemap?.mapType = MAP_TYPE_HYBRID
        mUiSettings = googlemap?.uiSettings
        mUiSettings?.isZoomControlsEnabled = true




        googlemap?.setOnMapLongClickListener { point ->

            val latitude : Double = point.latitude
            val longitude : Double = point.longitude
            val zoom : Float = googlemap!!.cameraPosition.zoom
            val intent : Intent = activity!!.intent
            val nameBigWater : String = intent.getStringExtra("nameBigWater")

            var bundle = Bundle()
            bundle.putDouble("latitude",latitude)
            bundle.putDouble("longitude", longitude)
            bundle.putFloat("zoom", zoom)
            bundle.putString("nameBigWater", nameBigWater)




            val dialog_fragment = AddPlaceDialogFragment()
            dialog_fragment.arguments = bundle
            dialog_fragment.show(childFragmentManager, "AddPlaceDialogFragment")

        }

    }
}