package com.example.attendease

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.QuerySnapshot

class AdminLoginFragment : Fragment() {

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_login, container, false)

        val loginButton = view.findViewById<CardView>(R.id.adminbtnlog)
        val emailEditText = view.findViewById<EditText>(R.id.adminemail)
        val passwordEditText = view.findViewById<EditText>(R.id.adminpassword)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Input validation
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(view.context, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(view.context, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("Admins")
                .whereEqualTo("Admin Email", email)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.isEmpty) {
                        Toast.makeText(view.context, "Wrong Email", Toast.LENGTH_SHORT).show()
                    } else {
                        val adminDoc = documentSnapshot.documents[0]
                        val storedPassword = adminDoc.getString("Admin Password")

                        if (password == storedPassword) {
                            view.findNavController().navigate(R.id.action_adminLoginFragment_to_adminFunctionsFragment)
                        } else {
                            Toast.makeText(view.context, "Wrong Password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(view.context, "Login failed: $exception", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

}