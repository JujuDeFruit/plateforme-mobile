package com.mobile.sharedwallet.fragment

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.mobile.sharedwallet.MainActivity
import com.mobile.sharedwallet.R
import java.util.zip.Inflater

class HomeFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        println("bien charge------------------------------")

        val view: View = inflater.inflate(R.layout.home_fragment, container, false)

        view.findViewById<Button>(R.id.CreateButton).setOnClickListener{
            CreateButtonClick()
        }

        return view
    }
    fun CreateButtonClick() {
        Toast.makeText(activity, "oui", Toast.LENGTH_SHORT).show()
        println("Test reussi------------------------------")
        val Liste = view?.findViewById<LinearLayout>(R.id.ListCagnotte)
        val tv_dynamic = TextView(activity) as TextView
        tv_dynamic.textSize = 20f
        tv_dynamic.text = "This is a dynamic TextView generated programmatically in Kotlin"
        Liste?.addView(tv_dynamic)

    }
}