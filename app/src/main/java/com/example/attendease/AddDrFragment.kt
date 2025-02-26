package com.example.attendease

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AddDrFragment : Fragment() {
   private val db = Firebase.firestore

    // to retrieve doctors email from database  used to check if exists
    private val doctorEmails = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_dr, container, false)

        val addDrBtn = view.findViewById<CardView>(R.id.BtnAddDr)
        val show=view.findViewById<Switch>(R.id.showdrs)
        val allDr = view.findViewById<TextView>(R.id.AllDr)
        val drName = view.findViewById<TextView>(R.id.DrName)
        val drEmail = view.findViewById<TextView>(R.id.DrEmail)
        val drPassword = view.findViewById<TextView>(R.id.DrPassword)


        var newDocumentId: Long
        newDocumentId = 0L
        db.collection("Doctors").document("DoctorIdCounter").get()
            .addOnSuccessListener {  documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val newDocumentIdString = documentSnapshot.data?.get("DrCount")?.toString() ?: ""
                    newDocumentId = newDocumentIdString.toLong()
                }
            }
// to store dr emails
        var doctorsEmailString :String

        // Reference to the Doctors collection
        val doctorsRef = db.collection("Doctors").orderBy("Doctor Name")

        // Retrieve all documents
        doctorsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(view.context,"failed to retrieve doctors emails , plz try again",Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                doctorEmails.clear() // Clear existing data before update
                doctorsEmailString = ""
                for (document in snapshot.documents) {
                    val email = document.getString("Doctor Email")
                    val name = document.getString("Doctor Name")
                   // val count = document.getString("DrCount")
                    if (email != null) {
                       // Toast.makeText(view.context,"$email",Toast.LENGTH_SHORT).show()
                        doctorEmails.add(email)
                        doctorsEmailString+= "$name: $email \n"
                    }
                   // if(count != null)
                    //    newDocumentId = count.toLong()

                }
                show.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        allDr.text=doctorsEmailString
                    } else {
                        allDr.text=""
                    }
                }


            }
        }

        // add dr to the database
        addDrBtn.setOnClickListener{


            if(drName.text.isNotEmpty() && isValidEmail(drEmail) && isValidPass(drPassword) &&
                !checkEmailExists(drEmail.text.toString())) {
                // create new dr
                val doctor = hashMapOf(
                    "Doctor Email" to drEmail.text.toString().trim(),
                    "Doctor Name" to drName.text.toString().trim().capitalize(),
                    "Doctor Password" to drPassword.text.toString().trim(),
                )
                // Add a new document with a generated ID
                newDocumentId += 1 // new id

                // Add a new document (new doctor)
                db.collection("Doctors").document(newDocumentId.toString()).set(doctor)
                    .addOnSuccessListener {
                        drName.text =""
                        drEmail.text =""
                        drPassword.text =""
                        // update the nb of doctors
                        db.collection("Doctors").document("DoctorIdCounter")
                            .set(hashMapOf("DrCount" to newDocumentId))
                            .addOnSuccessListener {
                               // Toast.makeText(view.context, "Dr count is inc", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                Toast.makeText(view.context, "failed to inc count", Toast.LENGTH_SHORT).show()

                            }

                        Toast.makeText(view.context, "Dr is successfully added", Toast.LENGTH_SHORT).show()
                        val action = AddDrFragmentDirections.actionAddDrFragmentToAddDrMaterialFragment(newDocumentId.toString())
                        view.findNavController().navigate(action)
                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(view.context, "Failed to add the Dr", Toast.LENGTH_SHORT).show()
                    }
            }//end if
            else{
               if(drName.text.isEmpty()) Toast.makeText(view.context,"Dr Name is required",Toast.LENGTH_SHORT).show()
                if(! isValidEmail(drEmail)) Toast.makeText(view.context,"Invalid Email",Toast.LENGTH_SHORT).show()
                if(!isValidPass(drPassword)) Toast.makeText(view.context,"Password should be at least 6 characters",Toast.LENGTH_SHORT).show()
                if(checkEmailExists(drEmail.text.toString())) Toast.makeText(view.context,"this Dr already exists",Toast.LENGTH_SHORT).show()

            }
        }// end the btn on click listener







        return view
    }
    // Function to check if entered email exists
    private fun checkEmailExists(email: String): Boolean {
        return doctorEmails.contains(email)
    }

    private fun isValidEmail(drEmail:TextView):Boolean{
        return drEmail.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(drEmail.text).matches()
    }
    private fun isValidPass(drPass:TextView):Boolean{
        return  drPass.text.isNotEmpty() && drPass.text.length>=6
    }


}