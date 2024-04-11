package com.example.vaccinationmanagement.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.vaccinationmanagement.R

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val textVaccineName: TextView = itemView.findViewById(R.id.textVaccineName)
//        val textDate: TextView = itemView.findViewById(R.id.textDate)
//        val textTime: TextView = itemView.findViewById(R.id.textTime)
//        val dose: TextView = itemView.findViewById(R.id.dose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val vaccination = vaccinationHistory[position]
//        holder.textVaccineName.text = vaccination.vaccineName
//        holder.textDate.text = vaccination.date
//        holder.textTime.text = vaccination.time
//        holder.dose.text = vaccination.dose
    }

    override fun getItemCount(): Int {
//        return vaccinationHistory.size
        return 0
    }
}