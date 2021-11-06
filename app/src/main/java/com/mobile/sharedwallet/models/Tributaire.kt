package com.mobile.sharedwallet.models

class Tributaire (
    val name : String,
    val uid : String,
    var cout : Float,
) : Model {

    enum class Attributes(val string: String) {
        NAME("name"),
        UID("uid"),
        COUT("cout")
    }

    constructor() : this("","",0f)

    override fun toFirebase(): HashMap<String, Any?> {
        return hashMapOf(
            Attributes.NAME.string to name,
            Attributes.UID.string to uid,
            Attributes.COUT.string to cout
        )
    }
}