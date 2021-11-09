package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.constants.FirebaseConstants
import com.mobile.sharedwallet.dialog.AddUserToPotDialog
import com.mobile.sharedwallet.dialog.NewSpendDialog
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.models.User
import com.mobile.sharedwallet.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
            NewSpendDialog().show(parentFragmentManager, "NewSpendFragment")
        }

        view.findViewById<FloatingActionButton>(R.id.addPerson).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val usersEmails = fetchUsersEmails()
                AddUserToPotDialog(cagnotte, usersEmails).show(parentFragmentManager, "AddUserToPotDialog")
            }
        }

        cagnotte?.let { createTextViewClick(it.totalSpent) }
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }


    private fun createTextViewClick(listDepenses: ArrayList<Depense>) {
        val liste = view?.findViewById<LinearLayout>(R.id.spends)
        for(depense in listDepenses){
            val inputText = depense.title
            val newTextView = TextView(activity)
            newTextView.setPadding(30, 20,0, 0)
            newTextView.layoutParams = ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            newTextView.setTextColor(Color.BLACK)
            newTextView.textSize = 20f
            newTextView.text = inputText
            newTextView.id = inputText.hashCode()

            liste?.addView(newTextView)

            val newTextViewDetail = TextView(activity)
            val detailText : String = "► " + depense.amountPaid.toString() + " paid by " + depense.whoPaid.name
            newTextViewDetail.setPadding(30, 10, 0, 20)
            newTextViewDetail.layoutParams = ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            newTextViewDetail.setTextColor(Color.GRAY)
            newTextViewDetail.textSize = 12f
            newTextViewDetail.text = detailText
            newTextViewDetail.id = inputText.hashCode()

            liste?.addView(newTextViewDetail)

            val separatorView = View(activity)
            separatorView.layoutParams = ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                2
            )
            separatorView.setBackgroundColor(Color.DKGRAY)
            liste?.addView(separatorView)
        }
    }

    private suspend fun fetchUsersEmails() : ArrayList<String> {
        return withContext(Dispatchers.Main) {
            try {
                return@withContext store
                    .collection(FirebaseConstants.CollectionNames.Users)
                    .get()
                    .await()
                    .map { d -> d[User.Attributes.EMAIL.string].toString() }
                        as ArrayList<String>
            }
            catch (e : Exception) {
                return@withContext ArrayList<String>()
            }
        }
    }
}