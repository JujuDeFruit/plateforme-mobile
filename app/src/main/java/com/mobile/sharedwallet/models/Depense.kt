package com.mobile.sharedwallet.models

// Users are stored with their uID, that is why there are strings
data class Depense (
    val title : String,
    val whoPaid : Tributaire,
    val amountPaid : Float,
    val forWho : List<Tributaire>
    ) : Model {

    enum class Attributes(val string: String) {
        TITLE("TITLE"),
        WHOPAID("Who paid"),
        AMOUNTPAID("Amount paid"),
        FORWHO("For who")
    }

    constructor() : this("", Tributaire(), 0.0f, ArrayList<Tributaire>())

    override fun toFirebase() : HashMap<String, Any?> {
        return hashMapOf(
            Attributes.TITLE.string to title,
            Attributes.WHOPAID.string to whoPaid,
            Attributes.AMOUNTPAID.string to amountPaid,
            Attributes.FORWHO.string to forWho.map { it.toFirebase() }
        )
    }
}