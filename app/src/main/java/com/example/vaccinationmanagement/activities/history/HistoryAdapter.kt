package com.example.vaccinationmanagement.activities.history

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagement.R
import com.example.vaccinationmanagement.appointments.Appointments
import com.example.vaccinationmanagement.dbConfig.DBconnection
import com.example.vaccinationmanagement.doctors.DoctorsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat

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

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaccination = vaccinationHistory[position]
        holder.textVaccineName.text = vaccination.vaccineName
        holder.textDate.text = vaccination.date.toString()
        holder.textTime.text = vaccination.time.toString()
        holder.textAddress.text = vaccination.address
        holder.textDoctor.text = "Doctor: ${vaccination.doctorName} ${vaccination.doctorSurname}"
        holder.textDose.text = "Dose: ${vaccination.dose}"

        // Updating the date of the vaccination
        holder.textDate.setOnTouchListener(object : View.OnTouchListener {
            private var lastTouchDown: Long = 0

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTouchDown < ViewConfiguration.getDoubleTapTimeout()) {
                            val editText = EditText(v.context)
                            editText.setText((v as TextView).text)
                            (v.parent as ViewGroup).addView(editText)
                            v.visibility = View.GONE

                            editText.setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    val newDate = editText.text.toString()
                                    if (isDateValid(newDate)) {
                                        v.text = newDate
                                        v.visibility = View.VISIBLE
                                        (v.parent as ViewGroup).removeView(editText)
                                    } else {
                                        Toast
                                            .makeText(
                                                v.context,
                                                "Invalid date format",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                        lastTouchDown = currentTime
                    }
                }
                return true
            }
        })

        // Updating the time of the vaccination
        holder.textTime.setOnTouchListener(object : View.OnTouchListener {
            private var lastTouchDown: Long = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val currentTime = System.currentTimeMillis()

                        if (currentTime - lastTouchDown < ViewConfiguration.getDoubleTapTimeout()) {
                            val editText = EditText(v.context)
                            editText.setText((v as TextView).text)
                            (v.parent as ViewGroup).addView(editText)
                            v.visibility = View.GONE

                            editText.setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    val newTime = editText.text.toString()
                                    if (isTimeValid(newTime)) {
                                        v.text = newTime
                                        v.visibility = View.VISIBLE
                                        (v.parent as ViewGroup).removeView(editText)
                                    } else {
                                        Toast
                                            .makeText(
                                                v.context,
                                                "Invalid time format",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                        lastTouchDown = currentTime
                    }
                }
                return true
            }
        })

        // Updating the address of the vaccination
        holder.textAddress.setOnTouchListener(object : View.OnTouchListener {
            private var lastTouchDown: Long = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTouchDown < ViewConfiguration.getDoubleTapTimeout()) {

                            val editText = EditText(v.context)
                            editText.setText((v as TextView).text)
                            (v.parent as ViewGroup).addView(editText)
                            v.visibility = View.GONE

                            editText.setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    v.text = editText.text
                                    v.visibility = View.VISIBLE
                                    (v.parent as ViewGroup).removeView(editText)
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                        lastTouchDown = currentTime
                    }
                }
                return true
            }
        })

        holder.btnUpdate.setOnClickListener {
            Log.d("HistoryAdapter", "Update button clicked")
            (holder.itemView.context as HistoryActivity).lifecycleScope.launch {
                val doctorId = getDoctorId(vaccination.doctorName, vaccination.doctorSurname)
                val updatedAppointment = Appointments(
                    id = vaccination.vaccineId,
                    vaccineId = vaccination.vaccineId,
                    pesel = vaccination.pesel,
                    doctorId = doctorId,
                    date = Date.valueOf(holder.textDate.text.toString()),
                    time = Time.valueOf(holder.textTime.text.toString()),
                    address = holder.textAddress.text.toString(),
                    dose = vaccination.dose
                )
                (holder.itemView.context as HistoryActivity).updateAppointment(updatedAppointment)
            }
        }

        holder.btnDelete.setOnClickListener {
            (holder.itemView.context as HistoryActivity).lifecycleScope.launch {
                (holder.itemView.context as HistoryActivity).deleteAppointment(vaccination)
            }
        }
    }

    private suspend fun getDoctorId(doctorName: String, doctorSurname: String): Int {
        return withContext(Dispatchers.IO) {
            var doctorId = 0
            try {
                val connection = DBconnection.getConnection()
                val doctorQuery = DoctorsQueries(connection)
                doctorId = doctorQuery.getDoctorId(doctorName, doctorSurname) ?: 0
                connection.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            doctorId
        }
    }

    override fun getItemCount(): Int {
        return vaccinationHistory.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun isDateValid(date: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd")
        format.isLenient = false
        return try {
            format.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isTimeValid(time: String): Boolean {
        val format = SimpleDateFormat("HH:mm")
        format.isLenient = false
        return try {
            format.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }
}