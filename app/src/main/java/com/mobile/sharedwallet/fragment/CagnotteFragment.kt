package com.mobile.sharedwallet.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Utils

class CagnotteFragment : Fragment() {

    companion object {
        var pot : Cagnotte = Cagnotte()
            get() {
                if (field.isEmpty()) {
                    field = Cagnotte()
                }
                return field
            }
        var potRef : String = ""
            get() {
                if(field.isEmpty()) {
                    field = ""
                }
                return field
            }
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.cagnotte_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.balanceButton).setOnClickListener {
            selectFrag(view.findViewById<Button>(R.id.balanceButton))
        }

        view.findViewById<Button>(R.id.spendButton).setOnClickListener {
            selectFrag(view.findViewById<Button>(R.id.spendButton))
        }

        view.findViewById<FloatingActionButton>(R.id.newspendButton).setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(NewSpendFragment())
        }

        /*val liste = view.findViewById<LinearLayout>(R.id.listeDepense)
        val newTextView = TextView(activity)
        newTextView.setPadding(10,20,0,20)
        newTextView.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )

        newTextView.setTextColor(Color.BLACK)
        newTextView.textSize = 15f
        newTextView.text = name
        newTextView.id = name.hashCode()
        liste?.addView(newTextView)*/

        view.findViewById<FloatingActionButton>(R.id.addperson).setOnClickListener {
            addMember()
        }
    }

    private fun selectFrag(view:View){
        val fr : Fragment = if (view == view.findViewById<Button>(R.id.balanceButton)){
            BalanceFragment()
        } else {
            SpendFragment()
        }
        val fm = childFragmentManager

        fm.beginTransaction()
            .replace(R.id.fragment_place, fr)
            .addToBackStack(null)
            .commit()
    }

    private fun addMember(){
        val people = pot.participants
        people.add(User("ACGGNPVUBIPpaH7A480QN6V7npU2", "test", "test", "test@yahoo.fr", null,0.0f))
        Firebase
            .firestore
            .collection(FirebaseConstants.CollectionNames.Pot)
            .document(potRef)
            .update(Cagnotte.Attributes.PARTICIPANTS.string, people.map { it.toFirebase() })
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "An error occured while creating new pot. Please retry !", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error writing document", e)
            }
    }
}
