package com.company.imetlin.fishmarker.fragments_profile

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.*
import com.company.imetlin.fishmarker.R
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.company.imetlin.fishmarker.helper_class.StaticHelper
import com.company.imetlin.fishmarker.helper_class.StaticHelper.Companion.filepathimage
import com.company.imetlin.fishmarker.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.lang.IllegalArgumentException
import java.util.UUID.randomUUID

class ProfileFragment : Fragment() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var user = User()

    lateinit var pref: SharedPreferences
    lateinit var adapter: ArrayAdapter<String>
    val uid: String = mAuth.currentUser?.uid.toString()
    private val PICK_IMAGE_REQUEST: Int = 1
    lateinit var filePath: Uri
    lateinit var firebaseStorage: StorageReference



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        cancel.isVisible = false
        spinner_location.isEnabled = false
        spinner_location.isClickable = false
        save.isVisible = false
        cancelBtn.isVisible = false
        okBtn.isVisible = false
        loader.isVisible = false


        firebaseStorage = FirebaseStorage.getInstance().getReference("images")
        pref = PreferenceManager.getDefaultSharedPreferences(context)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, StaticHelper.getValue())
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_location.adapter = adapter


        if (!StaticHelper.isNetworkAvailable(requireContext())) {
            localLoadUser()
        } else {
            loadFirebaseUser()
        }

        change_photo_button.setOnClickListener {
            chooseImage()
        }

        cancelBtn.setOnClickListener {
            diactivateView()
        }
        okBtn.setOnClickListener {
            savePictureCloudFirebaseAndDatabase()
        }

        change_account.setOnClickListener {
            change_photo_button.isVisible = false
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

    }

    fun activateView() {

        cancel.isVisible = true
        change_account.isVisible = false
        save.isVisible = true

        ////////клик по view
        edit_name.isClickable = true
        edit_last_name.isClickable = true
        spinner_location.isEnabled = true
        //edit_email.isClickable = true
        edit_telephone.isClickable = true
        edit_trophies.isClickable = true
        edit_preferred_type_of_fishing.isClickable = true

        ////////visible cursor view
        edit_name.isCursorVisible = true
        edit_last_name.isCursorVisible = true
        // edit_email.isCursorVisible = true
        edit_telephone.isCursorVisible = true
        edit_trophies.isCursorVisible = true
        edit_preferred_type_of_fishing.isCursorVisible = true

        ////////visible focus view
        edit_name.isFocusable = true
        edit_name.isFocusableInTouchMode = true
        edit_last_name.isFocusable = true
        edit_last_name.isFocusableInTouchMode = true
        edit_telephone.isFocusable = true
        edit_telephone.isFocusableInTouchMode = true
        edit_trophies.isFocusable = true
        edit_trophies.isFocusableInTouchMode = true
        edit_preferred_type_of_fishing.isFocusable = true
        edit_preferred_type_of_fishing.isFocusableInTouchMode = true

    }

    fun diactivateView() {

        loader.isVisible = false
        cancel.isVisible = false
        change_account.isVisible = true
        save.isVisible = false
        cancelBtn.isVisible = false
        okBtn.isVisible = false
        change_photo_button.isVisible = true


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

    fun localSaveUser(userlocal: User) {
        val editor = pref.edit()
        editor.clear()
        editor.putString("user_name", userlocal.user_name)
        editor.putString("user_last_name", userlocal.last_name)
        editor.putString("user_email", userlocal.email)
        editor.putString("user_telephone", userlocal.telephone)
        editor.putString("user_trophies", userlocal.trophies)
        editor.putString("user_prefered", userlocal.type_of_fishing)
        editor.putString("user_location", userlocal.location)
        editor.putString("user_photo", userlocal.url_photo)
        editor.apply()
    }

    fun localLoadUser() {

        val user_name: String? = pref.getString("user_name", "")
        val user_last_name: String? = pref.getString("user_last_name", "")
        val user_email: String? = pref.getString("user_email", "")
        val user_telephone: String? = pref.getString("user_telephone", "")
        val user_trophies: String? = pref.getString("user_trophies", "")
        val user_preferred: String? = pref.getString("user_prefered", "")
        val user_location: String? = pref.getString("user_location", "")
        val user_photo: String? = pref.getString("user_photo", "")
        val position: Int = adapter.getPosition(user_location)

        spinner_location.setSelection(position)
        edit_name.setText(user_name)
        edit_last_name.setText(user_last_name)
        edit_email.setText(user_email)
        edit_telephone.setText(user_telephone)
        edit_trophies.setText(user_trophies)
        edit_preferred_type_of_fishing.setText(user_preferred)

        if (user_email == "") {
            Toast.makeText(context, "local data empty too, enable internet", Toast.LENGTH_SHORT)
                .show()
        }
        try {
            if (user_photo == "") {
                photo_user_image_view.setImageResource(R.drawable.photo_user)
            } else {
                val myUri: Uri = Uri.parse(user_photo)
                Picasso.get().load(myUri).into(photo_user_image_view)
            }
        } catch (e: IllegalArgumentException) {
            photo_user_image_view.setImageResource(R.drawable.photo_user)
            Toast.makeText(context, "not internet, not photo", Toast.LENGTH_SHORT).show()
        }


    }

    fun loadFirebaseUser() {

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

                if (user.url_photo == "") {
                    photo_user_image_view.setImageResource(R.drawable.photo_user)
                } else {
                    val myUri: Uri = Uri.parse(user.url_photo)
                    Picasso.get().load(myUri).into(photo_user_image_view)
                    filepathimage = user.url_photo
                }
                localSaveUser(user)
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun changeUserInFirebase() {

        val name: String = view?.edit_name?.text.toString()
        val last_name: String = view?.edit_last_name?.text.toString()
        val location: String = view?.spinner_location?.selectedItem as String
        val email: String = view?.edit_email?.text.toString()
        val telephone: String = view?.edit_telephone?.text.toString()
        val trophies: String = view?.edit_trophies?.text.toString()
        val type_fishing: String = view?.edit_preferred_type_of_fishing?.text.toString()

        println(filepathimage)

        if (filepathimage == "")
        {
            photo_user_image_view.setImageResource(R.drawable.photo_user)
        }
        else
        {
            val myUri: Uri = Uri.parse(user.url_photo)
            Picasso.get().load(myUri).into(photo_user_image_view)
        }

        val user_update = User(uid, name, last_name, email, location, telephone, type_fishing, trophies, filepathimage)
        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(user_update)
        localSaveUser(user_update)

    }


    fun chooseImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
        cancelBtn.isVisible = true
        okBtn.isVisible = true
        change_photo_button.isVisible = false
        change_photo_button.isVisible = false

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            filePath = data.data!!
            photo_user_image_view.setImageDrawable(null)
            Picasso.get().load(filePath).fit().into(photo_user_image_view)
            photo_user_image_view.setImageURI(filePath)


        }
    }

    fun getFileExtension(uri: Uri): String {

        val contentResolverUri: ContentResolver = requireContext().contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolverUri.getType(uri)).toString()

    }

    fun savePictureCloudFirebaseAndDatabase() {



            loader.isVisible = true


            val storageReference: StorageReference =
                firebaseStorage.child(randomUUID().toString() + "." + getFileExtension(filePath))
            val upload = storageReference.putFile(filePath)

            val urlTask =
                upload.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation storageReference.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        addUploadRecordToDb(downloadUri.toString())
                    } else {
                        // Handle failures
                    }
                }.addOnFailureListener {

                }

    }
    fun addUploadRecordToDb(uri: String) {

        val user_update_photo = User(user.user_id, user.user_name, user.last_name, user.email, user.location, user.telephone, user.type_of_fishing, user.trophies, uri)
        val myUri: Uri = Uri.parse(uri)
        Picasso.get().load(myUri).into(photo_user_image_view)
        loader.isVisible = false
       // deleteInFirebaseStorageImage()
        filepathimage = uri
        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(user_update_photo)
        localSaveUser(user_update_photo)
        diactivateView()
        Toast.makeText(context, " AHHAHAHHA EASY", Toast.LENGTH_SHORT).show()

    }

    fun deleteInFirebaseStorageImage() {

        if (user.url_photo != "") {

            val imagereference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.url_photo)
            imagereference.delete().addOnSuccessListener {
                Log.d("storage", "complete")
            }.addOnFailureListener {
                Log.d("storage", "error")
            }
        } else {
            Log.d("storage", "empty")


        }


    }

}

