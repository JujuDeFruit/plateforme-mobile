package com.mobile.sharedwallet.models

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.sharedwallet.R
import com.site_valley.listwithcheckboxexamplekotlin.databinding.ListItemWithCbBinding

class CategoryAdapter(val catList: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listData = catList[position]
        holder.binding.name.text = listData.name
        holder.binding.cb.isChecked = listData.selected
        holder.binding.cb.setOnCheckedChangeListener { compoundButton, b ->
            listData.selected = b
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return catList.size
    }


}