package com.example.digitrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Materials

class MaterialsAdapter(
    private val materialsList: List<Materials>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder>() {

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMaterialName: TextView = itemView.findViewById(R.id.tvMaterial)

        fun bind(material: Materials) {
            tvMaterialName.text = material.materialName
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_materials, parent, false)
        return MaterialViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return materialsList.size
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(materialsList[position])
    }
}