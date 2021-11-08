package com.mobile.sharedwallet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.models.Participant
import com.mobile.sharedwallet.models.Tributaire
import com.mobile.sharedwallet.utils.Utils.Companion.castParticipantToTributaire


class ParticipantsAdapter(private val dataSet: ArrayList<Participant>) :
    RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox : CheckBox = view.findViewById(R.id.check_box)
        val textView : TextView = view.findViewById(R.id.textView)
        val inputText : EditText = view.findViewById(R.id.percentage_input)
        val coutView : TextView = view.findViewById(R.id.coutView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_item, viewGroup, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].name
        //viewHolder.textView.text = dataSet[position].solde.toString()
        viewHolder.checkbox.isSelected = dataSet[position].selected
        viewHolder.checkbox.setOnCheckedChangeListener { _, b ->
            dataSet[position].selected = b
            notifyDataSetChanged()
        }

    }

    fun peopleSelected() : ArrayList<Tributaire>{
        val participantSelected = ArrayList<Tributaire>()
        for (person in dataSet){
            if (person.selected){
                participantSelected.add(castParticipantToTributaire(person))
            }
        }
        return participantSelected
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}