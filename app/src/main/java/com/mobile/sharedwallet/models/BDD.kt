package com.mobile.sharedwallet.models

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BDD {

    fun addElement(nomElement : String){
        val store = Firebase.firestore;
        // Create a new user with a first and last name
        val testDoc = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )

        // Add a new document with a generated ID
        store.collection("test")
            .add(testDoc)
            .addOnSuccessListener { _ ->
                println("OK")
            }
            .addOnFailureListener { _ ->
                println("Error adding document")
            }
    }

}