package com.company.fishmarker_kotlin.fragments_marker

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.MarkerDetail
import com.company.fishmarker_kotlin.singleton.Singleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_place_dialog.edit_latitude
import kotlinx.android.synthetic.main.fragment_add_place_dialog.edit_longitude
import kotlinx.android.synthetic.main.fragment_card_marker.*
import java.text.SimpleDateFormat
import java.util.*

class CardMarkerFragment : DialogFragment() {

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_card_marker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar: Calendar = Calendar.getInstance()

        val bundle: Bundle? = arguments
        val latitude: Double = bundle?.getDouble("latitude")!!
        val longitude: Double = bundle.getDouble("longitude")

        if (latitude == 0.0 && longitude == 0.0){
            updateMarker()
        }else{
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
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->


            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            tvDate.text = sdf.format(calendar.time)

        }

        btnOk.setOnClickListener {

            addMarker()
        }
    }

    fun addMarker() {

        val bundle: Bundle? = arguments
        val latitude: Double = bundle?.getDouble("latitude")!!
        val longitude: Double = bundle.getDouble("longitude")

        edit_latitude.setText(latitude.toString())
        edit_longitude.setText(longitude.toString())

        if (isEmpty()) {
            Toast.makeText(context, "Empty field", Toast.LENGTH_LONG).show()
        } else {
            val idUser: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val idMarker: String = UUID.randomUUID().toString()
            val latitude: Double = edit_latitude.text.toString().toDouble()
            val longitude: Double = edit_longitude.text.toString().toDouble()
            val titleMarker: String = edit_title_marker.text.toString()
            val dateMarker: String = tvDate.text.toString()
            val dept: Double = edit_dept.text.toString().toDouble()
            val amountOfFish: Int = edit_number_of_fish.text.toString().toInt()
            val note: String = edit_note.text.toString()

            val newMarker = MarkerDetail(
                idUser,
                idMarker,
                latitude,
                longitude,
                titleMarker,
                dateMarker,
                dept,
                amountOfFish,
                note
            )
            FirebaseDatabase.getInstance().getReference("Marker").child(idMarker)
                .setValue(newMarker)
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
    fun updateMarker(){

        val bundle: Bundle? = arguments

        val uidUpdate = bundle?.getString("1")
        val idMarkerUpdate = bundle?.getString("2")
        val latitudeUpdate = bundle?.getString("3")
        val longitudeUpdate = bundle?.getString("4")
        val titleUpdate = bundle?.getString("5")
        val dateUpdate = bundle?.getString("6")
        val depthUpdate = bundle?.getString("7")
        val amountUpdate = bundle?.getString("8")
        val noteUpdate = bundle?.getString("9")

        edit_latitude.setText(latitudeUpdate)
        edit_longitude.setText(longitudeUpdate)
        edit_title_marker.setText(titleUpdate)
        tvDate.text = dateUpdate
        edit_dept.setText(depthUpdate)
        edit_number_of_fish.setText(amountUpdate)
        edit_note.setText(noteUpdate)







    }

    fun isEmpty(): Boolean {

        return TextUtils.isEmpty(edit_latitude.text) ||
                TextUtils.isEmpty(edit_longitude.text) ||
                TextUtils.isEmpty(edit_title_marker.text) ||
                TextUtils.isEmpty(tvDate.text) ||
                TextUtils.isEmpty(edit_dept.text) ||
                TextUtils.isEmpty(edit_number_of_fish.text) ||
                TextUtils.isEmpty(edit_note.text)
    }
}
