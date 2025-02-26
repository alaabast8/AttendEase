package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DrAddAttendanceFragment : Fragment() {

    val attStdId = mutableListOf<String>()
    val attSessionId = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dr_add_attendance, container, false)
        val materialId = DrAddAttendanceFragmentArgs.fromBundle(requireArguments()).materialId
        val sessionId = DrAddAttendanceFragmentArgs.fromBundle(requireArguments()).sessionId

      //  Toast.makeText(requireContext(), "$materialId $sessionId", Toast.LENGTH_SHORT).show()

        val add = view.findViewById<CardView>(R.id.addAttend)
        val stdIdList = mutableListOf<String>()

        val db = FirebaseFirestore.getInstance()
        val linearLayout = view.findViewById<LinearLayout>(R.id.checkboxes)

        // Retrieve all documents
        db.collection("Attendances").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(view.context, "Failed to retrieve Attendances, please try again", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                attSessionId.clear()
                attStdId.clear()
                for (document in snapshot.documents) {
                    val sesionId = document.getString("Session Id")
                    val stdId = document.getString("Student Id")

                    if (stdId != null && sesionId != null) {
                        attSessionId.add(sesionId)
                        attStdId.add(stdId)
                    }
                }
            }
        }

        add.setOnClickListener {
            val checkedIds = mutableListOf<String>()
            for (i in 0 until linearLayout.childCount) {
                val child = linearLayout.getChildAt(i)
                if (child is CheckBox && child.isChecked) {
                    val StudentId = child.tag.toString()
                    checkedIds.add(StudentId)

                    if (!checkAttendanceExists(StudentId, sessionId)) {
                        val AttendancesRef = db.collection("Attendances").document()
                        val attendanceData = hashMapOf(
                            "Student Id" to StudentId,
                            "Session Id" to sessionId
                        )
                        AttendancesRef.set(attendanceData)
                            .addOnSuccessListener {
                                //Toast.makeText(requireContext(), "done", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            for (i in 0 until linearLayout.childCount) {
                val child = linearLayout.getChildAt(i)
                if (child is CheckBox && child.isChecked) {
                    child.isChecked = false
                }
            }
        }

        db.collection("Students Materials")
            .whereEqualTo("Material Id", materialId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents.documents) {
                    val id = document.getString("Student Id")
                    if (id != null) {
                        stdIdList.add(id) // list if std id who has this material
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    fetchStudents(db, linearLayout, stdIdList)
                }
            }

        return view
    }
/*
    private suspend fun fetchStudents(db: FirebaseFirestore, linearLayout: LinearLayout, stdIdList: List<String>)
    {
        withContext(Dispatchers.IO) {
            val studentsQuery = db.collection("Students").whereIn(FieldPath.documentId(), stdIdList).get().await()

            withContext(Dispatchers.Main) {
                for (document in studentsQuery.documents) {
                    val id = document.id
                    val name = document.getString("Name")

                    // Create a new CheckBox for each student
                    val checkBox = CheckBox(requireContext())
                    checkBox.text = name
                    checkBox.tag = id
                    checkBox.setTextColor(Color.parseColor("#000000"))
                    // Add the CheckBox to the LinearLayout
                    linearLayout.addView(checkBox)
                }
            }
        }
    }
    */

    private suspend fun fetchStudents(db: FirebaseFirestore, linearLayout: LinearLayout, stdIdList: List<String>) {
        withContext(Dispatchers.IO) {
            val studentsQuery = db.collection("Students")
                .orderBy("Name")
                .whereIn(FieldPath.documentId(), stdIdList).get().await()

            val retrievedStudentsCount = studentsQuery.size()  // Track retrieved students

            withContext(Dispatchers.Main) {
                var checkboxesCreated = 0  // Track created checkboxes

                for (document in studentsQuery.documents) {
                    val id = document.id
                    val name = document.getString("Name")

                    if (name != null) {  // Check for missing name
                        val checkBox = CheckBox(requireContext())
                        checkBox.text = name
                        checkBox.tag = id
                        checkBox.setTextColor(Color.parseColor("#000000"))

//                        val attendanceExists = checkAttendanceExists(id, sessionId)
//                        checkBox.isChecked = !attendanceExists

                        linearLayout.addView(checkBox)
                        checkboxesCreated++
                    } else {
                        Toast.makeText(requireContext() , "a name is null",Toast.LENGTH_SHORT).show()
                    }
                }

                // Check for discrepancies between retrieved students and checkboxes
//                if (retrievedStudentsCount != checkboxesCreated) {
//                   // Log.w("Student Display", "Retrieved $retrievedStudentsCount students, displayed $checkboxesCreated checkboxes")
//                }
            }
        }
    }

    private fun checkAttendanceExists(std: String, session: String): Boolean {
        for (i in attStdId.indices) {
            if (attStdId[i] == std) {
                val sessionId = attSessionId[i]
                if (sessionId == session)
                    return true
            }
        }
        return false
    }
}
