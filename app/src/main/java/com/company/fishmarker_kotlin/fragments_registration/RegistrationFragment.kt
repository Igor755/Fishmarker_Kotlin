package com.company.fishmarker_kotlin.fragments_registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.company.fishmarker_kotlin.R

class RegistrationFragment : Fragment(){

override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.fragment_registration, container, false)

    return view

}
}