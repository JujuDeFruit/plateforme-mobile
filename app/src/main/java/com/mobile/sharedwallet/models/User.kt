package com.mobile.sharedwallet.models

data class User(
    val uid : String?,
    val firstName : String?,
    val lastName : String?,
    val email : String?,
) : Model {
    constructor() : this(null, null, null, null)

    enum class Attributes(val string: String) {
        UID("uid"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        EMAIL("email"),
    }

    fun isEmpty() : Boolean {
        return uid.isNullOrEmpty()
                && firstName.isNullOrEmpty()
                && lastName.isNullOrEmpty()
                && email.isNullOrEmpty()
    }

    override fun toFirebase(): HashMap<String, Any?> {
        return hashMapOf<String, Any?>(
            Attributes.UID.string to uid,
            Attributes.FIRST_NAME.string to firstName,
            Attributes.LAST_NAME.string to lastName,
            Attributes.EMAIL.string to email,
        )
    }
}
