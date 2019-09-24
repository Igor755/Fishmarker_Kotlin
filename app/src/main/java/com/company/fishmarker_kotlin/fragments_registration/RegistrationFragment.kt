package com.company.fishmarker_kotlin.fragments_registration

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class RegistrationFragment : Fragment(){

override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.fragment_registration, container, false)


    val edit_text_name = view.findViewById<EditText>(R.id.edit_text_name)
    val edit_text_email = view.findViewById<EditText>(R.id.edit_text_email)
    val edit_text_password = view.findViewById<EditText>(R.id.edit_text_password)
    val spinner_location = view.findViewById<Spinner>(R.id.spinner_location)
    val btnReg = view.findViewById<Button>(R.id.btn_registration)
    val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)

    val allCountry : ArrayList<String>? = null
    val coutryCodes : Array<out String>? = Locale.getISOCountries()
    val coutryCodes2 : String? = null


    if (coutryCodes != null) {
        for (coutryCodes2 in coutryCodes) {

            val locale: Locale = Locale("", coutryCodes2)
            val coutryName : String = locale.getDisplayCountry()
            allCountry?.add(coutryName)

        }

    }
    allCountry?.sort()

    return view

}
    fun isNameValid(name : String) : Boolean {

        val NAME_REGEX : Pattern = Pattern.compile("^[_a-zA-Z0-9абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ ]+$",
            Pattern.CASE_INSENSITIVE)

        return NAME_REGEX.matcher(name).matches()

    }
    fun isPasswordValid(password : String) : Boolean{

        val PASSWORD_REGEX : Pattern = Pattern.compile("^[_a-zA-Z0-9абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ ]+$",
            Pattern.CASE_INSENSITIVE)

        return PASSWORD_REGEX.matcher(password).matches()

    }
    fun isEmailValid(email: String) : Boolean{

        val EMAIL_REGEEX : Pattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            Pattern.CASE_INSENSITIVE)

        return  EMAIL_REGEEX.matcher(email).matches()
    }

}