package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mobile.sharedwallet.MainActivity
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

        val name : String = (activity as MainActivity).getCagnotteToLoad()
        //Connexion a firebase, au document correspondant au nom de la cagnotte qu'on veut charger
        /*
        var DepCollect : MutableList<Depense>

        for (item in collection){
            var newDep = Depense(null, null,null)
            DepCollect.add(newDep)
        }

        var CurrentCagnotte = Cagnotte(name, DepCollect, null)
        */
        val liste = view.findViewById<LinearLayout>(R.id.listeDepense)
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
        liste?.addView(newTextView)

        return view
    }
}
