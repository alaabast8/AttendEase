package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.findFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddSemFragment : Fragment() {
    private val SemestersName = mutableListOf<String>()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_sem, container, false)
        val SemName = view.findViewById<TextView>(R.id.SemName)
        val AddSem = view.findViewById<CardView>(R.id.addSem)
        val allSem = view.findViewById<TextView>(R.id.AllSem)

        // to store dr emails
        var SemNameString :String

        // Reference to the Doctors collection
        val SemestersRef = db.collection("Semesters").orderBy("Semester Name")



        // Retrieve all documents
        SemestersRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(view.context,"failed to retrieve semesters",
                    Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                SemestersName.clear() // Clear existing data before update
                SemNameString = ""
                for (document in snapshot.documents) {
                    val name = document.getString("Semester Name")
                    if (name != null) {
                        // Toast.makeText(view.context,"$email",Toast.LENGTH_SHORT).show()
                        SemestersName.add(name)
                        SemNameString+= "$name \n"
                    }
                }
                allSem.text=SemNameString
            }
        }

        // add dr to the database
        AddSem.setOnClickListener{

            if(SemName.text.isNotEmpty() && !checkSemExists(SemName.text.toString().lowercase().trim())
                &&  SemName.text.toString().lowercase().trim().matches("semester_\\d{1}".toRegex())) {
                // create new dr
                val lang = hashMapOf(
                    "Semester Name" to SemName.text.toString().lowercase().trim()
                )

                // Add a new Language
                db.collection("Semesters").document().set(lang)
                    .addOnSuccessListener {
                        SemName.text =""
                        Toast.makeText(view.context, "Semester is successfully added", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(view.context, "Failed to add the Semester", Toast.LENGTH_SHORT).show()
                    }
            }//end if
            else{
                if(SemName.text.isEmpty())
                    Toast.makeText(view.context,"Semester Name is required",Toast.LENGTH_SHORT).show()
                if(checkSemExists(SemName.text.toString().lowercase().trim()))
                    Toast.makeText(view.context,"this semester already exists",Toast.LENGTH_SHORT).show()
                if(! SemName.text.toString().lowercase().trim().matches("semester_\\d{1}".toRegex()))
                    Toast.makeText(view.context,"Please enter the year in the format semester_n",Toast.LENGTH_SHORT).show()
            }
        }// end the btn on click listener








        return view
    }

    // Function to check if entered email exists
    private fun checkSemExists(name: String): Boolean {
        return SemestersName.contains(name)
    }

}