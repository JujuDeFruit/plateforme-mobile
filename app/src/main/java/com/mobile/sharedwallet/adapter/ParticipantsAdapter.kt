package com.mobile.sharedwallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sharedwallet.models.Participant


class ParticipantsAdapter(private val dataSet: ArrayList<Participant>) :
    RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox:CheckBox
        val textView:TextView

        init {
            checkbox = view.findViewById(com.mobile.sharedwallet.R.id.check_box)
            textView = view.findViewById(com.mobile.sharedwallet.R.id.textView)

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(com.mobile.sharedwallet.R.layout.row_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].name
        viewHolder.checkbox.isSelected = dataSet[position].selected
        viewHolder.checkbox.setOnCheckedChangeListener { _, b ->
            dataSet[position].selected = b
            notifyDataSetChanged()
        }

    }

    fun saveNewSpend():ArrayList<Participant>{
        var participant_selected = ArrayList<Participant>()
        for (person in dataSet){
            if (person.selected){
                participant_selected.add(person)
            }
        }
        return participant_selected
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}