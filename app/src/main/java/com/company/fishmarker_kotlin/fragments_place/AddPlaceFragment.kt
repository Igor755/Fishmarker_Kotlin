package com.company.fishmarker_kotlin.fragments_place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.adapter.AdapterPlace
import com.company.fishmarker_kotlin.fragments_marker.MapMarkerFragment
import com.company.fishmarker_kotlin.modelclass.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AddPlaceFragment : Fragment() {

    private var mListRecyclerView: RecyclerView? = null
    private var txtnameplace : TextView? = null
    private var progressbar : ProgressBar? = null

    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_add_place, container, false)

        mListRecyclerView = view.findViewById(R.id.my_recycler_view) as RecyclerView
        txtnameplace = view.findViewById(R.id.txtnameplace) as TextView
        progressbar = view.findViewById(R.id.progressbar) as ProgressBar
        val fab : FloatingActionButton? = view.findViewById(R.id.fab)
        //name big water (BigWaterFragment)
        val intent : Intent = activity!!.intent
        val nameBigWater : String = intent.getStringExtra("nameBigWater")



        val list: MutableList<Place> = ArrayList()
        val mAdapter = AdapterPlace(list)
        mListRecyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        mListRecyclerView!!.adapter = mAdapter



       /* val place  = Place("asas",0.0020,0.0002,0.45345)
        val range = 1..5
        for(i in range){
            place.name_place = "NamePlace"
            place.latitude = 0.00056546
            place.longitude = 0.00056546
            place.zoom = 0.434
            list.add(place)
        }



        if (list.size != 0){
            txtnameplace!!.visibility = INVISIBLE
            progressbar!!.visibility = INVISIBLE
        } else{
            txtnameplace!!.visibility = VISIBLE
            progressbar!!.visibility = VISIBLE

        }*/




        mAdapter.setOnItemClickListener(object : AdapterPlace.ClickListener {
            override fun onClick(pos: Int, aView: View) {

                fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container_place, MapMarkerFragment())
                    ?.addToBackStack(null)
                    ?.commit()

            }
        })

        fab!!.setOnClickListener{
            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_place, MapPlaceFragment())
                ?.addToBackStack(null)
                ?.commit()
        }


        return  view


    }
}