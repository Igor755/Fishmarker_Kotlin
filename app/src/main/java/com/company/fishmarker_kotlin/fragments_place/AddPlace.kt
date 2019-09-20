package com.company.fishmarker_kotlin.fragments_place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.adapter.AdapterPlace
import com.company.fishmarker_kotlin.modelclass.Place


class AddPlace : Fragment() {

    private var mListRecyclerView: RecyclerView? = null
    var mAdapter: AdapterPlace? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_add_place, container, false)


        mListRecyclerView = view.findViewById(R.id.my_recycler_view) as RecyclerView


        var placelist : ArrayList<Place>? = null
        var place : Place? = null


        val range = 1..5

        for(i in range){

            place?.name_place = "NamePlace"
            place?.latitude = 0.00056546
            place?.longitude = 0.00056546
            place?.zoom = 0.434
           // placelist += place

        }

        //placelist.add()

        mListRecyclerView!!.adapter = mAdapter


        return  view


    }
}