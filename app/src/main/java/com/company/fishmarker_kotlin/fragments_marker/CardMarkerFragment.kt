package com.company.fishmarker_kotlin.fragments_marker

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.MarkerActivity
import com.company.fishmarker_kotlin.PlaceActivity
import com.company.fishmarker_kotlin.R
import kotlinx.android.synthetic.main.fragment_add_place_dialog.*
import kotlinx.android.synthetic.main.fragment_add_place_dialog.edit_latitude
import kotlinx.android.synthetic.main.fragment_add_place_dialog.edit_longitude
import kotlinx.android.synthetic.main.fragment_card_marker.*
import java.text.SimpleDateFormat
import java.util.*

class CardMarkerFragment : Fragment(){

    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_card_marker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar : Calendar = Calendar.getInstance()

     /*   edit_latitude
        edit_longitude
        edit_title_marker
        tvDate
        edit_dept
        edit_number_of_fish
        edit_note
        btnOk
        btnCancel*/


        val bundle : Bundle? = arguments
        val latitude : Double  = bundle?.getDouble("latitude")!!
        val longitude : Double  = bundle.getDouble("longitude")



        edit_latitude.setText(latitude.toString())
        edit_longitude.setText(longitude.toString())

        edit_latitude.isEnabled = false
        edit_longitude.isEnabled = false


        btnCancel.setOnClickListener {

            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_marker, MapMarkerFragment())
                ?.commit()

        }

        tvDate.setOnClickListener {

            val day  = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val datePickerDialog : DatePickerDialog = DatePickerDialog(context, R.style.ThemeOverlay_AppCompat_Dialog,dateSetListener,year,month,day)
            datePickerDialog.show()

        }
        dateSetListener = DatePickerDialog.OnDateSetListener{ view, year, month, day ->


            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            tvDate.text = sdf.format(calendar.time)

        }


    }
    fun addMarker(){

    }
    fun isEmpty() : Boolean{

        return TextUtils.isEmpty(edit_latitude.text) ||
                TextUtils.isEmpty(edit_longitude.text) ||
                TextUtils.isEmpty(edit_title_marker.text) ||
                TextUtils.isEmpty(tvDate.text) ||
                TextUtils.isEmpty(edit_dept.text) ||
                TextUtils.isEmpty(edit_number_of_fish.text) ||
                TextUtils.isEmpty(edit_note.text)
    }
}
