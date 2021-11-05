package com.mobile.sharedwallet.models

import com.google.firebase.Timestamp

data class Cagnotte(
    val uids : ArrayList<String>,
    val name: String,
    val creationDate: Timestamp,
    val totalSpent: List<Depense>,
    val participants: ArrayList<Participant>,
    ) : Model {

    enum class Attributes(val string: String) {
        UIDS("uids"),
        NAME("name"),
        CREATION_DATE("creationDate"),
        TOTAL_SPENT("totalSpent"),
        PARTICIPANTS("participants")
    }

    constructor() : this(ArrayList(), "", Timestamp(0, 0), ArrayList<Depense>(), ArrayList<Participant>())

    fun isEmpty() : Boolean {
        return uids.isEmpty() && name == "" && creationDate == Timestamp(0, 0) && totalSpent.isEmpty() && participants.isEmpty()
    }

    override fun toFirebase() : HashMap<String, Any?> {
        return hashMapOf(
            Attributes.UIDS.string to uids,
            Attributes.NAME.string to name,
            Attributes.CREATION_DATE.string to creationDate,
            Attributes.TOTAL_SPENT.string to totalSpent.map { it.toFirebase() },
            Attributes.PARTICIPANTS.string to participants.map { it.toFirebase()}
        )
    }
}