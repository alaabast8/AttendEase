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

class AddDeptFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_dept, container, false)

        firestore = FirebaseFirestore.getInstance()

        val deptNameEditText = view.findViewById<EditText>(R.id.deptName)
        val addDeptButton = view.findViewById<CardView>(R.id.adddept)
        val allDeptTextView = view.findViewById<TextView>(R.id.alldept)


        getAllDepartments(allDeptTextView) // call the function in onCreateView

        addDeptButton.setOnClickListener {
            val deptName = deptNameEditText.text.toString().lowercase()
            if(deptName != "") {
                val id = firestore.collection("Departments").document().id // Generate a unique id
                firestore.collection("Departments").document(id)
                    .set(hashMapOf("deptName" to deptName))
                deptNameEditText.text.clear()
                getAllDepartments(allDeptTextView)
            }
            else
                Toast.makeText(view.context , "Department Name is required",Toast.LENGTH_SHORT).show()
        }




        return view
    }

    private fun getAllDepartments(allDeptTextView: TextView) {
        firestore.collection("Departments").orderBy("deptName")
            .get()
            .addOnSuccessListener { documents ->
            allDeptTextView.text = "" // clear the text view before appending new data
            for (document in documents) {
                val deptName = document.getString("deptName") // get the department name
                allDeptTextView.append(deptName + "\n")
            }
        }
    }

}