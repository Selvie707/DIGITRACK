package com.example.digitrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.HelpCenter

class HelpCenterAdapter(
    private val hcList: List<HelpCenter>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<HelpCenterAdapter.HelpCenterViewHolder>() {

    inner class HelpCenterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        private val tvAnswer: TextView = itemView.findViewById(R.id.tvAnswer)
        private val vLine: View = itemView.findViewById(R.id.line)
        private val btnExpand: ImageView = itemView.findViewById(R.id.btnExpand)
        private val btnUnExpand: ImageView = itemView.findViewById(R.id.btnUnExpand)

        fun bind(helpCenter: HelpCenter) {
            tvQuestion.text = helpCenter.hcQuestion
            tvAnswer.text = helpCenter.hcAnswer

            btnExpand.setOnClickListener {
                vLine.visibility = View.VISIBLE
                tvAnswer.visibility = View.VISIBLE
                btnExpand.visibility = View.GONE
                btnUnExpand.visibility = View.VISIBLE
            }

            btnUnExpand.setOnClickListener {
                vLine.visibility = View.GONE
                tvAnswer.visibility = View.GONE
                btnExpand.visibility = View.VISIBLE
                btnUnExpand.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpCenterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_faq, parent, false)
        return HelpCenterViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return hcList.size
    }

    override fun onBindViewHolder(holder: HelpCenterViewHolder, position: Int) {
        holder.bind(hcList[position])
    }
}