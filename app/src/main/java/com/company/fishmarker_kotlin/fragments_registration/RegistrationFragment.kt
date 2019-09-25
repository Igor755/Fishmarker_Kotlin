package com.company.fishmarker_kotlin.fragments_registration

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.modelclass.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_registration.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("NAME_SHADOWING")
class RegistrationFragment : Fragment(){

    private lateinit var mAuth: FirebaseAuth


override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.fragment_registration, container, false)


    val edit_text_name = view.findViewById<EditText>(R.id.edit_text_name)
    val edit_text_email = view.findViewById<EditText>(R.id.edit_text_email)
    val edit_text_password = view.findViewById<EditText>(R.id.edit_text_password)
    val spinner_location = view.findViewById<Spinner>(R.id.spinner_location)
    val btnReg = view.findViewById<Button>(R.id.button_register)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)

    mAuth = FirebaseAuth.getInstance()


    progressbar!!.visibility = View.INVISIBLE



    val allCountry : ArrayList<String> = ArrayList()
    val coutryCodes : Array<out String>? = Locale.getISOCountries()
    val coutryCodes2 : String? = null


    if (coutryCodes != null) {
        for (coutryCodes2 in coutryCodes) {

            val locale: Locale = Locale("", coutryCodes2)
            val coutryName : String = locale.getDisplayCountry()
            allCountry.add(coutryName)

        }

    }
    allCountry.sort()

    val adapter  = ArrayAdapter<String>(context,
        android.R.layout.simple_spinner_item, allCountry)

    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
    spinner_location.adapter = adapter


    btnReg.setOnClickListener {
        newRegistrationUser()
    }

    return view

}
    fun isNameValid(name : String) : Boolean {

        val NAME_REGEX  = "^[_a-zA-Z0-9абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ ]+$".toRegex()
        return NAME_REGEX.containsMatchIn(name)

    }

    /////////////////т.е. разрешены все английские и русские буквы, цифры, символ нижнего подчеркивания и пробел. Всё остальное запрещено.

    fun isPasswordValid(password : String) : Boolean{

        val PASSWORD_REGEX  = "^[_a-zA-Z0-9абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ ]+$".toRegex()
        return PASSWORD_REGEX.containsMatchIn(password)

    }
    fun  isEmailValid(email: String) : Boolean{

        val EMAIL_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?".toRegex()
        return EMAIL_REGEX.containsMatchIn(email)
    }
    fun newRegistrationUser() {

        val countries: HashMap<String, String> = HashMap()
        val isoCountryCodes = Locale.getISOCountries()


        for (iso in isoCountryCodes) {

            val l: Locale = Locale("", iso)
            countries[l.displayCountry] = iso
        }

        val name: String = edit_text_name.text.toString().trim()
        val email: String = edit_text_email.text.toString().trim()
        val password: String = edit_text_password.text.toString().trim()
        val location: String? = countries[spinner_location.selectedItem.toString().trim()]

        if (name.isEmpty()) {
            edit_text_name.error = getText(R.string.empty_name)
            edit_text_name.requestFocus()
            return
        }
        if (email.isEmpty()) {
            edit_text_email.error = getText(R.string.empty_email)
            edit_text_email.requestFocus()
            return
        }
        if (password.isEmpty()) {
            edit_text_password.error = getText(R.string.password_empty)
            edit_text_password.requestFocus()
            return
        }
        if (!isNameValid(edit_text_name.text.toString())) {
            edit_text_name.error = getText(R.string.not_valid_name)
            edit_text_name.requestFocus()
            return
        }
        if (!isEmailValid(edit_text_email.text.toString())) {
            edit_text_email.error = getText(R.string.not_valid_email)
            edit_text_email.requestFocus()
            return
        }
        if (!isPasswordValid(edit_text_password.text.toString())) {
            edit_text_password.error = getText(R.string.requirements)
            edit_text_password.requestFocus()
            return
        }

        progressbar!!.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){

                    val user : User =  User(name, email, location.toString())
                    val userID : String = FirebaseAuth.getInstance().currentUser!!.uid

                    FirebaseDatabase.getInstance().getReference("Users").child(userID).setValue(user).addOnCompleteListener { task ->
                        progressbar!!.visibility = View.INVISIBLE

                        if (task.isSuccessful){
                            sendVerificationEmail()
                        }else{
                            Toast.makeText(context, R.string.valid, Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                    progressbar!!.visibility = View.INVISIBLE
                }


            }



    }
    fun sendVerificationEmail(){

        var user : FirebaseUser? = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()?.addOnCompleteListener {task ->
            if (task.isSuccessful){

                FirebaseAuth.getInstance().signOut()

                fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container_auth, SignInFragment())
                    ?.addToBackStack(null)
                    ?.commit()

                Toast.makeText(context, R.string.check, Toast.LENGTH_LONG).show()


            }else{

                Toast.makeText(context, "DONT SENT NO INTERNET", Toast.LENGTH_SHORT).show()

            }


        }

    }

}