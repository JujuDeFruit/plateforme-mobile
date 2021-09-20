package com.mobile.sharedwallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = Firebase.firestore;

        /** Exemple */
        /*
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
        */

        setContentView(R.layout.activity_main)
    }
}