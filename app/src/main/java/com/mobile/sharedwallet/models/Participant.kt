package com.mobile.sharedwallet.models

data class Participant(
    val name: String,
    val id: String,
    var cout: Float,
    var selected: Boolean): Model {

    enum class Attributes(val string: String) {
        NAME("name"),
        ID("id"),
        COUT("cout"),
        SELECTED("selected")
    }

    constructor() : this("","",0f,false)

    override fun toFirebase(): HashMap<String, Any?> {
        return hashMapOf(
            Attributes.NAME.string to name,
            Attributes.ID.string to id,
            Attributes.COUT.string to cout,
            Attributes.SELECTED.string to selected
        )
    }
}