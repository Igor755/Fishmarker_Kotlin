package com.company.fishmarker_kotlin.fragments_place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.INVISIBLE
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.fishmarker_kotlin.MarkerActivity
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.adapter.AdapterPlace
import com.company.fishmarker_kotlin.helper_class.StaticHelper.Companion.allplace
import com.company.fishmarker_kotlin.modelclass.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_add_place.*


class AddPlaceFragment : Fragment() {



    private var mListRecyclerView: RecyclerView? = null
    private var txtnameplace: TextView? = null
    private var progressbar: ProgressBar? = null
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()



    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_add_place, container, false)
        mListRecyclerView = view.findViewById(R.id.my_recycler_view) as RecyclerView
        txtnameplace = view.findViewById(R.id.txtnameplace) as TextView
        progressbar = view.findViewById(R.id.progressbar) as ProgressBar
        return view


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_place, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.back_to_place_activity -> {
                activity?.finish()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent: Intent = activity!!.intent
        val nameBigWater: String = intent.getStringExtra("nameBigWater")

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef: Query = database.getReference("Place").orderByChild("uid").equalTo(mAuth.currentUser?.uid)
        allplace.clear()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                    val place: Place = dataSnapshot1.getValue(Place::class.java)!!
                    allplace.add(place)

                }
                setAdapter(nameBigWater)


            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Maybe not internet (Failed to read value) :(" , Toast.LENGTH_SHORT).show()
            }
        })


        fab!!.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_place, MapPlaceFragment())
                ?.addToBackStack(null)
                ?.commit()
        }


    }

    @SuppressLint("WrongConstant")
    fun setAdapter(name : String){


        val mAdapter = AdapterPlace(getListPlace(name))

        if(getListPlace(name) != emptyArray<Place>()){
            txtnameplace!!.visibility = INVISIBLE
            progressbar!!.visibility = INVISIBLE
        }
        mAdapter.setOnItemClickListener(object : AdapterPlace.ClickListener {
            override fun onClick(pos: Int, aView: View) {

                val place: Place = getListPlace(name)[pos]


                val intent = Intent(activity, MarkerActivity::class.java)
                intent.putExtra("latitude", place.latitude!!)
                intent.putExtra("longitude", place.longitude!!)
                intent.putExtra("zoom",  place.zoom!!)
                startActivity(intent)



            }
        })
        mListRecyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        mListRecyclerView!!.adapter = mAdapter


    }

    fun addplace(place: Place) {

        allplace.add(place)
        mListRecyclerView?.adapter?.notifyDataSetChanged()

    }
    fun getListPlace(nameBigWater : String) : ArrayList<Place>{

        val list : ArrayList<Place>  = ArrayList()

        //list.clear()

        when(nameBigWater){
            in "SEA" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("SEA")) {
                        list.add(place) }
                }

            in "OCEAN" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("OCEAN")) {
                        list.add(place) }
                }
            in "RIVER" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("RIVER")) {
                        list.add(place) }
                }
            in "LAKE" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("LAKE")) {
                        list.add(place) }
                }
            in "GULF" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("GULF")) {
                        list.add(place) }
                }
            in "RESERVOIR" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("RESERVOIR")) {
                        list.add(place) }
                }
            in "LADE" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("LADE")) {
                        list.add(place) }
                }
            in "POND" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("POND")) {
                        list.add(place) }
                }
            in "ANOTHER" ->
                for (place : Place in allplace) {
                    if (place.bigwater.equals("ANOTHER")) {
                        list.add(place) }
                }
            else -> {
                progressbar!!.visibility = INVISIBLE
                Toast.makeText(context, "not place", Toast.LENGTH_SHORT).show()
            }

        }
        return list




    }
}