package com.company.fishmarker_kotlin.fragments_profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.PlaceActivity
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.adapter.AdapterPlaceBigWater

class ProfileFragment : Fragment() {


    private var gridView: GridView? = null
    private var adapterGrid: AdapterPlaceBigWater? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)


       // gridView  = view.findViewById(R.id.gridView) as GridView


        adapterGrid  = context?.let { AdapterPlaceBigWater(it) }

      //  gridView!!.adapter = adapterGrid

      //  gridView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->


           // val bigWater = BigWater.values()[position]
           // PlaceName(bigWater.nameWater)


     //   }
        return view

    }
    fun PlaceName (name : String){

        val intent = Intent (activity, PlaceActivity::class.java)
        intent.putExtra("nameWater", name)
        startActivity(intent)

    }
}