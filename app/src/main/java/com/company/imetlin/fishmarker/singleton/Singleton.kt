package com.company.imetlin.fishmarker.singleton

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.fragments_marker.CardMarkerFragment
import com.company.imetlin.fishmarker.modelclass.MarkerDetail
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Singleton {


    companion object {


        @JvmStatic
        var allmarkers: ArrayList<MarkerDetail> = arrayListOf()

        private var googlemap: GoogleMap? = null
        private var markers: ArrayList<Marker>? = null
        private lateinit var context: Context
        private var cardMarkerActivity: CardMarkerFragment? = null
        private var alert_detail: AlertDialog.Builder? = null


        /////////////////////////////////////////////////ALERT DIALOG
        private var latitude: TextView? = null
        private var longitude: TextView? = null
        private var title_marker: TextView? = null
        private var date: TextView? = null
        private var depth: TextView? = null
        private var amount: TextView? = null
        private var note: TextView? = null
        private var title_alert: TextView? = null


        fun setContext(context: Context) {

            this.context = context

        }

        fun addMarker(marker: MarkerDetail) {
            allmarkers.add(marker)
        }

        fun createMarker(lat: Double?, lon: Double?, title: String?, icon: Bitmap) {

            val marker: Marker? = googlemap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(lat!!, lon!!))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
            )


            if (marker != null) {
                markers?.add(marker)
            }


        }

        fun LoaderData(_googlemap: GoogleMap?) {

            this.allmarkers = ArrayList<MarkerDetail>()
            this.markers = ArrayList<Marker>()
            googlemap = _googlemap
            this.cardMarkerActivity = cardMarkerActivity
            //val res: Resources = context!!.resources

            val resource: Resources = context.resources!!

            val myIconFish: Drawable? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                resource.getDrawable(
                    R.drawable.fishmy30_old,
                    context.theme
                )
            } else {
                resource.getDrawable(R.drawable.fishmy30_old)
            }
            val bitmap_my = (myIconFish as BitmapDrawable).bitmap

            val anotherIconFish: Drawable? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    resource.getDrawable(R.drawable.fishanother30,
                        context.theme
                    )
                } else {
                    resource.getDrawable(R.drawable.fishanother30)
                }
            val bitmap_another = (anotherIconFish as BitmapDrawable).bitmap


            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Marker")

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {

                        val markerInformation: MarkerDetail =
                            dataSnapshot1.getValue(MarkerDetail::class.java)!!
                        allmarkers.add(markerInformation)

                        System.out.println(markerInformation)
                    }
                    for (i in 0 until allmarkers.size) {

                        val latitude: Double = allmarkers[i].latitude
                        val longitude: Double = allmarkers[i].longitude
                        val tittle: String = allmarkers[i].title
                        val uid: String? = allmarkers[i].uid
                        if (uid == FirebaseAuth.getInstance().currentUser!!.uid) {
                            createMarker(latitude, longitude, tittle, bitmap_my)
                        } else if (uid != FirebaseAuth.getInstance().currentUser!!.uid) {
                            createMarker(latitude, longitude, tittle, bitmap_another)
                        }
                    }
                    System.out.println(allmarkers)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })
        }

        @SuppressLint("SetTextI18n")
        fun DetailMarker(detailmarker: Marker) {

            for (modelClass in allmarkers) {
                if (modelClass.latitude == detailmarker.position.latitude && modelClass.longitude == detailmarker.position.longitude) {

                    val li = LayoutInflater.from(context)
                    val promptsView: View = li.inflate(R.layout.alert_detail_marker, null)

                    alert_detail = AlertDialog.Builder(promptsView.context)
                    alert_detail!!.setView(promptsView)

                    title_alert = promptsView.findViewById<View>(R.id.title_alert) as TextView
                    latitude = promptsView.findViewById<View>(R.id.latitude_alert) as TextView
                    longitude = promptsView.findViewById<View>(R.id.longitude_alert) as TextView
                    title_marker = promptsView.findViewById<View>(R.id.title) as TextView
                    date = promptsView.findViewById<View>(R.id.date) as TextView
                    depth = promptsView.findViewById<View>(R.id.depth) as TextView
                    amount = promptsView.findViewById<View>(R.id.amount) as TextView
                    note = promptsView.findViewById<View>(R.id.note) as TextView

                    val tf = Typeface.createFromAsset(context.assets, "alert_font_title.ttf")
                    title_alert?.typeface = tf
                    latitude?.text = latitude?.text.toString() + " " + modelClass.latitude
                    longitude?.text = longitude?.text.toString() + " " + modelClass.longitude
                    title_marker?.text = title_marker?.text.toString() + " " + modelClass.title
                    date?.text = date?.text.toString() + " " + modelClass.date
                    depth?.text = depth?.text.toString() + " " + modelClass.depth
                    amount?.text = amount?.text.toString() + " " + modelClass.amount
                    note?.text = note?.text.toString() + " " + modelClass.note


                    alert_detail?.setPositiveButton(context.resources.getString(R.string.ok), DialogInterface.OnClickListener { _, _ -> })

                    val alert11: AlertDialog = alert_detail!!.create()
                    alert11.window.setBackgroundDrawableResource(R.color.orange)
                    alert11.show()
                    val buttonbackground = alert11.getButton(DialogInterface.BUTTON_POSITIVE)

                    buttonbackground.setTextColor(context.resources.getColor(R.color.colorWhite))
                    break
                }
            }
        }

        fun UpdateMarker(updatemarker: MarkerDetail) {

            googlemap!!.clear()

            var my_ic: Int = R.drawable.fishmy30_old
            val markers_array = java.util.ArrayList<Marker>()

            val iterator: MutableListIterator<MarkerDetail> =
                allmarkers.listIterator()

            while (iterator.hasNext()) {
                val next: MarkerDetail = iterator.next()
                if (!next.uid.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    my_ic = R.drawable.fishanother30
                } else {
                    my_ic = R.drawable.fishmy30_old
                }
                if (next.latitude.equals(updatemarker.latitude) &&
                    next.longitude.equals(updatemarker.longitude)
                ) {
                    iterator.set(updatemarker)
                    val marker_update = googlemap!!.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    updatemarker.latitude,
                                    updatemarker.longitude
                                )
                            )
                            .title(updatemarker.title)
                            .icon(BitmapDescriptorFactory.fromResource(my_ic))
                    )
                    markers_array.add(marker_update)
                } else {
                    val marker_update = googlemap!!.addMarker(
                        MarkerOptions()
                            .position(LatLng(next.latitude, next.longitude))
                            .title(next.title)
                            .icon(BitmapDescriptorFactory.fromResource(my_ic))
                    )
                    markers_array.add(marker_update)
                }
            }

            markers = java.util.ArrayList(markers_array)


        }
        fun deleteMarker(deletemarker : MarkerDetail){

            googlemap!!.clear()

            var my_ic: Int = R.drawable.fishmy30_old

            val markers_array = java.util.ArrayList<Marker>()
            val iterator: MutableListIterator<MarkerDetail> =
                allmarkers.listIterator()
            while (iterator.hasNext()) {
                val next: MarkerDetail = iterator.next()
                if (!next.uid.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    my_ic = R.drawable.fishanother30
                } else {
                    my_ic = R.drawable.fishmy30_old
                }
                if (next.latitude.equals(deletemarker.latitude) &&
                    next.longitude.equals(deletemarker.longitude)
                ) {
                    iterator.remove()
                } else {
                    val marker_delete = googlemap!!.addMarker(
                        MarkerOptions()
                            .position(LatLng(next.latitude, next.longitude))
                            .title(next.title)
                            .icon(BitmapDescriptorFactory.fromResource(my_ic))
                    )
                    markers_array.add(marker_delete)
                }
            }
            markers = java.util.ArrayList(markers_array)

        }
        fun searchMarker(lat: Double, lon: Double): Boolean {
            for (marker in markers!!) {
                if (marker.position.latitude == lat &&
                    marker.position.longitude == lon
                ) {
                    return true
                }
            }
            return false
        }

    }


}






