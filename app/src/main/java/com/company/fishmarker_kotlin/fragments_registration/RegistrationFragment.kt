package com.company.fishmarker_kotlin.fragments_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.company.fishmarker_kotlin.helper_class.StaticHelper
import com.company.fishmarker_kotlin.modelclass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_registration.*





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



    val adapter  = ArrayAdapter<String>(context,
        android.R.layout.simple_spinner_item,  StaticHelper.getValue())

    adapter.setDropDownViewResource(com.company.fishmarker_kotlin.R.layout.support_simple_spinner_dropdown_item)
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


        val position = spinner_location.selectedItemPosition


        val name: String = edit_text_name.text.toString().trim()
        val email: String = edit_text_email.text.toString().trim()
        val password: String = edit_text_password.text.toString().trim()
        val location: String? = StaticHelper.getValue()[position]

        if (name.isEmpty()) {
            edit_text_name.error = getText(com.company.fishmarker_kotlin.R.string.empty_name)
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

                    val userID : String = FirebaseAuth.getInstance().currentUser!!.uid
                    val user : User =  User(userID, name, email, location.toString())

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
                    ?.replace(com.company.fishmarker_kotlin.R.id.fragment_container_auth, SignInFragment())
                    ?.commit()

                Toast.makeText(context, com.company.fishmarker_kotlin.R.string.check, Toast.LENGTH_LONG).show()


            }else{

                Toast.makeText(context, "DONT SENT NO INTERNET", Toast.LENGTH_SHORT).show()

            }


        }

    }

}