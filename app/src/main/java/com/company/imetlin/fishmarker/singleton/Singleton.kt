package com.company.imetlin.fishmarker.singleton

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.fragments_marker.CardMarkerFragment
import com.company.imetlin.fishmarker.model.MarkerDetail
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

       /* @SuppressLint("SetTextI18n")
        fun DetailMarker(detailmarker: Marker) {

            for (modelClass in allmarkers) {
                if (modelClass.latitude == detailmarker.position.latitude && modelClass.longitude == detailmarker.position.longitude) {

                     //   MapMarkerFragment().bigDetailMarker(modelClass, context)
                    break
                }
            }
        }*/

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






