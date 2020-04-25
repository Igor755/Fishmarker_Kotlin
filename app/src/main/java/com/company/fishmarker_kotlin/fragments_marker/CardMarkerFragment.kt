package com.company.fishmarker_kotlin.fragments_marker

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
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
import kotlinx.android.synthetic.main.fragment_add_marker_dialog.*
import kotlinx.android.synthetic.main.fragment_add_marker_map.*
import java.text.SimpleDateFormat
import java.util.*

class CardMarkerFragment : DialogFragment() {

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_marker_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar: Calendar = Calendar.getInstance()
        val bundle: Bundle? = arguments
        val latitude: Double = bundle?.getDouble("latitude")!!
        val longitude: Double = bundle.getDouble("longitude")
        btnDelete.visibility = View.INVISIBLE


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
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->


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


    }

    fun updateMarker() {

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


        btnDelete.visibility = View.VISIBLE

        btnDelete.setOnClickListener {

            deleteMarker(
                uidUpdate,
                idMarkerUpdate,
                latitudeUpdate,
                longitudeUpdate,
                titleUpdate,
                dateUpdate,
                depthUpdate,
                amountUpdate,
                noteUpdate
            )

        }


        btnOk.setOnClickListener {

            if (isEmpty()) {

                Toast.makeText(context, R.string.fill, Toast.LENGTH_LONG).show()

            } else {

                val latitude: String = edit_latitude.text.toString()
                val longitude: String = edit_longitude.text.toString()
                val title: String = edit_title_marker.text.toString()
                val displayDate: String = tvDate.text.toString()
                val depth: String = edit_dept.text.toString()
                val amountoffish: String = edit_number_of_fish.text.toString()
                val note: String = edit_note.text.toString()

                val markerInformation = MarkerDetail(
                    uidUpdate,
                    idMarkerUpdate,
                    java.lang.Double.valueOf(latitude),
                    java.lang.Double.valueOf(longitude),
                    title,
                    displayDate,
                    java.lang.Double.valueOf(depth), amountoffish.toInt(),
                    note
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

    fun deleteMarker(
        uidUpdate: String?,
        idMarkerUpdate: String?,
        latitudeUpdate: String?,
        longitudeUpdate: String?,
        titleUpdate: String?,
        dateUpdate: String?,
        depthUpdate: String?,
        amountUpdate: String?,
        noteUpdate: String?
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
                depthUpdate!!.toDouble(),
                Integer.parseInt(amountUpdate),
                noteUpdate.toString()
            );

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

        return TextUtils.isEmpty(edit_latitude.text) ||
                TextUtils.isEmpty(edit_longitude.text) ||
                TextUtils.isEmpty(edit_title_marker.text) ||
                TextUtils.isEmpty(tvDate.text) ||
                TextUtils.isEmpty(edit_dept.text) ||
                TextUtils.isEmpty(edit_number_of_fish.text) ||
                TextUtils.isEmpty(edit_note.text)
    }
}
