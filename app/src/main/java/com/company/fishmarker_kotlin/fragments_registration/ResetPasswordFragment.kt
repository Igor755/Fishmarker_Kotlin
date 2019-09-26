package com.company.fishmarker_kotlin.fragments_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_reset_password, container, false)

        val et_email = view.findViewById<EditText>(com.company.fishmarker_kotlin.R.id.et_email)
        val btn_send_pass = view.findViewById<Button>(com.company.fishmarker_kotlin.R.id.btn_send_pass)
        val progressbar = view.findViewById<ProgressBar>(com.company.fishmarker_kotlin.R.id.progressbar)

        progressbar!!.visibility = View.INVISIBLE


        val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

        btn_send_pass.setOnClickListener {
            progressbar.visibility = View.VISIBLE

            val email : String = et_email.text.toString().trim()

            if (email.isEmpty()){
                progressbar.visibility = View.INVISIBLE
                et_email.error = getText(R.string.empty_email)
                et_email.requestFocus()

            }
            if (!RegistrationFragment().isEmailValid(email)){
                progressbar.visibility = View.INVISIBLE
                et_email.error = getText(R.string.not_valid_email)
                et_email.requestFocus()

            }

            firebaseAuth.sendPasswordResetEmail(et_email.text.toString()).addOnCompleteListener {task ->
                progressbar.visibility = View.INVISIBLE
                if (task.isSuccessful){
                    Toast.makeText(context, R.string.sent, Toast.LENGTH_SHORT).show()

                    fragmentManager
                        ?.beginTransaction()
                        ?.replace(com.company.fishmarker_kotlin.R.id.fragment_container_auth, SignInFragment())
                        ?.commit()
                }else{
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }

            }

        }

        return view
    }
}
