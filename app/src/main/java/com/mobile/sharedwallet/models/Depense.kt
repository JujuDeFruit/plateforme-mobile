package com.mobile.sharedwallet.models

// Users are stored with their uID, that is why there are strings
data class Depense (
    val whoPaid: User,
    val amountPaid : Float,
    val forWho : List<User>
    ) : Model {

    constructor() : this(User(), 0.0f, ArrayList<User>())

    override fun toFirebase() : HashMap<String, Any> {
        return hashMapOf(
            "whoPaid" to whoPaid,
            "amountPaid" to amountPaid,
            "forWho" to forWho
        )
    }
}