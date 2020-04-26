package com.company.imetlin.fishmarker.fragments_place

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.modelclass.Place
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
        return  inflater.inflate(R.layout.fragment_add_place_dialog, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        btnOk.setOnClickListener {
            val name: String = view.edit_name?.text.toString().trim()
            if (name.isEmpty()){
                Toast.makeText(context, "name place is empty", Toast.LENGTH_SHORT).show()
            }else{
                infoPlace = Place(idPlace, uid, name, latitude, longitude, zoom, nameBigWater)
                addPlaceDatabase(infoPlace!!)
            }
        }
        latitude = this.arguments?.getDouble("latitude")
        longitude = this.arguments?.getDouble("longitude")
        zoom = this.arguments?.getFloat("zoom")
        nameBigWater = this.arguments?.getString("nameBigWater")
        edit_latitude.text = Editable.Factory.getInstance().newEditable(latitude.toString())
        edit_longitude.text = Editable.Factory.getInstance().newEditable(longitude.toString())
        edit_zoom.text = Editable.Factory.getInstance().newEditable(zoom.toString())
        edit_big_water.text = Editable.Factory.getInstance().newEditable(nameBigWater.toString())
    }


    fun addPlaceDatabase(infoplace: Place) {
        FirebaseDatabase.getInstance().getReference("Place").child(idPlace).setValue(infoplace)
        infoplace.let { AddPlaceFragment().addplace(it) }
        dialog?.dismiss()
        Toast.makeText(context, "add place, go back", Toast.LENGTH_SHORT).show()
    }
}