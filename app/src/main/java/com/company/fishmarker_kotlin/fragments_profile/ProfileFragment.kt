package com.company.fishmarker_kotlin.fragments_profile

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.R.color
import com.company.fishmarker_kotlin.helper_class.StaticHelper
import com.company.fishmarker_kotlin.fragments_registration.RegistrationFragment
import com.company.fishmarker_kotlin.modelclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val spinner_location = view.findViewById<Spinner>(R.id.spinner_location)





        val adapter  = ArrayAdapter<String>(context,
            android.R.layout.simple_spinner_item, StaticHelper.getValue())

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_location.adapter = adapter
        spinner_location.isEnabled = false
        spinner_location.isClickable = false



        //val position : Int = adapter.getPosition("Afghanistan")
       // spinner_location.setSelection(position)
        /*spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Change the selected item's text color
                ((TextView) view).setTextColor(backgroundColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });*/




        var user = User()
        val myRef : Query = firebaseDatabase.getReference("Users").orderByKey().equalTo(mAuth.currentUser?.uid)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataSnapshot1 : DataSnapshot in dataSnapshot.children) {

                    user = dataSnapshot1.getValue(User::class.java)!!
                    println(user)

                }

                val position : Int = adapter.getPosition(user.location)



                view?.edit_name?.setText(user.user_name)
                view?.edit_last_name?.setText(user.last_name)
                view?.spinner_location?.setSelection(position)
                view?.edit_email?.setText(user.email)
                view?.edit_telephone?.setText(user.telephone)
                view?.edit_trophies?.setText(user.trophies)
                view?.edit_preferred_type_of_fishing?.setText(user.type_of_fishing)
                //view?.edit_about_me?.setText(user.about_me)

                }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })



        /*change_account.setOnClickListener {

        *//*    edit_name.isClickable
            edit_last_name.isClickable
                //edit_location.
            edit_email
            edit_telephone
            edit_trophies
            edit_preferred_type_of_fishing*//*

        }*/


        return view
    }


}