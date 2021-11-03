package com.mobile.sharedwallet.models

import android.provider.Telephony

// Users are stored with their uID, that is why there are strings
data class Depense (
    val title : String,
    val whoPaid : Participant,
    val amountPaid : Float,
    val forWho : List<Participant>
    ) : Model {

    constructor() : this("", Participant(), 0.0f, ArrayList<Participant>())

    override fun toFirebase() : HashMap<String, Any?> {
        return hashMapOf(
            "title" to title,
            "whoPaid" to whoPaid,
            "amountPaid" to amountPaid,
            "forWho" to forWho.map { it.toFirebase() }
        )
    }
}