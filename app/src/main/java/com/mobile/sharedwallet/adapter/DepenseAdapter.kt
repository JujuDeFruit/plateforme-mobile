package com.mobile.sharedwallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.fragment.CagnotteFragment
import com.mobile.sharedwallet.models.Depense

class DepenseAdapter(private val context_ : Context, private val dataSet : ArrayList<Depense>) : ArrayAdapter<Depense>(context_, R.layout.depense_row) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val depense : Depense = dataSet[position]

        val depenseView : View = LayoutInflater.from(context_).inflate(R.layout.depense_row, parent,false)

        // Fetch Tributaire photo
        depenseView.findViewById<ImageView>(R.id.depensePurchaserPhoto).setImageBitmap(
                CagnotteFragment.pot.participants.first { it.uid == depense.whoPaid.uid }.photo
        )
        depenseView.findViewById<TextView>(R.id.depenseTitle).text = depense.title
        depenseView.findViewById<TextView>(R.id.depenseDescription).text = "► ".plus("Category")
        depenseView.findViewById<TextView>(R.id.depenseMetaText).text = depense.amountPaid.toString()
                .plus(context_.getString(R.string.space))
                .plus(context_.getString(R.string.euro_symbol))

        return depenseView
    }


}