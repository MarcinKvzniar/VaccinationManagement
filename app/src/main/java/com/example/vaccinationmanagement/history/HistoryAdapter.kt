package com.example.vaccinationmanagement.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.data.VaccinationData

class HistoryAdapter(private val vaccinationHistory: List<VaccinationData>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorId: TextView = itemView.findViewById(R.id.tvDoctorId)
        val textVaccineName: TextView = itemView.findViewById(R.id.tvVaccineNameItem)
        val textDate: TextView = itemView.findViewById(R.id.tvDateItem)
        val textTime: TextView = itemView.findViewById(R.id.tvTimeItem)
        val textAddress: TextView = itemView.findViewById(R.id.tvAddressItem)
        val textDose: TextView = itemView.findViewById(R.id.tvCurrentDose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaccination = vaccinationHistory[position]
        holder.doctorId.text = vaccination.doctorId.toString()
        holder.textDate.text = vaccination.date
        holder.textTime.text = vaccination.time
        holder.textAddress.text = vaccination.address
        holder.textVaccineName.text = vaccination.vaccineName
        holder.textDose.text = vaccination.dose.toString()
    }

    override fun getItemCount(): Int {
        return vaccinationHistory.size
    }
}