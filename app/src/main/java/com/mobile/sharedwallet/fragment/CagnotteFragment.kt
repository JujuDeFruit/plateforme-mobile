package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginTop
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Cagnotte
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.models.User

class CagnotteFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.cagnotte_fragment, container, false)

        var name : String = (activity as MainActivity).getCagnotteToLoad()
        //Connexion a firebase, au document correspondant au nom de la cagnotte qu'on veut charger
        /*
        var DepCollect : MutableList<Depense>

        for (item in collection){
            var newDep = Depense(null, null,null)
            DepCollect.add(newDep)
        }

        var CurrentCagnotte = Cagnotte(name, DepCollect, null)
        */
        var Liste = view?.findViewById<LinearLayout>(R.id.ListeDepense)
        var NewTextView = TextView(activity)
        NewTextView.setPadding(10,20,0,20)
        NewTextView.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        NewTextView.setTextColor(Color.BLACK)
        NewTextView.textSize = 15f
        NewTextView.text = name + " - 15$, Julien"
        NewTextView.id = name.hashCode()
        Liste?.addView(NewTextView)


        return view;
    }
}
