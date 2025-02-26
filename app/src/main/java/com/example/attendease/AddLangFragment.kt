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

class AddLangFragment : Fragment() {
    private val LanguagesName = mutableListOf<String>()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_lang, container, false)
        val LangName = view.findViewById<TextView>(R.id.langName)
        val AddLang = view.findViewById<CardView>(R.id.addLang)
        val allLang = view.findViewById<TextView>(R.id.AllLang)

        // to store dr emails
        var LangNameString :String

        // Reference to the Doctors collection
        val languagesRef = db.collection("Languages").orderBy("Language Name")

        // Retrieve all documents
        languagesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(view.context,"failed to retrieve languages",
                    Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                LanguagesName.clear() // Clear existing data before update
                LangNameString = ""
                for (document in snapshot.documents) {
                    val name = document.getString("Language Name")
                    if (name != null) {
                        // Toast.makeText(view.context,"$email",Toast.LENGTH_SHORT).show()
                        LanguagesName.add(name)
                        LangNameString+= "$name \n"
                    }
                }
                allLang.text=LangNameString
            }
        }

        // add dr to the database
        AddLang.setOnClickListener{

            if(LangName.text.isNotEmpty() && !checkLangExists(LangName.text.toString().lowercase().trim())) {
                // create new dr
                val lang = hashMapOf(
                    "Language Name" to LangName.text.toString().lowercase().trim()
                     )

                // Add a new Language
                db.collection("Languages").document().set(lang)
                    .addOnSuccessListener {
                        LangName.text =""
                        Toast.makeText(view.context, "Language is successfully added", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(view.context, "Failed to add the Language", Toast.LENGTH_SHORT).show()
                    }
            }//end if
            else{
                if(LangName.text.isEmpty()) Toast.makeText(view.context,"Language Name is required",Toast.LENGTH_SHORT).show()
                if(checkLangExists(LangName.text.toString().lowercase().trim())) Toast.makeText(view.context,"this Language already exists",Toast.LENGTH_SHORT).show()

            }
        }// end the btn on click listener



        return view
    }

    // Function to check if entered email exists
    private fun checkLangExists(name: String): Boolean {
        return LanguagesName.contains(name)
    }

}