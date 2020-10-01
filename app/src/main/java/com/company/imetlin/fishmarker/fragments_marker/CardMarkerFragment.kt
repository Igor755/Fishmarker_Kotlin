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
import androidx.fragment.app.Fragment
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

class CardMarkerFragment :Fragment() {

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val TAG: String = "CardMarkerFragment"
    val arraySpinner : MutableList<DataSpinner> = mutableListOf<DataSpinner>()
    private var existMarker: MarkerDetail? = null

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
        spinerFillUp(arraySpinner)
        if (latitude == 0.0 && longitude == 0.0) {
            updateMarker()
        } else {
            addMarker()
        }
        edit_latitude.isEnabled = false
        edit_longitude.isEnabled = false
        btnCancel.setOnClickListener {
            activity?.onBackPressed()
            //dialog?.dismiss()
        }
        tvDate.setOnClickListener {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog: DatePickerDialog = DatePickerDialog(context, R.style.ThemeOverlay_AppCompat_Dialog, dateSetListener,
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
//                startActivityForResult(intent2, Activity.RESULT_OK)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent2)
                Singleton.addMarker(newMarker)
                activity?.onBackPressed()
                //dialog?.dismiss()
            }
        }
    }

    fun updateMarker() {

        if (arguments?.getParcelable<MarkerDetail>("existMarker") != null) {
            existMarker = arguments?.getParcelable<MarkerDetail>("existMarker")
        }

        val bundle: Bundle? = arguments
        val uidUpdate = existMarker?.uid
        val idMarkerUpdate = existMarker?.id_marker_key
        val latitudeUpdate = existMarker?.latitude
        val longitudeUpdate = existMarker?.longitude
        val titleUpdate = existMarker?.title
        val dateUpdate = existMarker?.date
        val idbait = existMarker?.idBait
        val depthUpdate = existMarker?.depth
        val amountUpdate = existMarker?.amount
        val noteUpdate = existMarker?.note
        val idplaceUpdate = existMarker?.idplace
        edit_latitude.setText(latitudeUpdate.toString())
        edit_longitude.setText(longitudeUpdate.toString())
        edit_title_marker.setText(titleUpdate)
        tvDate.text = dateUpdate

        idbait?.let { spinerFillUp(it) }

        edit_dept.setText(depthUpdate.toString())
        edit_number_of_fish.setText(amountUpdate.toString())
        edit_note.setText(noteUpdate)
        btnDelete.visibility = View.VISIBLE
        btnDelete.setOnClickListener {
            idbait?.let { it1 ->
                deleteMarker(uidUpdate, idMarkerUpdate, latitudeUpdate.toString(), longitudeUpdate.toString(),
                    titleUpdate, dateUpdate, it1, depthUpdate.toString(),
                    amountUpdate.toString(), noteUpdate, idplaceUpdate)
            }

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
                    activity?.onBackPressed()
                    //dialog?.dismiss()
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
                    activity?.onBackPressed()
                   // dialog?.dismiss()
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
            activity?.onBackPressed()
            //dialog?.dismiss()
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

    fun spinerFillUp(arraySpinerUpdate :  MutableList<DataSpinner>){

        val listNameBait = listOf(*resources.getStringArray(R.array.bait_array))
        val listImageBait = listOf<Int>(
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
        for (i in listNameBait.indices) {
            val dataSpinner = DataSpinner()
            dataSpinner.id = (i + 1).toLong()
            dataSpinner.name = listNameBait[i]
            dataSpinner.image = listImageBait[i]
            dataSpinner.isSelected = false
            listDataSpinner.add(dataSpinner)
        }
        if (arraySpinerUpdate.size != 0){
            var iterator: MutableListIterator<DataSpinner> = listDataSpinner.listIterator()
            arraySpinerUpdate.forEach {
                println(it)
                while (iterator.hasNext()) {
                    val next: DataSpinner = iterator.next()
                    if (it.name == next.name) {
                        next.isSelected = true
                    }
                }
                iterator = listDataSpinner.listIterator()
            }
            /*
            for (spin in listDataSpinner) {
                for (spin2 in array) {
                    if (spin.name == spin2.name)
                        spin.isSelected = true
                }
            }*/
        }
        searchMultiSpinnerUnlimited.setEmptyTitle("Not Data Found!")
        searchMultiSpinnerUnlimited.setSearchHint("Find Data")
        searchMultiSpinnerUnlimited.setItems(listDataSpinner, -1) { items ->
            for (i in arraySpinerUpdate.indices) {
                arraySpinerUpdate[i].isSelected = true
                if (items[i].isSelected) {
                    Log.i(CardMarkerFragment().TAG, i.toString() + " : " + items[i].name + " : " + items[i].isSelected) }
            }
        }
    }
}
