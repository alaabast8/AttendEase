package com.example.attendease

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AddYearFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_year, container, false)

        firestore = FirebaseFirestore.getInstance()

        val yearEditText = view.findViewById<EditText>(R.id.academicyear)
        val addYearButton = view.findViewById<CardView>(R.id.addyear)
        val allYearsTextView = view.findViewById<TextView>(R.id.allyears)

        getAllYears(allYearsTextView) // call the function in onCreateView

        addYearButton.setOnClickListener {
            val year = yearEditText.text.toString() // get the year from the EditText
            if (year.matches("\\d{4}_\\d{4}".toRegex())) { // check if the year is in the format YYYY_YYYY
                val id = firestore.collection("Academic Years").document().id // Generate a unique id
                firestore.collection("Academic Years").document(id).set(hashMapOf("year" to year))
                yearEditText.text.clear()
                getAllYears(allYearsTextView) // call the function after adding a year
            } else {
                Toast.makeText(context, "Please enter the year in the format YYYY_YYYY", Toast.LENGTH_LONG).show()
                yearEditText.text.clear() // clear the EditText
            }
        }

        return view
    }

    private fun getAllYears(allYearsTextView: TextView) {
        //addOnSuccessListener  will be called if the operation completes successfully
        firestore.collection("Academic Years").orderBy("year", Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            allYearsTextView.text = "" // clear the text view before appending new data
            for (document in documents) {
                val yearr = document.getString("year")
                allYearsTextView.append(yearr+ "\n")
            }
        }
    }
}