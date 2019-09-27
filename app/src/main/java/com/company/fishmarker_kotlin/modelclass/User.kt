package com.company.fishmarker_kotlin.modelclass

data class User(var user_id: String = "",
           var user_name: String = "",
           var last_name: String = "" ,
           var email: String= "",
           var location: String= "",
           var telephone: String = "",
           var type_of_fishing: String= "",
           var trophies: String = "",
           var about_me: String = "",
           var url_photo : String = "") {

    constructor(user_name: String, email: String, location: String) :
            this("null",user_name, email, location, "null", "null", "null", "null", "null","null")

   /* constructor(user_id: String,
                user_name: String,
                last_name: String ,
                email: String,
                location: String,
                telephone: String,
                type_of_fishing: String,
                trophies: String,
                about_me: String,
                url_photo : String) : this(user_id = null,
        user_name = null,
        last_name = null ,
        email = null,
        location = null,
        telephone = null,
        type_of_fishing = null,
        trophies = null,
        about_me = null,
        url_photo = null)
*/

}