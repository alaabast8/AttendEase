package com.example.attendease

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DrLoginFragment : Fragment() {

    private val db = Firebase.firestore



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_dr_login, container, false)


        val log=view.findViewById<CardView>(R.id.drbtnlog)
        val drEmail = view.findViewById<TextView>(R.id.dremail)
        val drPassword = view.findViewById<TextView>(R.id.drpassword)


        log.setOnClickListener {
            val email = drEmail.text.toString().trim()
            val password = drPassword.text.toString().trim()

            // Input validation
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(view.context, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(view.context, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("Doctors")
                .whereEqualTo("Doctor Email", email)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.isEmpty) {
                        Toast.makeText(view.context, "Wrong Email", Toast.LENGTH_SHORT).show()
                    } else {
                        val drDoc = documentSnapshot.documents[0]
                        val storedPassword = drDoc.getString("Doctor Password")
                        val storedId=drDoc.id

                        if (password == storedPassword) {
                            val action=DrLoginFragmentDirections.actionDrLoginFragmentToDrSelectSemFragment(storedId)
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