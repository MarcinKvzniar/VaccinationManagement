package com.example.vaccinationmanagement.activities.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
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

/**
 * HistoryAdapter is a RecyclerView.Adapter subclass that displays a list of vaccination history details.
 * @property vaccinationHistory The list of vaccination history details to be displayed.
 */
class HistoryAdapter(private val vaccinationHistory: List<VaccinationDetail>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val textSize = 14f

    /**
     * ViewHolder is a RecyclerView.ViewHolder subclass that represents a single item in the list.
     * @property itemView The root view of the ViewHolder.
     */
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

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaccination = vaccinationHistory[position]
        holder.textVaccineName.text = vaccination.vaccineName
        holder.textDate.text = vaccination.date.toString()
        holder.textTime.text = vaccination.time.toString()
        holder.textAddress.text = vaccination.address
        holder.textDoctor.text = "Doctor: ${vaccination.doctorName} ${vaccination.doctorSurname}"
        holder.textDose.text = "Dose: ${vaccination.dose}"

        var lastTouchDown = 0L

        // Updating the date of the vaccination
        holder.textDate.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTouchDown < ViewConfiguration.getDoubleTapTimeout()) {
                    val editText = EditText(v.context)
                    editText.setText((v as TextView).text)
                    editText.textSize = textSize
                    editText.layoutParams = v.layoutParams
                    (v.parent as ViewGroup).addView(editText)
                    v.visibility = View.GONE

                    editText.setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            if (isDateValid(editText.text.toString())) {
                                v.text = editText.text
                            } else {
                                Toast.makeText(v.context, "Invalid date format", Toast.LENGTH_SHORT).show()
                            }
                            v.visibility = View.VISIBLE
                            (v.parent as ViewGroup).removeView(editText)
                        }
                    }
                }
                lastTouchDown = currentTime
            }
            true
        }

        // Updating the time of the vaccination
        holder.textTime.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTouchDown < ViewConfiguration.getDoubleTapTimeout()) {
                    val editText = EditText(v.context)
                    editText.setText((v as TextView).text)
                    editText.textSize = textSize
                    editText.layoutParams = v.layoutParams
                    (v.parent as ViewGroup).addView(editText)
                    v.visibility = View.GONE

                    editText.setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            if (isTimeValid(editText.text.toString())) {
                                v.text = editText.text
                            } else {
                                Toast.makeText(v.context, "Invalid time format", Toast.LENGTH_SHORT).show()
                            }
                            v.visibility = View.VISIBLE
                            (v.parent as ViewGroup).removeView(editText)
                        }
                    }
                }
                lastTouchDown = currentTime
            }
            true
        }

        // Updating the address of the vaccination
        holder.textAddress.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTouchDown < ViewConfiguration.getDoubleTapTimeout()) {
                    val editText = EditText(v.context)
                    editText.setText((v as TextView).text)
                    editText.textSize = textSize
                    editText.layoutParams = v.layoutParams
                    (v.parent as ViewGroup).addView(editText)
                    v.visibility = View.GONE

                    editText.setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            v.text = editText.text
                            v.visibility = View.VISIBLE
                            (v.parent as ViewGroup).removeView(editText)
                        }
                    }
                }
                lastTouchDown = currentTime
            }
            true
        }

        holder.btnUpdate.setOnClickListener {
            val editTextDate = (holder.textDate.parent as ViewGroup).children.find { it is EditText }
            val editTextTime = (holder.textTime.parent as ViewGroup).children.find { it is EditText }
            val editTextAddress = (holder.textAddress.parent as ViewGroup).children.find { it is EditText }

            editTextDate?.clearFocus()
            editTextTime?.clearFocus()
            editTextAddress?.clearFocus()

            (holder.itemView.context as HistoryActivity).lifecycleScope.launch {
                val doctorId = getDoctorId(vaccination.doctorName, vaccination.doctorSurname)
                val updatedAppointment = Appointments(
                    id = vaccination.id,
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return vaccinationHistory.size
    }

    /**
     * Checks if a date string is valid.
     * @param date The date string to be checked.
     * @return Boolean indicating whether the date string is valid.
     */
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

    /**
     * Checks if a time string is valid.
     * @param time The time string to be checked.
     * @return Boolean indicating whether the time string is valid.
     */
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