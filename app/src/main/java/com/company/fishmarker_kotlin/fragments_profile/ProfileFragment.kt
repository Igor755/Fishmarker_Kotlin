package com.company.fishmarker_kotlin.fragments_profile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.helper_class.StaticHelper
import com.company.fishmarker_kotlin.modelclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var user = User()

    lateinit var pref: SharedPreferences
    lateinit var adapter: ArrayAdapter<String>
    val uid : String = mAuth.currentUser?.uid.toString()
    private val PICK_IMAGE_REQUEST : Int  = 1
    lateinit var filePath : Uri






    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val spinnerLocation = view.findViewById<Spinner>(R.id.spinner_location)
        val changeAccount = view.findViewById<Button>(R.id.change_account)
        val cancel = view.findViewById<Button>(R.id.cancel)
        val save = view.findViewById<Button>(R.id.save)
        val changePhoto = view.findViewById<Button>(R.id.change_photo)
        val photo_user = view.findViewById<ImageView>(R.id.photo_user)

        cancel.isVisible = false
        spinnerLocation.isEnabled = false
        spinnerLocation.isClickable = false
        save.isVisible = false


        pref = context!!.getSharedPreferences("USER",MODE_PRIVATE)
        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, StaticHelper.getValue())
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinnerLocation.adapter = adapter

        loadFirebaseUser()


        changePhoto.setOnClickListener{

            chooseImage()
        }


        changeAccount.setOnClickListener {
            activateView()
        }

        cancel.setOnClickListener {
            diactivateView()
        }
        save.setOnClickListener {

            changeUserInFirebase()
            loadFirebaseUser()
            diactivateView()
        }

        /*val result :  Boolean = isNetworkAvailable(context!!)

        if (!result){

            localLoadUser()

        }*/


        return view
    }

    fun activateView(){

        cancel.isVisible = true
        change_account.isVisible = false
        save.isVisible = true

        ////////клик по view
        edit_name.isClickable = true
        edit_last_name.isClickable = true
        spinner_location.isEnabled = true
        edit_email.isClickable = true
        edit_telephone.isClickable = true
        edit_trophies.isClickable = true
        edit_preferred_type_of_fishing.isClickable = true

        ////////visible cursor view
        edit_name.isCursorVisible = true
        edit_last_name.isCursorVisible = true
        edit_email.isCursorVisible = true
        edit_telephone.isCursorVisible = true
        edit_trophies.isCursorVisible = true
        edit_preferred_type_of_fishing.isCursorVisible = true

        ////////visible focus view
        edit_name.isFocusable = true
        edit_name.isFocusableInTouchMode = true
        edit_last_name.isFocusable = true
        edit_last_name.isFocusableInTouchMode = true
        edit_email.isFocusable = true
        edit_email.isFocusableInTouchMode = true
        edit_telephone.isFocusable = true
        edit_telephone.isFocusableInTouchMode = true
        edit_trophies.isFocusable = true
        edit_trophies.isFocusableInTouchMode = true
        edit_preferred_type_of_fishing.isFocusable = true
        edit_preferred_type_of_fishing.isFocusableInTouchMode = true

    }
    fun diactivateView() {

        cancel.isVisible = false
        change_account.isVisible = true
        save.isVisible = false


        ////////клик по view
        edit_name.isClickable = false
        edit_last_name.isClickable = false
        spinner_location.isEnabled = false
        edit_email.isClickable = false
        edit_telephone.isClickable = false
        edit_trophies.isClickable = false
        edit_preferred_type_of_fishing.isClickable = false

        ////////visible cursor view
        edit_name.isCursorVisible = false
        edit_last_name.isCursorVisible = false
        edit_email.isCursorVisible = false
        edit_telephone.isCursorVisible = false
        edit_trophies.isCursorVisible = false
        edit_preferred_type_of_fishing.isCursorVisible = false

        ////////visible focus view
        edit_name.isFocusable = false
        edit_name.isFocusableInTouchMode = false
        edit_last_name.isFocusable = false
        edit_last_name.isFocusableInTouchMode = false
        edit_email.isFocusable = false
        edit_email.isFocusableInTouchMode = false
        edit_telephone.isFocusable = false
        edit_telephone.isFocusableInTouchMode = false
        edit_trophies.isFocusable = false
        edit_trophies.isFocusableInTouchMode = false
        edit_preferred_type_of_fishing.isFocusable = false
        edit_preferred_type_of_fishing.isFocusableInTouchMode = false


        localLoadUser()

    }

    fun localSaveUser(userlocal : User){

        val editor  = pref.edit()
        editor.putString("user_name", userlocal.user_name)
        editor.putString("user_last_name", userlocal.last_name)
        editor.putString("user_email", userlocal.email)
        editor.putString("user_telephone", userlocal.telephone)
        editor.putString("user_trophies", userlocal.trophies)
        editor.putString("user_prefered", userlocal.type_of_fishing)
        editor.putString("user_location", userlocal.location)
        editor.apply()

    }
    fun localLoadUser(){

        val user_location : String = pref.getString("user_location", "")
        val user_name : String = pref.getString("user_name", "")
        val user_last_name : String = pref.getString("user_last_name", "")
        val user_email : String = pref.getString("user_email", "")
        val user_telephone: String = pref.getString("user_telephone", "")
        val user_trophies : String = pref.getString("user_trophies", "")
        val user_preferred : String = pref.getString("user_prefered", "")
        val position: Int = adapter.getPosition(user_location)

        view?.spinner_location?.setSelection(position)
        view?.edit_name?.setText(user_name)
        view?.edit_last_name?.setText(user_last_name)
        view?.edit_email?.setText(user_email)
        view?.edit_telephone?.setText(user_telephone)
        view?.edit_trophies?.setText(user_trophies)
        view?.edit_preferred_type_of_fishing?.setText(user_preferred)
    }

    fun loadFirebaseUser(){

        val myRef: Query = firebaseDatabase.getReference("Users").orderByKey().equalTo(uid)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                    user = dataSnapshot1.getValue(User::class.java)!!
                    println(user)

                }

                val position: Int = adapter.getPosition(user.location)


                view?.edit_name?.setText(user.user_name)
                view?.edit_last_name?.setText(user.last_name)
                view?.spinner_location?.setSelection(position)
                view?.edit_email?.setText(user.email)
                view?.edit_telephone?.setText(user.telephone)
                view?.edit_trophies?.setText(user.trophies)
                view?.edit_preferred_type_of_fishing?.setText(user.type_of_fishing)
                //view?.edit_about_me?.setText(user.about_me)

                localSaveUser(user)
                //localLoadUser()

            }

            override fun onCancelled(error: DatabaseError) {


                // Failed to read value
            }
        })
    }
    fun changeUserInFirebase(){


        val name : String = view?.edit_name?.text.toString()
        val last_name : String = view?.edit_last_name?.text.toString()
        var location : String = view?.spinner_location?.selectedItem as String
        var email : String = view?.edit_email?.text.toString()
        var telephone : String = view?.edit_telephone?.text.toString()
        var trophies : String = view?.edit_trophies?.text.toString()
        var type_fishing : String = view?.edit_preferred_type_of_fishing?.text.toString()

        var user_update : User = User(user.user_id, name, last_name,email , location, telephone,type_fishing , trophies, "null",user.url_photo)

        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(user_update)


    }

    override fun onResume() {
        super.onResume()

        localLoadUser()

    }

    fun chooseImage(){

        val intent : Intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, " Select Picture"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && requestCode == RESULT_OK
            && data != null && data.data != null){

            filePath = data.data
            Picasso.get().load(filePath).into(photo_user)
            photo_user.setImageURI(filePath)

        }
    }
    }
