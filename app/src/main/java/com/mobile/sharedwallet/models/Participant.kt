package com.mobile.sharedwallet.models

data class Participant(
    val name : String,
    val uid : String,
    var solde : Float
    ) : Model {

    enum class Attributes(val string: String) {
        NAME("name"),
        UID("uid"),
        SOLDE("solde"),
    }

    constructor() : this("","",0f)

    override fun toFirebase(): HashMap<String, Any?> {
        return hashMapOf(
            Attributes.NAME.string to name,
            Attributes.UID.string to uid,
            Attributes.SOLDE.string to solde,
        )
    }

    override fun toString(): String {
        return name
    }
}