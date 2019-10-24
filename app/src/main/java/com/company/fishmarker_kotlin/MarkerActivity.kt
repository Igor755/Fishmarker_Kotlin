package com.company.fishmarker_kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company.fishmarker_kotlin.fragments_marker.MapMarkerFragment

class MarkerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker)



        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_marker, MapMarkerFragment())
                .commit()
        }
    }
}