package com.mobile.sharedwallet.models

data class User(
    var uid : String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var validEmail: Boolean
) : Model {
    constructor() : this("", "", "", "", false)

    fun isEmpty() : Boolean {
        return uid == "" && firstName == "" && lastName == "" && email == "" && !validEmail
    }

    override fun toFirebase(): HashMap<String, Any> {
        return hashMapOf(
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "validEmail" to validEmail
        )
    }
}