package com.mobile.sharedwallet.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Participant
import com.mobile.sharedwallet.utils.Utils
import kotlin.math.abs
import kotlin.math.min

class BalanceFragment: Fragment() {

    private var participants : ArrayList<Participant>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        participants = CagnotteFragment.pot.participants
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.balance_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quiDoitQuoi()
    }

    private fun quiDoitQuoi(){
        //on trie la liste des participants par ordre croissant de cout
        val sortedpaticipants = participants!!.sortedByDescending{it.solde}

        // on retire a toute la liste des participants le montant moyen
        var i = 0;
        var j = sortedpaticipants.size - 1;
        var debt : Float

        while (i < j) {
            debt = min(
                abs(sortedpaticipants[i].solde),
                abs(sortedpaticipants[j].solde)
            )

            sortedpaticipants[i].solde = sortedpaticipants[i].solde - debt;
            sortedpaticipants[j].solde = sortedpaticipants[j].solde + debt;

            createTextView(sortedpaticipants[i].name.toString(), sortedpaticipants[j].name.toString(), debt.toString())
            if (sortedpaticipants[i].solde == 0f) {
                i++;
            }
            if (sortedpaticipants[j].solde == 0f) {
                j--;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createTextView(p1: String, p2: String, debt: String) {
        val liste = view?.findViewById<LinearLayout>(R.id.remboursement)
        val newTextView = TextView(requireContext())
        newTextView.setPadding(90, 50, 80, 50)
        newTextView.setTextColor(Color.BLACK)
        newTextView.textSize = 15f
        newTextView.text = "$p2 owes $debt to $p1"
        liste?.addView(newTextView)
    }
}