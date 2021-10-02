package com.mobile.sharedwallet.models

import android.net.Uri

data class User(
    val uid : String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val photoUri : Uri?
) : Model {
    constructor() : this(null, null, null, null, null)

    fun isEmpty() : Boolean {
        return uid.isNullOrEmpty()
                && firstName.isNullOrEmpty()
                && lastName.isNullOrEmpty()
                && email.isNullOrEmpty()
                && photoUri.toString().isNullOrEmpty()
    }

    override fun toFirebase(): HashMap<String, Any?> {
        return hashMapOf<String, Any?>(
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            //"photoUri" to if(photoUri.toString().isNullOrEmpty()) "" else photoUri.toString()
        )
    }
}