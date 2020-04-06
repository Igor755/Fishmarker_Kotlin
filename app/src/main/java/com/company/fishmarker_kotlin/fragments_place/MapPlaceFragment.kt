package com.company.fishmarker_kotlin.fragments_place

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID

class MapPlaceFragment : Fragment(), OnMapReadyCallback {

    private var googlemap: GoogleMap? = null
    private var mUiSettings: UiSettings? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_place_map, container, false)
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_place, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.back_to_profile -> {
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container_place, AddPlaceFragment())
                    ?.commit()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    override fun onMapReady(mMap: GoogleMap?) {

        googlemap = mMap
        googlemap?.mapType = MAP_TYPE_HYBRID
        mUiSettings = googlemap?.uiSettings
        mUiSettings?.isZoomControlsEnabled = true

        googlemap?.setOnMapLongClickListener { point ->

            val latitude: Double = point.latitude
            val longitude: Double = point.longitude
            val zoom: Float = googlemap!!.cameraPosition.zoom
            val intent: Intent = activity!!.intent
            val nameBigWater: String = intent.getStringExtra("nameBigWater")

            val bundle = Bundle()
            bundle.putDouble("latitude", latitude)
            bundle.putDouble("longitude", longitude)
            bundle.putFloat("zoom", zoom)
            bundle.putString("nameBigWater", nameBigWater)

            val addPlaceDialogFragment = AddPlaceDialogFragment()
            addPlaceDialogFragment.arguments = bundle
            addPlaceDialogFragment.show(childFragmentManager, "AddPlaceDialogFragment")
        }
    }

}