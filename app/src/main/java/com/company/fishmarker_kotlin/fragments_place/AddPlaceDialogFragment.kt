package com.company.fishmarker_kotlin.fragments_place

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_place_dialog.*
import kotlinx.android.synthetic.main.fragment_add_place_dialog.view.*
import java.util.*

class AddPlaceDialogFragment : DialogFragment() {

    val idPlace = UUID.randomUUID().toString()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var infoPlace: Place? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var zoom: Float? = null
    var nameBigWater: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_place_dialog, container, false)

        val ok = view.findViewById<TextView>(R.id.ok)
        val cancel = view.findViewById<TextView>(R.id.cancel)


        cancel.setOnClickListener {
            dialog?.dismiss()
        }
        ok.setOnClickListener {

            val name: String = view?.edit_name?.text.toString().trim()
            infoPlace = Place(idPlace, uid, name, latitude, longitude, zoom, nameBigWater)
            addPlaceDatabase(infoPlace!!)

        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latitude = this.arguments?.getDouble("latitude")
        longitude = this.arguments?.getDouble("longitude")
        zoom = this.arguments?.getFloat("zoom")
        nameBigWater = this.arguments?.getString("nameBigWater")

        edit_latitude.text = Editable.Factory.getInstance().newEditable("Latitude: " + latitude.toString())
        edit_longitude.text = Editable.Factory.getInstance().newEditable("Longitude: " + longitude.toString())
        edit_zoom.text = Editable.Factory.getInstance().newEditable("Zoom: " + zoom.toString())
        edit_big_water.text = Editable.Factory.getInstance().newEditable("Big water: " + nameBigWater.toString())


    }


    fun addPlaceDatabase(infoplace: Place) {

        FirebaseDatabase.getInstance().getReference("Place").child(idPlace).setValue(infoplace)
        infoplace.let { AddPlaceFragment().addplace(it) }
        dialog?.dismiss()

        Toast.makeText(context, "add place, go back", Toast.LENGTH_SHORT).show()
    }
}