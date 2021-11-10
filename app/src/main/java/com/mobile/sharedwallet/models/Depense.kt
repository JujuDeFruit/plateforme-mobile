package com.mobile.sharedwallet.models

// Users are stored with their uID, that is why there are strings
data class Depense (
    val title : String,
    val whoPaid : Tributaire,
    val amountPaid : Float,
    val forWho : List<Tributaire>
    ) : Model {

    enum class Attributes(val string: String) {
        TITLE("title"),
        WHO_PAID("whoPaid"),
        AMOUNT_PAID("amountPaid"),
        FOR_WHO("forWho")
    }

    constructor() : this("", Tributaire(), 0.0f, ArrayList<Tributaire>())

    override fun toFirebase() : HashMap<String, Any?> {
        return hashMapOf(
            Attributes.TITLE.string to title,
            Attributes.WHO_PAID.string to whoPaid,
            Attributes.AMOUNT_PAID.string to amountPaid,
            Attributes.FOR_WHO.string to forWho.map { it.toFirebase() }
        )
    }
}