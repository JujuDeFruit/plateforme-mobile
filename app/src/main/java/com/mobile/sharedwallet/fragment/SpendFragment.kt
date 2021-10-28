package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Utils

class SpendFragment : Fragment() {

    private lateinit var store : FirebaseFirestore
    private var cagnotte : Cagnotte? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        store = FirebaseFirestore.getInstance()
        cagnotte = CagnotteFragment.pot
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.spend_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.newSpendButton).setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(NewSpendFragment())
        }

        view.findViewById<FloatingActionButton>(R.id.addPerson).setOnClickListener {
            addMember()
        }

        cagnotte?.let { createTextViewClick(it.totalSpent) }
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }


    private fun createTextViewClick(listdepenses: List<Depense>) {
        val liste = view?.findViewById<LinearLayout>(R.id.spends)
        for(depense in listdepenses){
            val inputText = depense.title
            val newTextView = TextView(activity)
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

    private fun addMember(){
        cagnotte?.let { cagnotte : Cagnotte ->
            val people = cagnotte.participants
            people.add(
                User(
                    "ACGGNPVUBIPpaH7A480QN6V7npU2",
                    "test",
                    "test",
                    "test@yahoo.fr",
                    null,
                    0.0f
                )
            )

            store
                .collection(FirebaseConstants.CollectionNames.Pot)
                .document(CagnotteFragment.potRef)
                .update(Cagnotte.Attributes.PARTICIPANTS.string, people.map { it.toFirebase() })
                .addOnSuccessListener {
                    // TODO
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.message_error_add_participant),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}