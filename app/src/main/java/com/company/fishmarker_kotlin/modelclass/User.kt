package com.company.fishmarker_kotlin.modelclass

class User(var user_name: String,
           var last_name: String ,
           var email: String,
           var location: String,
           var telephone: String,
           var type_of_fishing: String,
           var trophies: String,
           var about_me: String,
           var url_photo : String) {

    constructor(user_name: String, email: String, location: String) :
            this(user_name, email, location, "null", "null", "null", "null", "null","null")


}