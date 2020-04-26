package com.company.imetlin.fishmarker.fragments_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.company.imetlin.fishmarker.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_reset_password.*

class ResetPasswordFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressbar!!.visibility = View.INVISIBLE
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        btn_send_pass.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            val email = et_email.text.toString().trim()
            if (email.isEmpty() && (!RegistrationFragment().isEmailValid(email))) {
                progressbar.visibility = View.INVISIBLE
                et_email.error = getText(R.string.not_valid_email)
                et_email.requestFocus()
            } else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        Toast.makeText(context, R.string.sent, Toast.LENGTH_SHORT).show()
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.fragment_container_auth, SignInFragment())
                            ?.commit()
                    } else {
                        Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
