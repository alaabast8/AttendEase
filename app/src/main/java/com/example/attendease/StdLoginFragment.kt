package com.example.attendease

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class StdLoginFragment : Fragment() {

    private val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_std_login, container, false)

        val login=view.findViewById<CardView>(R.id.stdbtnlog)

        val stdemail = view.findViewById<TextView>(R.id.stdemail)
        val stdpassword = view.findViewById<TextView>(R.id.stdpassword)



        login.setOnClickListener {
            val email = stdemail.text.toString().trim()
            val password = stdpassword.text.toString().trim()

            // Input validation
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(view.context, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(view.context, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("Students")
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.isEmpty) {
                        Toast.makeText(view.context, "Wrong Email", Toast.LENGTH_SHORT).show()
                    } else {
                        val stdDoc = documentSnapshot.documents[0]
                        val storedPassword = stdDoc.getString("Password")
                        val storedId=stdDoc.getString("ID").toString()

                        if (password == storedPassword) {
                            val action=StdLoginFragmentDirections.actionStdLoginFragmentToStdSelectSemFragment(storedId)
                            view.findNavController().navigate(action)
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