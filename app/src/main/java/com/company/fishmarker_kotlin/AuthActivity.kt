package com.company.fishmarker_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.company.fishmarker_kotlin.fragments_registration.SignInFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_auth, SignInFragment())
                .commit()
        }
    }
}
