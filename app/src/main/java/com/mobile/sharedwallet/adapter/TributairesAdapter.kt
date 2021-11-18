package com.mobile.sharedwallet.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sharedwallet.R
import com.mobile.sharedwallet.dialog.NewSpendDialog.Companion.price
import com.mobile.sharedwallet.models.Tributaire
import java.math.BigDecimal
import java.math.RoundingMode


class TributairesAdapter(private val dataSet: ArrayList<Tributaire>, private val view : View) :
    RecyclerView.Adapter<TributairesAdapter.ViewHolder>() {

    private var listItemManual = mutableMapOf<Int,Float>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox : CheckBox = view.findViewById(R.id.check_box)
        val textView : TextView = view.findViewById(R.id.textView)
        val coutView : TextView = view.findViewById(R.id.coutView)
        val percentageInput : TextView = view.findViewById(R.id.percentage_input)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_item, viewGroup, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        var size : Int
        viewHolder.textView.text = dataSet[position].name
        viewHolder.checkbox.isSelected = dataSet[position].selected

        viewHolder.checkbox.setOnCheckedChangeListener { _, b ->
            dataSet[position].selected = b
            if (!dataSet[position].selected) {
                viewHolder.coutView.text = ""
                viewHolder.percentageInput.text = ""
                listItemManual.remove(position)
            }
            size = peopleSelected().size
            updateCoutPeople(price, size)
        }

        if (dataSet[position].selected) {
            viewHolder.percentageInput.text = dataSet[position].percentageCout.toString()
            viewHolder.coutView .text = dataSet[position].cout.toString()
        }

        viewHolder.percentageInput.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                listItemManual[position] = viewHolder.percentageInput.text.toString().toFloat()
                if(listItemManual.size >= peopleSelected().size) {
                    listItemManual.remove(listItemManual.keys.last())
                }
                updateCoutPeople(price, peopleSelected().size)
                true
            }
            else false
        }
    }

    private fun updateCoutPeople(price: Float, size : Int) {
        for (i in 0 until dataSet.size - 1) {
            if (dataSet[i].selected) {
                if ((price != 0f) && (size != 0)) {
                    if (listItemManual.isEmpty()) {
                        dataSet[i].percentageCout = 1f / size.toFloat() * 100f
                        dataSet[i].cout = BigDecimal(
                            (price * dataSet[i].percentageCout / 100f).toString().toDouble()
                        ).setScale(2, RoundingMode.HALF_UP).toFloat()
                    } else {
                        if (i in listItemManual.keys) {
                            dataSet[i].percentageCout = listItemManual[i]!!
                            dataSet[i].cout = BigDecimal(
                                (price * listItemManual[i]!! / 100).toString().toDouble()
                            ).setScale(2, RoundingMode.HALF_UP).toFloat()
                        } else {
                            var somme = 0f
                            listItemManual.forEach { somme += it.value }
                            dataSet[i].percentageCout =
                                (100f - somme) / (size - listItemManual.size).toFloat()
                            dataSet[i].cout = BigDecimal(
                                (price * dataSet[i].percentageCout / 100f).toString().toDouble()
                            ).setScale(2, RoundingMode.HALF_UP).toFloat()
                        }
                    }
                } else {
                    dataSet[i].percentageCout = 1f / size.toFloat() * 100
                    dataSet[i].cout = 0f
                }
                //notifyItemChanged(i)
            }
        }
        notifyDataSetChanged()
    }

    fun peopleSelected() : ArrayList<Tributaire>{
        val participantSelected = ArrayList<Tributaire>()
        for (person in dataSet) {
            if (person.selected) {
                participantSelected.add(person)
            }
        }
        return participantSelected
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
