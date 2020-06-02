package com.company.imetlin.fishmarker.fragments_profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.company.imetlin.fishmarker.PlaceActivity
import com.company.imetlin.fishmarker.R
import com.company.imetlin.fishmarker.adapter.AdapterBigWater
import com.company.imetlin.fishmarker.modelclass.BigWater

class  BigWaterFragment : Fragment() {

    private var gridView: GridView? = null
    private var adapterGrid: AdapterBigWater? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_big_water, container, false)
        gridView = view.findViewById(R.id.gridView) as GridView
        gridView!!.isVerticalScrollBarEnabled = false
        adapterGrid = context?.let { AdapterBigWater(it) }
        gridView!!.adapter = adapterGrid

        gridView!!.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val bigWater = BigWater.values()[position]
                bigWaterName(bigWater.nameWater)
            }
                return view
            }
        fun bigWaterName(nameBigWater: String) {
            val intent = Intent(activity, PlaceActivity::class.java)
            intent.putExtra("nameBigWater", nameBigWater)
            startActivity(intent)
        }
    }
