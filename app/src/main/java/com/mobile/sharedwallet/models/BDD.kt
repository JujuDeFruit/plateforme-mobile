package com.mobile.sharedwallet.models

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class BDD {
    private val store : FirebaseFirestore = Firebase.firestore;

    fun addCagnotte(nameCagnotte: String){
        //date de création de la cagnotte
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        // Create a new user with a first and last name
        val info = hashMapOf(
            "name" to nameCagnotte,
            "date de création" to currentDate
        )
        // Add a new document with a generated ID
        store.collection("Cagnotte")
            .document(nameCagnotte)
            .set(info, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

}