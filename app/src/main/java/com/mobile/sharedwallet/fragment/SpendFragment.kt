package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.fragment.CagnotteFragment.Companion.pot
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense

class SpendFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view: View = inflater.inflate(R.layout.spend_fragment, container, false)
        loadCagnotteList()
        return view
    }

    private fun loadCagnotteList(){

        // Load all pots current user is involved in
        Firebase.firestore
            .collection(FirebaseConstants.CollectionNames.Pot)
            .document(CagnotteFragment.potRef)
            .get()
            .addOnSuccessListener { result ->
                result.toObject<Cagnotte>().also { cagnotte : Cagnotte? ->
                    cagnotte?.let {
                        pot = it
                    }
                }
                createTextViewClick(pot.totalSpent)
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }


    private fun createTextViewClick(listdepenses: List<Depense>) {
        var liste = view?.findViewById<LinearLayout>(R.id.spends)
        for(depense in listdepenses){
            var inputText=depense.title
            var newTextView = TextView(activity)
            newTextView.setPadding(90, 50, 80, 50)
            newTextView.layoutParams = ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            newTextView.setTextColor(Color.BLACK)
            newTextView.textSize = 25f
            newTextView.text = inputText
            newTextView.id = inputText.hashCode()
            /*newTextView.isClickable = true
                newTextView.setOnClickListener{
                    loadCagnottePage(inputText)
                }*/
            liste?.addView(newTextView)
        }
    }
}