package com.company.imetlin.fishmarker.fragments_marker

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.gpstracker.GpsTracker
import com.company.imetlin.fishmarker.singleton.Singleton
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add_marker_map.*
import java.io.Serializable


class MapMarkerFragment : Fragment() , OnMapReadyCallback {

    private var googlemap: GoogleMap? = null
    private lateinit var  mUiSettings : UiSettings
    private lateinit var context_fargment : Context

    private var latitude : Double = 0.0
    private var longitude : Double = 0.0


    private var alert_detail: AlertDialog.Builder? = null

    /////////////////////////////////////////////////ALERT DIALOG
    private var latitude_a: TextView? = null
    private var longitude_b: TextView? = null
    private var title_marker: TextView? = null
    private var date: TextView? = null
    private var depth: TextView? = null
    private var amount: TextView? = null
    private var note: TextView? = null
    private var title_alert: TextView? = null
    private var big_detail: ImageButton? = null
    private var idplace : String = ""


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
        context_fargment = requireActivity().baseContext
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

        val  extras : Bundle = requireActivity().intent.extras
        val latitude_place : Double = extras.getDouble("latitude")
        val longitude_place : Double = extras.getDouble("longitude")
        val zoom_place : Float = extras.getFloat("zoom")
        idplace = extras.getString("idplace")

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
            val hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_detail_marker)
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
        button_detail.setOnClickListener(View.OnClickListener
        {
            bigDetailMarker(marker)
            hideButtonSheet()

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
                        bundle.putSerializable("7",modelClass.idBait as Serializable)
                        bundle.putString("8", java.lang.String.valueOf(modelClass.depth))
                        bundle.putString("9", java.lang.String.valueOf(modelClass.amount))
                        bundle.putString("10", modelClass.note)
                        bundle.putString("11", modelClass.idplace)


                        fragment.arguments = bundle
                        fragment.setTargetFragment(this,1)
                        activity?.supportFragmentManager?.let { fragment.show(it, "CardMarkerFragment") }
                        hideButtonSheet()
                    }
                    }
            } else {
                Toast.makeText(context, R.string.foreign_markers, Toast.LENGTH_SHORT).show()
                hideButtonSheet()

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
            bundle.putString("idplace",idplace)
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
                resource.getDrawable(R.drawable.fishmy30_old, requireContext().theme)
            } else {
                resource.getDrawable(R.drawable.fishmy30_old)            }
            val bitmap_my = (myIconFish as BitmapDrawable).bitmap
            Singleton.createMarker(latitude, longitude, titleMarker, bitmap_my)

        }
    }
    fun hideButtonSheet() {

        if (bottom_sheet.visibility == View.VISIBLE) {
            val hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_detail_marker)
            bottom_sheet.startAnimation(hide)
            bottom_sheet.visibility = View.GONE
        }
    }
    fun bigDetailMarker(bigdetailmarker : Marker) {


        for (modelClass in Singleton.allmarkers) {
            if (modelClass.latitude == bigdetailmarker.position.latitude && modelClass.longitude == bigdetailmarker.position.longitude) {
                val li = LayoutInflater.from(context)
                val promptsView: View = li.inflate(R.layout.alert_detail_marker, null)

                alert_detail = AlertDialog.Builder(promptsView.context)
                alert_detail!!.setView(promptsView)

                title_alert = promptsView.findViewById<View>(R.id.title_alert) as TextView
                latitude_a = promptsView.findViewById<View>(R.id.latitude_alert) as TextView
                longitude_b = promptsView.findViewById<View>(R.id.longitude_alert) as TextView
                title_marker = promptsView.findViewById<View>(R.id.title) as TextView
                date = promptsView.findViewById<View>(R.id.date) as TextView
                depth = promptsView.findViewById<View>(R.id.depth) as TextView
                amount = promptsView.findViewById<View>(R.id.amount) as TextView
                note = promptsView.findViewById<View>(R.id.note) as TextView
                big_detail = promptsView.findViewById<View>(R.id.big_detail) as ImageButton

                val tf = Typeface.createFromAsset(context?.assets, "alert_font_title.ttf")
                title_alert?.typeface = tf
                latitude_a!!.text = latitude_a!!.text.toString() + " " + modelClass.latitude
                longitude_b!!.text = longitude_b!!.text.toString() + " " + modelClass.longitude
                title_marker?.text = title_marker?.text.toString() + " " + modelClass.title
                date?.text = date?.text.toString() + " " + modelClass.date
                depth?.text = depth?.text.toString() + " " + modelClass.depth
                amount?.text = amount?.text.toString() + " " + modelClass.amount
                note?.text = note?.text.toString() + " " + modelClass.note

                alert_detail?.setPositiveButton(
                    context?.resources?.getString(R.string.ok),
                    DialogInterface.OnClickListener { _, _ -> })
                val alert11: AlertDialog = alert_detail!!.create()
                alert11.window.setBackgroundDrawableResource(R.color.orange)
                alert11.show()
                val buttonbackground = alert11.getButton(DialogInterface.BUTTON_POSITIVE)
                buttonbackground.setTextColor(context?.resources?.getColor(R.color.colorWhite)!!)


                big_detail?.setOnClickListener {
                    val fragment: Fragment = BigDetailMarkerFragment()
                    alert11.cancel()

                    val bundle = Bundle()
                    bundle.putSerializable("serializedObject",modelClass)
                    fragment.arguments = bundle

                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.fragment_container_marker, fragment)
                        ?.commit()
                }
                break
            }
        }
    }
}
