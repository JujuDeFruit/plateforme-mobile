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
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobile.sharedwallet.MainActivity


class HomeFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        println("bien charge------------------------------")

        val view: View = inflater.inflate(R.layout.home_fragment, container, false)

        view.findViewById<FloatingActionButton>(R.id.CreateButton).setOnClickListener{
            openDialog()
        }

        view.findViewById<FloatingActionButton>(R.id.RefreshButton).setOnClickListener{
            loadCagnotteList()
        }

        return view
    }

    private fun loadCagnotteList() {
        TODO("Not yet implemented")
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
        var NewTextView = TextView(activity)
        NewTextView.setPadding(90,50,80,50)
        NewTextView.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        NewTextView.setTextColor(Color.BLACK)
        NewTextView.textSize = 25f
        NewTextView.text = inputtext
        NewTextView.id = inputtext.hashCode()
        NewTextView.isClickable = true
        NewTextView.setOnClickListener{
            LoadCagnottePage(inputtext)
        }
        Liste?.addView(NewTextView)
    }

    fun LoadCagnottePage(inputtext : String?){
        if (inputtext != null) {
            (activity as MainActivity).setCagnotteToLoad(inputtext)
            findNavController().navigate(R.id.cagnotteFragment)
        }

    }

}