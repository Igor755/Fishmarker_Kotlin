package com.company.fishmarker_kotlin.fragments_place

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.adapter.AdapterPlace
import com.company.fishmarker_kotlin.modelclass.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AddPlace : Fragment() {

    private var mListRecyclerView: RecyclerView? = null

    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.fragment_add_place, container, false)


        mListRecyclerView = view.findViewById(R.id.my_recycler_view) as RecyclerView


        //val placelist : ArrayList<Place>? = null

        val list: MutableList<Place> = ArrayList()

        val place  = Place("asas",0.0020,0.0002,0.45345)
        val fab : FloatingActionButton? = view.findViewById(R.id.fab)


        val range = 1..5

        for(i in range){

            place.name_place = "NamePlace"
            place.latitude = 0.00056546
            place.longitude = 0.00056546
            place.zoom = 0.434

            list.add(place)



        }

        var mAdapter = AdapterPlace(list)

        mListRecyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)


        mListRecyclerView!!.adapter = mAdapter

        fab!!.setOnClickListener{
            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_place, MapAddPlace())
                ?.addToBackStack(null)
                ?.commit()
        }


        return  view


    }
}