package com.company.imetlin.fishmarker.fragments_registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.company.imetlin.fishmarker.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_reset_password.*
import kotlinx.android.synthetic.main.fragment_reset_password.tiEtLoginEmail

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password), TextWatcher {

    private var firebaseAuth: FirebaseAuth? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        firebaseAuth = FirebaseAuth.getInstance()
        tiEtLoginEmail.addTextChangedListener(this)
    }

    fun initListeners(){
        btnSendOnEmail.setOnClickListener {
            btnSendOnEmail.isLoading = true
            val email = tiEtLoginEmail.text.toString().trim()
            if (email.isEmpty() && (!RegistrationFragment().isEmailValid(email))) {
                tiEtLoginEmail.error = getText(R.string.not_valid_email)
                tiEtLoginEmail.requestFocus()
            } else {
                firebaseAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        btnSendOnEmail.isLoading = false
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
        btn_cancel.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_container_auth, SignInFragment())
                ?.commit()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        validateFields()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun validateFields() {
        btnSendOnEmail.isEnabled = listOf(
            (tiEtLoginEmail.text!!.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(tiEtLoginEmail.text.toString()).matches()),
        ).all { it }
    }
}
