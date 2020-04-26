package com.company.imetlin.fishmarker.helper_class

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.company.imetlin.fishmarker.modelclass.Place
import java.util.*
import kotlin.collections.ArrayList

class StaticHelper {


    companion object {

        var filepathimage = ""

        @JvmStatic
        var allplace: ArrayList<Place> = ArrayList()


        fun getValue(): ArrayList<String> {

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

