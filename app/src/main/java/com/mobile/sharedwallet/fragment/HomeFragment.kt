package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.mobile.sharedwallet.R
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.models.User
import java.util.ArrayList

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        println("bien charge------------------------------")
        loadCagnotteList()

        val view: View = inflater.inflate(R.layout.home_fragment, container, false)

        view.findViewById<FloatingActionButton>(R.id.createButton).setOnClickListener{
            openDialog()
        }

        return view
    }

    private fun loadCagnotteList() {
        // Load all pots current user is involved in
        Firebase.firestore
            .collection(FirebaseConstants.Pot)
            .whereArrayContains(Cagnotte.Attributes.PARTICIPANTS.string, LoginFragment.currentUser.toFirebase())
            .get()
            .addOnSuccessListener { result ->
                val tricountList : HashMap<String, Cagnotte> = HashMap();

                for (document in result) {
                    tricountList[document.id] = document.toObject<Cagnotte>()
                }

                for ((key, value) in tricountList) {
                    createButtonClick(value.name)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun openDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Nom du groupe")
        // Set up the input
        val input = EditText(activity)
        builder.setView(input)
        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            // Here you get get input text from the Edittext
            val mText = input.text.toString()
            if(mText != ""){
                createButtonClick(mText)
                addCagnotte(mText)
            } else {
                Toast.makeText(activity, "Nom de groupe invalide", Toast.LENGTH_SHORT).show()
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        builder.show()
    }

    private fun createButtonClick(inputText : String?) {
        println("Test reussi------------------------------")
        val liste = view?.findViewById<LinearLayout>(R.id.listCagnotte)
        val newTextView = TextView(activity)
        newTextView.setPadding(90,50,80,50)
        newTextView.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        newTextView.setTextColor(Color.BLACK)
        newTextView.textSize = 25f
        newTextView.text = inputText
        newTextView.id = inputText.hashCode()
        newTextView.isClickable = true
        newTextView.setOnClickListener{
            loadCagnottePage(inputText)
        }
        liste?.addView(newTextView)
    }

    private fun loadCagnottePage(inputText : String?){
        if (inputText != null) {
            (activity as MainActivity).setCagnotteToLoad(inputText)
            findNavController().navigate(R.id.cagnotteFragment)
        }

        val liste = view?.findViewById<LinearLayout>(R.id.listCagnotte)
        val tvDynamic = TextView(activity)
        tvDynamic.setPadding(90,50,80,50)
        tvDynamic.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        tvDynamic.setTextColor(Color.BLACK)
        tvDynamic.textSize = 25f
        tvDynamic.text = inputText
        liste?.addView(tvDynamic)
    }

    private fun addCagnotte(name: String){

        // Create a new cagnotte with a first and last name
        val info : HashMap<String, Any> =
            Cagnotte(name, Timestamp.now(), ArrayList<Depense>(), arrayListOf(LoginFragment.currentUser)).toFirebase()
        // Add a new document with a generated ID
        Firebase
            .firestore
            .collection(FirebaseConstants.Pot)
            .add(info)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }
}