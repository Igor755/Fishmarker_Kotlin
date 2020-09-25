package com.company.imetlin.fishmarker.fragments_marker

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.customview.spinner.DataSpinner
import com.company.imetlin.fishmarker.modelclass.MarkerDetail
import com.company.imetlin.fishmarker.singleton.Singleton
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_marker_dialog.*
import kotlinx.android.synthetic.main.fragment_add_place_dialog.edit_latitude
import kotlinx.android.synthetic.main.fragment_add_place_dialog.edit_longitude
import kotlinx.android.synthetic.main.spinner_alert_dialog_listview_search.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardMarkerFragment : DialogFragment() {

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val TAG: String = "CardMarkerFragment"
    val listInt = emptyList<Int>()
    val array : MutableList<DataSpinner> = mutableListOf<DataSpinner>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_marker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar: Calendar = Calendar.getInstance()
        val bundle: Bundle? = arguments
        val latitude: Double = bundle?.getDouble("latitude")!!
        val longitude: Double = bundle.getDouble("longitude")
        btnDelete.visibility = View.INVISIBLE
        spinerFillUp(array)
        if (latitude == 0.0 && longitude == 0.0) {
            updateMarker()
        } else {
            addMarker()
        }
        edit_latitude.isEnabled = false
        edit_longitude.isEnabled = false
        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        tvDate.setOnClickListener {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog: DatePickerDialog = DatePickerDialog(
                context,
                R.style.ThemeOverlay_AppCompat_Dialog,
                dateSetListener,
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            tvDate.text = sdf.format(calendar.time)
        }

    }

    fun addMarker() {
        val bundle: Bundle? = arguments
        val latitude: Double = bundle?.getDouble("latitude")!!
        val longitude: Double = bundle.getDouble("longitude")
        val idplace: String = bundle.getString("idplace")
        edit_latitude.setText(latitude.toString())
        edit_longitude.setText(longitude.toString())
        btnOk.setOnClickListener {

            if (isEmpty()) {
                Toast.makeText(context, "Empty field", Toast.LENGTH_LONG).show()
            } else {
                val idUser: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
                val idMarker: String = UUID.randomUUID().toString()
                val latitude: Double = edit_latitude.text.toString().toDouble()
                val longitude: Double = edit_longitude.text.toString().toDouble()
                val titleMarker: String = edit_title_marker.text.toString()
                val dateMarker: String = tvDate.text.toString()
                val bait : MutableList<DataSpinner>? = searchMultiSpinnerUnlimited.selectedItems
                val dept: Double = edit_dept.text.toString().toDouble()
                val amountOfFish: Int = edit_number_of_fish.text.toString().toInt()
                val note: String = edit_note.text.toString()
                val newMarker = MarkerDetail(idUser, idMarker, latitude, longitude, titleMarker, dateMarker, bait, dept, amountOfFish, note,idplace)

                FirebaseDatabase.getInstance().getReference("Marker").child(idMarker).setValue(newMarker)
                val bundle = Bundle()
                bundle.putString("idMarker", idMarker)
                bundle.putString("titleMarker", titleMarker)
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
                val intent2 = Intent()
                intent2.putExtras(bundle)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent2)
                Singleton.addMarker(newMarker)
                dialog?.dismiss()
            }
        }
    }

    fun updateMarker() {
        val bundle: Bundle? = arguments
        val uidUpdate = bundle?.getString("1")
        val idMarkerUpdate = bundle?.getString("2")
        val latitudeUpdate = bundle?.getString("3")
        val longitudeUpdate = bundle?.getString("4")
        val titleUpdate = bundle?.getString("5")
        val dateUpdate = bundle?.getString("6")
        val idbait = arguments?.getSerializable("7") as MutableList<DataSpinner>
        val depthUpdate = bundle?.getString("8")
        val amountUpdate = bundle?.getString("9")
        val noteUpdate = bundle?.getString("10")
        val idplaceUpdate = bundle?.getString("11")
        edit_latitude.setText(latitudeUpdate)
        edit_longitude.setText(longitudeUpdate)
        edit_title_marker.setText(titleUpdate)
        tvDate.text = dateUpdate

        spinerFillUpdate(idbait)
       // searchMultiSpinnerUnlimited.setSelectedItems(idbait)
      /* searchMultiSpinnerUnlimited.setItems(idbait, -1) {
            for (i in idbait.indices) {
                idbait[i].isSelected = true
            }
        }
*/
        edit_dept.setText(depthUpdate)
        edit_number_of_fish.setText(amountUpdate)
        edit_note.setText(noteUpdate)
        btnDelete.visibility = View.VISIBLE
        btnDelete.setOnClickListener {
            deleteMarker(uidUpdate, idMarkerUpdate, latitudeUpdate, longitudeUpdate,
                titleUpdate, dateUpdate, idbait, depthUpdate,
                amountUpdate, noteUpdate, idplaceUpdate)

        }
        btnOk.setOnClickListener {
            if (isEmpty()) {
                Toast.makeText(context, R.string.fill, Toast.LENGTH_LONG).show()
            } else {
                val latitude: String = edit_latitude.text.toString()
                val longitude: String = edit_longitude.text.toString()
                val title: String = edit_title_marker.text.toString()
                val displayDate: String = tvDate.text.toString()
                val idbaite  = searchMultiSpinnerUnlimited.selectedItems
                val depth: String = edit_dept.text.toString()
                val amountoffish: String = edit_number_of_fish.text.toString()
                val note: String = edit_note.text.toString()
                if (idbaite.size == 0){
                    val markerInformation = MarkerDetail(
                        uidUpdate,
                        idMarkerUpdate,
                        java.lang.Double.valueOf(latitude),
                        java.lang.Double.valueOf(longitude),
                        title,
                        displayDate,
                        idbait,
                        java.lang.Double.valueOf(depth),
                        amountoffish.toInt(),
                        note,
                        idplaceUpdate
                    )
                    if (idMarkerUpdate != null) {
                        FirebaseDatabase.getInstance().getReference("Marker").child(idMarkerUpdate)
                            .setValue(markerInformation)
                    }
                    Toast.makeText(context, "update success", Toast.LENGTH_SHORT).show()
                    Singleton.UpdateMarker(markerInformation)
                    dialog?.dismiss()
                }else{
                    val markerInformation = MarkerDetail(
                        uidUpdate,
                        idMarkerUpdate,
                        java.lang.Double.valueOf(latitude),
                        java.lang.Double.valueOf(longitude),
                        title,
                        displayDate,
                        idbaite,
                        java.lang.Double.valueOf(depth),
                        amountoffish.toInt(),
                        note,
                        idplaceUpdate
                    )
                    if (idMarkerUpdate != null) {
                        FirebaseDatabase.getInstance().getReference("Marker").child(idMarkerUpdate)
                            .setValue(markerInformation)
                    }
                    Toast.makeText(context, "update success", Toast.LENGTH_SHORT).show()
                    Singleton.UpdateMarker(markerInformation)
                    dialog?.dismiss()
                }
            }
        }
    }

    fun deleteMarker(
        uidUpdate: String?,
        idMarkerUpdate: String?,
        latitudeUpdate: String?,
        longitudeUpdate: String?,
        titleUpdate: String?,
        dateUpdate: String?,
        idBait : MutableList<DataSpinner>,
        depthUpdate: String?,
        amountUpdate: String?,
        noteUpdate: String?,
        idplaceUpdate:String?
    ) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(R.string.you)
        alertDialog.setMessage(
            context?.resources?.getString(R.string.the) + "\n" +
                    context?.resources?.getString(R.string.from)
        )
        alertDialog.setPositiveButton(android.R.string.yes) { _, _ ->
            val modelClassDelete: MarkerDetail = MarkerDetail(
                uidUpdate,
                idMarkerUpdate,
                latitudeUpdate!!.toDouble(),
                longitudeUpdate!!.toDouble(),
                titleUpdate.toString(),
                dateUpdate.toString(),
                idBait,
                depthUpdate!!.toDouble(),
                Integer.parseInt(amountUpdate),
                noteUpdate.toString(),
                idplaceUpdate
            )
            val delmark = FirebaseDatabase.getInstance().getReference("Marker").child(idMarkerUpdate.toString())
            delmark.removeValue()
            Singleton.deleteMarker(modelClassDelete)
            Toast.makeText(context, android.R.string.yes, Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
        alertDialog.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(context, android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()


    }

    fun isEmpty(): Boolean {

        val baitArray : MutableList<DataSpinner>? = searchMultiSpinnerUnlimited.selectedItems
        return TextUtils.isEmpty(edit_latitude.text) ||
                TextUtils.isEmpty(edit_longitude.text) ||
                TextUtils.isEmpty(edit_title_marker.text) ||
                TextUtils.isEmpty(tvDate.text) ||
                baitArray?.size == 0 ||
                TextUtils.isEmpty(edit_dept.text) ||
                TextUtils.isEmpty(edit_number_of_fish.text) ||
                TextUtils.isEmpty(edit_note.text)
    }

    fun spinerFillUpdate(array :  MutableList<DataSpinner>) {

        //println(array)
        val listNameDataSpiner = listOf(*resources.getStringArray(R.array.bait_array))
        val listImageDataSpiner = listOf<Int>(
            R.drawable.bait_red_worm,
            R.drawable.bait_black_worm,
            R.drawable.bait_sea_worm,
            R.drawable.bait_maggot_white,
            R.drawable.bait_maggot_red,
            R.drawable.bait_bloodworm,
            R.drawable.bait_peas,
            R.drawable.bait_corn,
            R.drawable.bait_maybug_larva,
            R.drawable.bait_caddis_larva,
            R.drawable.bait_cazar,
            R.drawable.bait_fly,
            R.drawable.bait_bark_beetle,
            R.drawable.bait_live,
            R.drawable.bait_silicone,
            R.drawable.bait_spinner,
            R.drawable.bait_pinwheel,
            R.drawable.bait_wobbler,
            R.drawable.bait_muzzle_sight,
            R.drawable.bait_another
        )
        val listDataSpinner: MutableList<DataSpinner> = ArrayList<DataSpinner>()
        for (i in listNameDataSpiner.indices) {
            val dataSpinner = DataSpinner()
            dataSpinner.id = (i + 1).toLong()
            dataSpinner.name = listNameDataSpiner[i]
            dataSpinner.image = listImageDataSpiner[i]
            dataSpinner.isSelected = false
            listDataSpinner.add(dataSpinner)
        }
        searchMultiSpinnerUnlimited.setEmptyTitle("Not Data Found!")
        searchMultiSpinnerUnlimited.setSearchHint("Find Data")

        val iterator: MutableListIterator<DataSpinner> = listDataSpinner.listIterator()
        array.forEach {
            while (iterator.hasNext()) {
                val next: DataSpinner = iterator.next()
                if (it.name == next.name) {
                    next.isSelected = true
                    /*for (j in listDataSpinner.indices)
                       if (next.name == listDataSpinner[j].name){
                          listDataSpinner[j].isSelected = true}*/
                }
            }
        }
      /*  array.forEach {
            while (iterator.hasNext()) {
                val next: DataSpinner = iterator.next()
                if (listDataSpinner.contains(it)){
                    next.isSelected = true
                    *//*for (j in listDataSpinner.indices)
                       if (next.name == listDataSpinner[j].name){
                          listDataSpinner[j].isSelected = true}*//*
                }
            }
        }*/
       /* for (i in array.indices) {
            while (iterator.hasNext()) {
                val next: DataSpinner = iterator.next()
                if (array[i].name == next.name) {
                    iterator.next().isSelected = true
                    *//*for (j in listDataSpinner.indices)
                       if (next.name == listDataSpinner[j].name){
                          listDataSpinner[j].isSelected = true}*//* }
            }
        }*/
        searchMultiSpinnerUnlimited.setItems(listDataSpinner, -1) { items ->

        }

/*
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
                    val marker_update = Singleton.googlemap!!.addMarker(
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
                    val marker_update = Singleton.googlemap!!.addMarker(
                        MarkerOptions()
                            .position(LatLng(next.latitude, next.longitude))
                            .title(next.title)
                            .icon(BitmapDescriptorFactory.fromResource(my_ic))
                    )
                    markers_array.add(marker_update)
                }*/


    }


    fun spinerFillUp(array :  MutableList<DataSpinner>){

        val list = listOf(*resources.getStringArray(R.array.bait_array))
        val list2 = listOf<Int>(
            R.drawable.bait_red_worm,
            R.drawable.bait_black_worm,
            R.drawable.bait_sea_worm,
            R.drawable.bait_maggot_white,
            R.drawable.bait_maggot_red,
            R.drawable.bait_bloodworm,
            R.drawable.bait_peas,
            R.drawable.bait_corn,
            R.drawable.bait_maybug_larva,
            R.drawable.bait_caddis_larva,
            R.drawable.bait_cazar,
            R.drawable.bait_fly,
            R.drawable.bait_bark_beetle,
            R.drawable.bait_live,
            R.drawable.bait_silicone,
            R.drawable.bait_spinner,
            R.drawable.bait_pinwheel,
            R.drawable.bait_wobbler,
            R.drawable.bait_muzzle_sight,
            R.drawable.bait_another
        )
        val listArray0: MutableList<DataSpinner> = ArrayList<DataSpinner>()
        for (i in list.indices) {
            val h = DataSpinner()
            h.id = (i + 1).toLong()
            h.name = list[i]
            h.image = list2[i]
            h.isSelected = false
            listArray0.add(h)
        }
        searchMultiSpinnerUnlimited.setEmptyTitle("Not Data Found!")
        searchMultiSpinnerUnlimited.setSearchHint("Find Data")
        searchMultiSpinnerUnlimited.setItems(listArray0, -1) { items ->
            for (i in array.indices) {
                array[i].isSelected = true
                if (items[i].isSelected) {
                    Log.i(CardMarkerFragment().TAG, i.toString() + " : " + items[i].name + " : " + items[i].isSelected) }
            }
        }
    }
}
