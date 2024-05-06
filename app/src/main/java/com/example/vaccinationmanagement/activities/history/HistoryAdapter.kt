package com.example.vaccinationmanagement.activities.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagement.R

class HistoryAdapter(private val vaccinationHistory: List<VaccinationDetail>)
    : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textVaccineName: TextView = itemView.findViewById(R.id.tvVaccineNameItem)
        val textDate: TextView = itemView.findViewById(R.id.tvDateItem)
        val textTime: TextView = itemView.findViewById(R.id.tvTimeItem)
        val textAddress: TextView = itemView.findViewById(R.id.tvAddressItem)
        val textDose: TextView = itemView.findViewById(R.id.tvCurrentDose)
        val textDoctor: TextView = itemView.findViewById(R.id.tvDoctor)
        val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaccination = vaccinationHistory[position]
        holder.textVaccineName.text = vaccination.vaccineName
        holder.textDate.text = vaccination.date.toString()
        holder.textTime.text = vaccination.time.toString()
        holder.textAddress.text = vaccination.address
        holder.textDoctor.text = "Doctor: ${vaccination.doctorName} ${vaccination.doctorSurname}"
        holder.textDose.text = "Dose: ${vaccination.dose}"

//        holder.btnUpdate.setOnClickListener {
//            (it.context as HistoryActivity).updateAppointment(vaccination)
//        }
//
//        holder.btnDelete.setOnClickListener {
//            (it.context as HistoryActivity).deleteAppointment(vaccination)
//        }
    }

    override fun getItemCount(): Int {
        return vaccinationHistory.size
    }
}