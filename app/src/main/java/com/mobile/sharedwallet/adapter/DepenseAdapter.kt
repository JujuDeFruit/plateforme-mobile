package com.mobile.sharedwallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Depense
import com.mobile.sharedwallet.utils.Shared

class DepenseAdapter(context : Context, private val dataSet : ArrayList<Depense>) : ArrayAdapter<Depense>(context, R.layout.depense_row) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val depense : Depense = dataSet[position]

        val depenseView : View = convertView ?: LayoutInflater.from(context).inflate(R.layout.depense_row, parent,false)

        // Fetch Tributaire photo
        depenseView.findViewById<ImageView>(R.id.depensePurchaserPhoto).setImageBitmap(
            Shared.pot.participants.first { it.uid == depense.whoPaid.uid }.photo
        )
        depenseView.findViewById<TextView>(R.id.depenseTitle).text = depense.title
        depenseView.findViewById<TextView>(R.id.depenseDescription).text = "► ".plus("Category")
        depenseView.findViewById<TextView>(R.id.depenseMetaText).text = depense.amountPaid.toString()
            .plus(context.getString(R.string.space))
            .plus(context.getString(R.string.euro_symbol))

        return depenseView

    }

    override fun add(`object` : Depense?) {
        dataSet.add(`object` ?: Depense())
        dataSet.sortByDescending { it.creationDate }
        notifyDataSetChanged()
    }
}