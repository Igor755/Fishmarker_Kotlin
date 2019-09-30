package com.company.fishmarker_kotlin.helper_class

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.util.*
import kotlin.collections.ArrayList

class StaticHelper {

    companion object {

        fun getValue() : ArrayList<String>{

            val allCountry: ArrayList<String> = ArrayList()
            val coutryCodes: Array<out String>? = Locale.getISOCountries()
            val coutryCodes2: String? = null
            if (coutryCodes != null) {
                for (coutryCodes2 in coutryCodes) {

                    val locale: Locale = Locale("", coutryCodes2)
                    val coutryName: String = locale.displayCountry
                    allCountry.add(coutryName)
                }

            }
            allCountry.sort()

            return allCountry
        }

        fun isNetworkAvailable(context: Context): Boolean {

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetworkInfo: NetworkInfo? = null
            activeNetworkInfo = cm.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }


    }


}