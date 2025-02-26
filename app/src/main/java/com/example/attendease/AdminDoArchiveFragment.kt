package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class AdminDoArchiveFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_admin_do_archive, container, false)

        val db = FirebaseFirestore.getInstance()

        val yearSpinner = view.findViewById<Spinner>(R.id.selectyearA)
        val button = view.findViewById<CardView>(R.id.admindoarchive)

        val academicYears = mutableListOf<String>()
        val yearId = mutableListOf<String>()
        val sessionsList= mutableListOf<String>()


        db.collection("Academic Years")
            .orderBy("year", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                academicYears.clear()
                yearId.clear()
                academicYears.add("Year")
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


        button.setOnClickListener {
            val selectedYear = yearSpinner.selectedItem.toString()
            if (yearSpinner.selectedItemPosition == 0 || selectedYear == "Year") {
                Toast.makeText(requireContext(), "No Academic Year Selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            // get id of selected year
            val academic=findString(selectedYear,academicYears,yearId)

        //    Toast.makeText(requireContext()," $academic $selectedYear",Toast.LENGTH_SHORT).show()
            val studentAr="Students $selectedYear"
            val studentMaterialAr="Students Materials $selectedYear"
            val sessionsAr="Sessions $selectedYear"
            val attendAr="Attendances $selectedYear"
            sessionsList.clear()

            db.collection("Students")
                .whereEqualTo("Academic Year",academic)
                .get()
                .addOnSuccessListener {students->
                    for(student in students){
                        val departmentAr=student.getString("Department")
                        val emailAr=student.getString("Email")
                        val passwordAr=student.getString("Password")
                        val nameAr=student.getString("Name")
                        val idAr=student.id
                        val languageAr=student.getString("Language")

                        val data = hashMapOf(
                            "Id" to idAr,
                            "Name" to nameAr,
                            "Department" to departmentAr,
                            "Language"  to languageAr,
                            "Email" to emailAr,
                            "Password" to passwordAr
                        )
                        db.collection(studentAr)
                            .add(data)
                        db.collection("Students").document(student.id).delete()
                    }

                }
                Toast.makeText(view.context,"Students were moved to archive",Toast.LENGTH_SHORT).show()

            db.collection("Students Materials")
                .whereEqualTo("Academic Year Id",academic)
                .get()
                .addOnSuccessListener {studentsmaterials->
                    for(studentM in studentsmaterials){
                        val materialId=studentM.getString("Material Id")
                        val studentId=studentM.getString("Student Id")

                        val data = hashMapOf(

                            "Material Id" to materialId,
                            "Student Id" to studentId
                        )
                        db.collection(studentMaterialAr)
                            .add(data)

                        db.collection("Students Materials").document(studentM.id).delete()

                    }

                }
            Toast.makeText(view.context,"Student's materials were moved to archive",Toast.LENGTH_SHORT).show()



            db.collection("Sessions")
                .whereEqualTo("Academic Year",academic)
                .get()
                .addOnSuccessListener {sessions->
                    for(session in sessions){
                        val materialId=session.getString("Material Id")
                        val drId=session.getString("Doctor Id")
                        val startTime=session.getString("Start Time")
                        val endTime=session.getString("End Time")
                        val date=session.getString("Date")
                        val location=session.getString("Location")
                        val sessionIdd=session.id
                        sessionsList.add(sessionIdd)

                        val data = hashMapOf(

                            "Material Id" to materialId,
                            "Date" to date,
                            "Start Time" to startTime,
                            "End Time" to endTime,
                            "Location" to location,
                            "Doctor Id" to drId,
                            "Session Id" to session.id

                        )
                        db.collection(sessionsAr)
                            .add(data).addOnSuccessListener { documentSnapshot ->
                                val documentId = documentSnapshot.id





                                db.collection("Attendances").whereEqualTo("Session Id", sessionIdd)
                                    .get()
                                    .addOnSuccessListener { attends ->
                                        for (attend in attends) {
                                            val sId = attend.getString("Session Id")
                                            if (sessionsList.contains(sId)) {
                                                val stdId = attend.getString("Student Id")

                                                val data = hashMapOf(

                                                    "Student Id" to stdId,
                                                    "Session Id" to sessionIdd
                                                )
                                                db.collection(attendAr)
                                                    .add(data)
                                                db.collection("Attendances").document(attend.id).delete()
                                            }

                                        }

                                    }
                                db.collection("Sessions").document(sessionIdd).delete()


                            }
                    }

                }
            Toast.makeText(view.context,"Sessions and attendances were moved to archive",Toast.LENGTH_SHORT).show()


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