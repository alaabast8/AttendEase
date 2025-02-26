package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AdminCheckArchiveDetailsFragment : Fragment() {
    lateinit var year:String
    val drIdList= mutableListOf<String>()
    val drNameList= mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_admin_check_archive_details, container, false)

        val materialId = AdminCheckArchiveDetailsFragmentArgs.fromBundle(requireArguments()).materialid
        year = AdminCheckArchiveDetailsFragmentArgs.fromBundle(requireArguments()).year


        //Toast.makeText(view.context,"$year", Toast.LENGTH_SHORT).show()


        val db = Firebase.firestore
        val linearr = view.findViewById<LinearLayout>(R.id.linearrAr)
        db.collection("Doctors")
            .get()
            .addOnSuccessListener {doctors->
                for(doctor in doctors){
                    val drId=doctor.id
                    val drName=doctor.getString("Doctor Name")
                    drIdList.add(drId)
                    if(drName!=null) {
                        drNameList.add(drName)
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    fetchSessions(db, linearr, materialId)
                }
            }





        return view
    }

    private suspend fun fetchSessions(db: FirebaseFirestore, linearr: LinearLayout, materialId: String) = withContext(Dispatchers.IO) {
        val sessions = db.collection("Sessions $year")
            .whereEqualTo("Material Id", materialId)
            .get()
            .await()

        for (session in sessions) {
            val date = session.getString("Date")
            val startTime = session.getString("Start Time")
            val endTime = session.getString("End Time")
            val drId=session.getString("Doctor Id")
            val location=session.getString("Location")
            val sessionId = session.getString("Session Id")

            val name=findString(drId.toString(),drIdList,drNameList)

            withContext(Dispatchers.Main) {
                val textView1 = TextView(requireContext())
                textView1.text = "Date: $date By: $name"
                textView1.textSize = 23f
                textView1.setTextColor(Color.RED)
                textView1.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                linearr.addView(textView1)

                val textView2 = TextView(requireContext())
                textView2.text = "Start Time: $startTime - End Time: $endTime \n Location: $location \n"
                textView2.textSize = 20f
                textView2.setTextColor(Color.RED)
                textView2.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                linearr.addView(textView2)
            }

            fetchAttendances(db, linearr, sessionId.toString())
        }
    }

    private suspend fun fetchAttendances(db: FirebaseFirestore, linearr: LinearLayout, sessionId: String) = withContext(Dispatchers.IO) {
        val attends = db.collection("Attendances $year")
            .whereEqualTo("Session Id", sessionId)
            .get()
            .await()

        for (attend in attends) {
            val studentId = attend.getString("Student Id")
            val documentSnapshot = db.collection("Students $year")
                .whereEqualTo("Id" , studentId)
                .get()
                .await()
            for(std in documentSnapshot) {
                val studentName = std.getString("Name")

                withContext(Dispatchers.Main) {
                    val textView3 = TextView(requireContext())
                    textView3.text = "$studentId - $studentName"
                    textView3.textSize = 18f
                    textView3.setTextColor(Color.BLACK)
                    textView3.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    linearr.addView(textView3)
                }
            }
        }
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