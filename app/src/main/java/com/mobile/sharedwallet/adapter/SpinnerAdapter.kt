package com.mobile.sharedwallet.adapter

import com.mobile.sharedwallet.R
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.mobile.sharedwallet.models.Participant

class SpinnerAdapter : AdapterView.OnItemSelectedListener {

    fun generateSpinner (context: Context, view: View, participants:ArrayList<Participant>? ){
        val spinner:Spinner= view.findViewById(R.id.spinnerPayeur)
        val spinnerArrayAdapter:ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            participants!!.map{it.name}
        )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerArrayAdapter
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        println("yesssssssss j'ai été cliqué ")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        println("rien n'a été cliqué")
    }
}