package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.utils.Utils

class CagnotteFragment : Fragment() {

    companion object {
        var pot : Cagnotte = Cagnotte()
            get() {
                if (pot.isEmpty()) {
                    field = Cagnotte()
                }
                return field
            }
    }

    override fun onStart() {
        super.onStart()
        Utils.checkLoggedIn(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.cagnotte_fragment, container, false)

        //val name : String = (activity as MainActivity).getCagnotteToLoad()
        view.findViewById<Button>(R.id.balanceButton).setOnClickListener{
            selectFrag(view.findViewById<Button>(R.id.balanceButton))
        }

        view.findViewById<Button>(R.id.spendButton).setOnClickListener{
            selectFrag(view.findViewById<Button>(R.id.spendButton))
        }

        view.findViewById<FloatingActionButton>(R.id.newspendButton).setOnClickListener{
            findNavController().navigate(R.id.newspendFragment)
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

        return view
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
}
