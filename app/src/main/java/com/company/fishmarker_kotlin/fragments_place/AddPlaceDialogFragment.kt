package com.company.fishmarker_kotlin.fragments_place

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.company.fishmarker_kotlin.R
import kotlinx.android.synthetic.main.fragment_add_place_dialog.*

class AddPlaceDialogFragment : DialogFragment() {





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_add_place_dialog, container, false)

        val ok = view.findViewById<TextView>(R.id.ok)
        val cancel = view.findViewById<TextView>(R.id.cancel)


        cancel.setOnClickListener {
            dialog?.dismiss()
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val latitude = this.arguments?.getDouble("latitude")
        val longitude = this.arguments?.getDouble("longitude")
        
        edit_latitude.text = Editable.Factory.getInstance().newEditable("Latitude: " + latitude.toString())
        edit_longitude.text = Editable.Factory.getInstance().newEditable("Longitude: " + longitude.toString())
    }
}