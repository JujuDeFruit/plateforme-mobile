package com.mobile.sharedwallet.models

data class User(
    val uid : String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val validEmail: Boolean
) : Model {
    constructor() : this(null, null, null, null, false)

    fun isEmpty() : Boolean {
        return uid.isNullOrEmpty() && firstName.isNullOrEmpty() && lastName.isNullOrEmpty() && email.isNullOrEmpty() && !validEmail
    }

    override fun toFirebase(): HashMap<String, Any> {
        return hashMapOf<String, Any>(
            "uid" to uid!!,
            "firstName" to firstName!!,
            "lastName" to lastName!!,
            "email" to email!!,
            "validEmail" to validEmail
        )
    }
}