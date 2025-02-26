package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class DrMaterialFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    val lang = mutableListOf<String>()
    val langId = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_dr_material, container, false)


        val DoctorId = DrMaterialFragmentArgs.fromBundle(requireArguments()).doctorId
        val SelectedSem = DrMaterialFragmentArgs.fromBundle(requireArguments()).selectedSem


        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)

        //Toast.makeText(view.context,"$DoctorId , $SelectedSem", Toast.LENGTH_LONG).show()

        var materialId = " "
        // Fetch materials related to the doctor
        val materialsId = ArrayList<String>()
        db.collection("Doctors Materials")
            .whereEqualTo("Doctor Id", DoctorId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val materialId = document.getString("Material Id")
                    if (materialId != null) {
                        materialsId.add(materialId)
                    }
                }
               //Toast.makeText(requireContext(), " $materialsId", Toast.LENGTH_SHORT).show()
            }

        db.collection("Languages")
            .orderBy("Language Name")
            .addSnapshotListener { result, error ->
                if(result != null) {
                    lang.clear()
                    langId.clear()
                    for (document in result.documents) {
                        val langName =
                            document.getString("Language Name") // get the language name
                        val id = document.id // get the document id

                        if (langName != null) {
                            lang.add(langName) // add the department name to the departments list
                            langId.add(id) // add the id to the deptId list
                        }
                    }

                }
            }

        db.collection("Materials")
                    .orderBy("Material Name")
                    .whereEqualTo("Semester", SelectedSem)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {

                                if (materialsId.contains(document.id)) {
                                    val materialName =
                                        document.getString("Material Name").toString()
                                    val materialCode =
                                        document.getString("Material Code").toString()
                                    val materialLanguage =
                                        document.getString("Language").toString()
                                    val langIndex =langId.indexOf(materialLanguage)

                                    val name = lang[langIndex]

                                    val cardView = context?.let {
                                        CardView(it).apply {
                                            layoutParams = LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            ).apply {
                                                topMargin = 16
                                                bottomMargin = 16
                                            }
                                            radius = 15F
                                            setCardBackgroundColor(
                                                ContextCompat.getColor(
                                                    context,
                                                    R.color.white
                                                )
                                            )
                                            elevation = 10F

                                            addView(TextView(context).apply {
                                                layoutParams = LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.MATCH_PARENT
                                                )
                                                text = "$materialName $materialCode $name"
                                                textSize = 20F
                                                gravity = Gravity.CENTER
                                                setTextColor(
                                                    ContextCompat.getColor(
                                                        context,
                                                        R.color.red
                                                    )
                                                )
                                            })
                                            setOnClickListener {
                                                val action =
                                                    DrMaterialFragmentDirections.actionDrMaterialFragmentToDrFunctionsFragment(
                                                        DoctorId,
                                                        document.id
                                                    )
                                                view.findNavController().navigate(action)
                                            }


                                        }
                                    }

                                    linearLayout.addView(cardView)


                                }

                            }
                        }




        return view
    }
}
