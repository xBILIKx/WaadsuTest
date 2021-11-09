package com.example.waadsutest

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.waadsutest.databinding.CoordinatItemBinding

import com.example.waadsutest.databinding.FragmentMainBinding

class CoordRecyclerViewAdapter(
    private val values: List<CoordinatItem>
) : RecyclerView.Adapter<CoordRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            CoordinatItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idTextView.text = item.id
        holder.coordTextView.text = item.coord
        holder.locNameTextView.text = item.locName
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: CoordinatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idTextView: TextView = binding.idTextView
        val coordTextView: TextView = binding.coordinatsTextView
        val locNameTextView: TextView = binding.locNameTextView
    }

}