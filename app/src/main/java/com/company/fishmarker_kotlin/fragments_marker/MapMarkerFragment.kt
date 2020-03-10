package com.company.fishmarker_kotlin.fragments_marker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.getIntent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.Resource
import com.company.fishmarker_kotlin.MarkerActivity
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.singleton.Singleton
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso



class MapMarkerFragment : Fragment() , OnMapReadyCallback {


    private var googlemap: GoogleMap? = null
    private lateinit var  mUiSettings : UiSettings


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_add_marker_map, container, false)

        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(mMap: GoogleMap?) {

        googlemap = mMap
        googlemap?.mapType = GoogleMap.MAP_TYPE_HYBRID


        mUiSettings = googlemap!!.uiSettings
        mUiSettings.isZoomControlsEnabled = true
        mUiSettings.isMapToolbarEnabled = false


        val  extras : Bundle = activity!!.intent.extras

        val latitude_place : Double = extras.getDouble("latitude")
        val longitude_place : Double = extras.getDouble("longitude")
        val zoom_place : Float = extras.getFloat("zoom")



        /*  val bundle : Bundle? = arguments
           val latitude_place : Double = bundle?.getDouble("latitude")!!
           val longitude_place : Double = bundle.getDouble("longitude")
           val zoom_place : Float = bundle.getFloat("zoom")*/

        val cameraPosition : CameraPosition? = CameraPosition.Builder()
            .target(LatLng(latitude_place,longitude_place))
            .zoom(zoom_place)
            .build()
        val cameraUpdate : CameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googlemap?.animateCamera(cameraUpdate)

        googlemap?.setOnMapLongClickListener {point ->

            onMapLongClick(point.latitude, point.longitude)
        }



    }
    fun onMapLongClick(latitude : Double, longitude : Double){

        val builder = AlertDialog.Builder(context)

        builder.setTitle("Add marker on map?")

        builder.setMessage(
                "Are you seriosly want add marker?" + "\n" +
                "Latitude: " + latitude + "\n" +
                "Longitude: " + longitude)

        builder.setPositiveButton("YES"){dialog, which ->

            val fragment : DialogFragment  =  CardMarkerFragment()
            val bundle  =  Bundle()
            bundle.putDouble("latitude", latitude)
            bundle.putDouble("longitude", longitude)
            fragment.arguments = bundle




            fragment.setTargetFragment(this,1)

            activity?.supportFragmentManager?.let { fragment.show(it, "CardMarkerFragment") }

            /*fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_marker, fragment)
                ?.addToBackStack(null)
                ?.commit()*/


            Toast.makeText(context,"Go, go , go add marker.",Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No"){dialog,which ->
            Toast.makeText(context,"You are not agree.",Toast.LENGTH_SHORT).show()
        }


        val dialog: AlertDialog = builder.create()

        dialog.show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK) {



            val titleMarker = data?.getStringExtra("titleMarker")
            val idMarker = data?.getStringExtra("idMarker")
            val latitude: Double? = data?.getDoubleExtra("latitude",0.0)
            val longitude: Double? = data?.getDoubleExtra("longitude", 0.0)

            val resource : Resources = context?.resources!!

            val myIconFish : Drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                resource.getDrawable(R.drawable.photo_user, context!!.theme)
            } else {
                resource.getDrawable(R.drawable.photo_user)            }

            Singleton.createMarker(latitude, longitude, titleMarker, myIconFish)

        }
    }

}
