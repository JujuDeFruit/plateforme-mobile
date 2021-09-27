package com.mobile.sharedwallet.fragment

import android.app.ActionBar
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.mobile.sharedwallet.R
import android.view.Gravity
import androidx.core.view.setPadding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        println("bien charge------------------------------")

        val view: View = inflater.inflate(R.layout.home_fragment, container, false)

        view.findViewById<FloatingActionButton>(R.id.CreateButton).setOnClickListener{
            openDialog()
        }

        return view
    }


    fun openDialog(){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle("Nom du groupe")
        // Set up the input
        val input = EditText(activity)
        builder.setView(input)
        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            var m_Text = input.text.toString()
            if(m_Text!=""){
                CreateButtonClick(m_Text)
            }else{
                Toast.makeText(activity, "Nom de groupe invalide", Toast.LENGTH_SHORT).show()
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }

    fun CreateButtonClick(inputtext : String? ) {
        println("Test reussi------------------------------")
        var Liste = view?.findViewById<LinearLayout>(R.id.ListCagnotte)
        var tv_dynamic = TextView(activity)
        tv_dynamic.setPadding(90,50,80,50)
        tv_dynamic.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        tv_dynamic.setTextColor(Color.BLACK)
        tv_dynamic.textSize = 25f
        tv_dynamic.text = inputtext
        Liste?.addView(tv_dynamic)
    }

}