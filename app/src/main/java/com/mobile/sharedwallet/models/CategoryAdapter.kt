package com.mobile.sharedwallet.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView


class CategoryAdapter(
    context: Context,
    textViewResourceId: Int,
    objects: ArrayList<Category>) : ArrayAdapter<Category>(context, textViewResourceId, objects) {

    private val categoryList : ArrayList<Category> = objects;

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View=inflater.inflate(com.mobile.sharedwallet.R.layout.row_item, parent, false)
        val textView = view.findViewById<TextView>(com.mobile.sharedwallet.R.id.textView)
        val checkbox = view.findViewById<CheckBox>(com.mobile.sharedwallet.R.id.checkbox)
        textView.text = categoryList[position].name
        checkbox.isChecked = categoryList[position].selected
        /*checkbox.setOnCheckedChangeListener(b ->
            listData.selected = b
            notifyDataSetChanged()
        )*/

        return view
    }
}