package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DrCreateSessionFragment : Fragment() {

    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dr_create_session, container, false)

        val drId=DrFunctionsFragmentArgs.fromBundle(requireArguments()).drId
        val materialId=DrFunctionsFragmentArgs.fromBundle(requireArguments()).materialId


        val academicYears = mutableListOf<String>()
        val yearId = mutableListOf<String>()

        val startDatePicker = view.findViewById<DatePicker>(R.id.start_date_picker)
        val startTimePicker = view.findViewById<TimePicker>(R.id.start_time_picker)
        val endTimePicker = view.findViewById<TimePicker>(R.id.end_time_picker)
        val locationGroup = view.findViewById<RadioGroup>(R.id.location)
        val addSessionButton = view.findViewById<CardView>(R.id.addSession)
        val yearSpinner = view.findViewById<Spinner>(R.id.selectyearA)

        db.collection("Academic Years")
            .orderBy("year", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                academicYears.clear()
                yearId.clear()
                academicYears.add("Academic Year")
                yearId.add("0")
                for (document in result) {
                    val yearr = document.getString("year")
                    val id = document.id

                    if (yearr != null) {
                        academicYears.add(yearr)
                        yearId.add(id)
                    }

                }

                // Add the string to the spinner
                val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, academicYears)
                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                yearSpinner.adapter = yearAdapter

            }

        addSessionButton.setOnClickListener {
            // Check each view separately
            val selectedYear = yearSpinner.selectedItem.toString()
            if (startDatePicker == null) {
                Toast.makeText(context, "Please select a start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (startTimePicker == null) {
                Toast.makeText(context, "Please select a start time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }  else if (endTimePicker == null) {
                Toast.makeText(context, "Please select an end time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (locationGroup.checkedRadioButtonId == -1) {
                Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (yearSpinner.selectedItemPosition == 0 || selectedYear == "Academic Year") {
                Toast.makeText(requireContext(), "No Academic Year Selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }else {
                // Get the selected location
                val selectedLocationId = locationGroup.checkedRadioButtonId
                val selectedLocation = view.findViewById<RadioButton>(selectedLocationId).text

                val academic=findString(selectedYear,academicYears,yearId)


                val startTime = "${startTimePicker.hour}:${startTimePicker.minute}"
                val endTime = "${endTimePicker.hour}:${endTimePicker.minute}"
                val DateSel = "${startDatePicker.dayOfMonth}/${startDatePicker.month + 1}/${startDatePicker.year}"


                val newSessionRef = db.collection("Sessions").document()

                val sessionId = newSessionRef.id
                val newSession = hashMapOf(
                    "Doctor Id" to drId,
                    "Material Id" to materialId,
                    "Location" to selectedLocation,
                    "Date" to DateSel,
                    "Start Time" to startTime,
                    "End Time" to endTime,
                    "Academic Year" to academic
                )

                newSessionRef.set(newSession)
                    .addOnSuccessListener {
                        Toast.makeText(view.context,"session successfully added",Toast.LENGTH_SHORT).show()
                        val action=DrCreateSessionFragmentDirections.actionDrCreateSessionFragmentToDrAddAttendanceFragment(materialId,sessionId)
                        view.findNavController().navigate(action)
                    }


            }
        }

        return view
    }

    fun findString(input: String, array1: MutableList<String>, array2: MutableList<String>): String? {
        val index = array1.indexOf(input)
        return if (index != -1 && index < array2.size) {
            array2[index]
        } else {
            null
        }
    }
}