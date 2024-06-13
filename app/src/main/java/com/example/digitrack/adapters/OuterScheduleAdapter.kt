package com.example.digitrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Students

class OuterScheduleAdapter(
    private val groupedSchedules: Map<String, List<Pair<Students, String>>>,
    private val selectedDate: String, // Tambahkan parameter ini
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<OuterScheduleAdapter.OuterScheduleViewHolder>() {

    inner class OuterScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.rvInner)

        fun bind(time: String, schedules: List<Pair<Students, String>>) {
            tvTime.text = "JAM $time WIB"

            innerRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            innerRecyclerView.adapter = InnerAdapter(schedules.map { it.first }, selectedDate, time) // Pass date and time to InnerAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OuterScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_schedule, parent, false)
        return OuterScheduleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return groupedSchedules.size
    }

    override fun onBindViewHolder(holder: OuterScheduleViewHolder, position: Int) {
        val time = groupedSchedules.keys.toList()[position]
        val schedules = groupedSchedules[time]!!
        holder.bind(time, schedules)
    }
}