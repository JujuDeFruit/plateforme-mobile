package com.mobile.sharedwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.mobile.sharedwallet.models.BDD


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** Exemple */

        /*val store = BDD.firestore;

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
                }*/

        setContentView(R.layout.activity_main)

        //val fragmentID : Int = if (LoginFragment.uID != null) R.id.homeFragment else R.id.loginFragment;
        findNavController(R.id.navigation).navigate(R.id.loginFragment)
    }
    private var globalCagnottetoLoad : String = ""

    fun getCagnotteToLoad() : String{
        return globalCagnottetoLoad
    }

    fun setCagnotteToLoad(cName : String){
        globalCagnottetoLoad = cName
    }


}