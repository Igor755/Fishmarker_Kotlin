package com.company.fishmarker_kotlin.fragments_place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_place.view.*

class MapPlaceFragment : Fragment(), OnMapReadyCallback {


    private var googlemap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_add_place_map, container, false)

        val mapFragment : SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view


    }
    override fun onMapReady(mMap: GoogleMap?) {

        googlemap = mMap
        googlemap?.mapType = MAP_TYPE_HYBRID

        googlemap?.setOnMapLongClickListener { point ->

            val latitude : Double = point.latitude
            val longitude : Double = point.longitude
            var bundle = Bundle()
            bundle.putDouble("latitude",latitude)
            bundle.putDouble("longitude", longitude)

            val dialog_fragment = AddPlaceDialogFragment()
            dialog_fragment.arguments = bundle
            dialog_fragment.show(childFragmentManager, "AddPlaceDialogFragment")

        }

    }
}