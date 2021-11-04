package com.mobile.sharedwallet.models

data class Participant(
    val name : String,
    val uid : String,
    var cout : Float,
    var solde : Float,
    var selected : Boolean
    ) : Model {

    enum class Attributes(val string: String) {
        NAME("name"),
        UID("uid"),
        COUT("cout"),
        SELECTED("selected"),
        SOLDE("solde"),
    }

    constructor() : this("","",0f,0f,false)

    override fun toFirebase(): HashMap<String, Any?> {
        return hashMapOf(
            Attributes.NAME.string to name,
            Attributes.UID.string to uid,
            Attributes.COUT.string to cout,
            Attributes.SELECTED.string to selected,
            Attributes.SOLDE.string to solde,
        )
    }

    override fun toString(): String {
        return name
    }
}