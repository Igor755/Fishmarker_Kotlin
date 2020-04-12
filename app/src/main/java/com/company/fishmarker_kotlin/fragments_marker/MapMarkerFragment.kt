package com.company.fishmarker_kotlin.fragments_marker

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.gpstracker.GpsTracker
import com.company.fishmarker_kotlin.singleton.Singleton
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_marker_map.*


class MapMarkerFragment : Fragment() , OnMapReadyCallback {

    private var googlemap: GoogleMap? = null
    private lateinit var  mUiSettings : UiSettings
    private lateinit var context_fargment : Context

    private var latitude : Double = 0.0
    private var longitude : Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_marker, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.add_marker_on_gps -> {
                addMarkerOnGPSCoordinate()
                true
            }
            R.id.back_to_place_activity -> {
                activity?.finish()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }
    fun addMarkerOnGPSCoordinate(){

        if (ContextCompat.checkSelfPermission(context_fargment, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val gps = context?.let { GpsTracker(it) }

            latitude = gps!!.getLatitude()
            longitude = gps.getLongitude()
            if (latitude == 0.0 && longitude == 0.0) {
                gps.showSettingsAlert()
            } else {
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(latitude, longitude))
                    .zoom(15f)
                    .build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                googlemap!!.animateCamera(cameraUpdate)
                val ismarkerExist: Boolean = Singleton.searchMarker(latitude, longitude)
                if (!ismarkerExist) {
                    onMapLongClick(latitude, longitude)
                } else {
                    Toast.makeText(context, R.string.unique, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    2
                )
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_marker_map, container, false)
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        context_fargment = activity!!.baseContext
        Singleton.setContext(context_fargment)
        return view
    }

    override fun onMapReady(mMap: GoogleMap?) {

        googlemap = mMap
        googlemap?.mapType = GoogleMap.MAP_TYPE_HYBRID
        mUiSettings = googlemap!!.uiSettings
        mUiSettings.isZoomControlsEnabled = true
        mUiSettings.isMapToolbarEnabled = false
        bottom_sheet.visibility = View.GONE

        val  extras : Bundle = activity!!.intent.extras
        val latitude_place : Double = extras.getDouble("latitude")
        val longitude_place : Double = extras.getDouble("longitude")
        val zoom_place : Float = extras.getFloat("zoom")

        val cameraPosition : CameraPosition? = CameraPosition.Builder()
            .target(LatLng(latitude_place,longitude_place))
            .zoom(zoom_place)
            .build()
        val cameraUpdate : CameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googlemap?.animateCamera(cameraUpdate)

        googlemap?.setOnMapLongClickListener {point ->
            onMapLongClick(point.latitude, point.longitude)
        }
        googlemap?.setOnMarkerClickListener{marker ->
            onMarkerClick(marker)
        }
        googlemap?.setOnMapClickListener{ _ ->
            onMapClick()
        }
        Singleton.LoaderData(mMap)

    }
    fun onMapClick(){
        if (bottom_sheet.visibility == View.VISIBLE) {
            val hide = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.hide_detail_marker
            )
            bottom_sheet.startAnimation(hide)
            bottom_sheet.visibility = View.GONE
            mUiSettings.isZoomControlsEnabled = true
        } else {
            bottom_sheet.visibility = View.GONE
            mUiSettings.isZoomControlsEnabled = true
        }
    }

    fun onMarkerClick(marker : Marker): Boolean {

        marker.showInfoWindow()
        mUiSettings.isZoomControlsEnabled = false
        val show = AnimationUtils.loadAnimation(
            getApplicationContext(),
            R.anim.show_detail_marker
        )
        bottom_sheet.startAnimation(show)
        bottom_sheet.visibility = View.VISIBLE
        button_detail.setOnClickListener(View.OnClickListener {
            Singleton.DetailMarker(marker)
        })
        button_edit.setOnClickListener(View.OnClickListener {
            var isMyMarker = false
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            for (item in Singleton.allmarkers) {
                if (item.longitude == marker.position.longitude && item.latitude == marker.position.latitude &&
                    item.title == marker.title && userId == item.uid
                ) {
                    isMyMarker = true
                    break
                }
            }
            if (isMyMarker) {
                for (modelClass in Singleton.allmarkers) {
                    if (modelClass.latitude == marker.position.latitude && modelClass.longitude == marker.position.longitude) {

                        val fragment : DialogFragment =  CardMarkerFragment()
                        val bundle  =  Bundle()
                        bundle.putString("1", modelClass.uid)
                        bundle.putString("2", modelClass.id_marker_key)
                        bundle.putString("3", java.lang.String.valueOf(modelClass.latitude))
                        bundle.putString("4", java.lang.String.valueOf(modelClass.longitude))
                        bundle.putString("5", modelClass.title)
                        bundle.putString("6", modelClass.date)
                        bundle.putString("7", java.lang.String.valueOf(modelClass.depth))
                        bundle.putString("8", java.lang.String.valueOf(modelClass.amount))
                        bundle.putString("9", modelClass.note)

                        fragment.arguments = bundle
                        fragment.setTargetFragment(this,1)
                        activity?.supportFragmentManager?.let { fragment.show(it, "CardMarkerFragment") }

                    }
                    }
            } else {
                Toast.makeText(context, R.string.foreign_markers, Toast.LENGTH_SHORT)
                    .show()
            }
        })
        return true

    }
    fun onMapLongClick(latitude : Double, longitude : Double){

        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.add_marker_on_map))
        builder.setMessage(
                context?.resources?.getString(R.string.add_marker_s) + "\n" +
                        getString(R.string.lat_c) + " " + latitude + "\n" +
                        getString(R.string.lon_c) + " " + longitude)
        builder.setPositiveButton(R.string.yes){ _, _ ->
            val fragment : DialogFragment  =  CardMarkerFragment()
            val bundle  =  Bundle()
            bundle.putDouble("latitude", latitude)
            bundle.putDouble("longitude", longitude)
            fragment.arguments = bundle

            fragment.setTargetFragment(this,1)
            activity?.supportFragmentManager?.let { fragment.show(it, "CardMarkerFragment") }
            Toast.makeText(context,R.string.go_go,Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.no){ _, _ ->
            Toast.makeText(context,R.string.agree,Toast.LENGTH_SHORT).show()
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
            val myIconFish : Drawable? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                resource.getDrawable(R.drawable.fishmy30, context!!.theme)
            } else {
                resource.getDrawable(R.drawable.fishmy30)            }
            val bitmap_my = (myIconFish as BitmapDrawable).bitmap
            Singleton.createMarker(latitude, longitude, titleMarker, bitmap_my)

        }
    }

}
