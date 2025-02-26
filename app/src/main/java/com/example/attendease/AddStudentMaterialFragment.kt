package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Color

class AddStudentMaterialFragment : Fragment() {

    val materialsList = mutableListOf<String>()
    val materialsId = mutableListOf<String>()

    // to store std id from Students Materials collection
    val stdmat_StdId = mutableListOf<String>()
    val stdmat_MatId = mutableListOf<String>()
    val stdmat_YearId = mutableListOf<String>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_student_material, container, false)


        val StudentId = AddStudentMaterialFragmentArgs.fromBundle(requireArguments()).studentId
        val Language = AddStudentMaterialFragmentArgs.fromBundle(requireArguments()).language
        val Department = AddStudentMaterialFragmentArgs.fromBundle(requireArguments()).department
        val yearId = AddStudentMaterialFragmentArgs.fromBundle(requireArguments()).academicYearId


        val db = FirebaseFirestore.getInstance()
        // Reference to the Doctors collection
        val stdMatRef = db.collection("Students Materials")

        // Retrieve all documents
        stdMatRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(view.context,"failed to retrieve students materials, plz try again",Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                stdmat_MatId.clear() // Clear existing data before update
                stdmat_StdId.clear()
                for (document in snapshot.documents) {
                    val stdId = document.getString("Student Id")
                    val matId = document.getString("Material Id")
                    val acYearId = document.getString("Academic Year Id")

                    // val count = document.getString("DrCount")
                    if (stdId != null && matId != null && acYearId != null) {
                        // Toast.makeText(view.context,"$email",Toast.LENGTH_SHORT).show()
                        stdmat_MatId.add(matId)
                        stdmat_StdId.add(stdId)
                        stdmat_YearId.add(acYearId)

                     }
                }
            }
        }

        val materialsCollection = db.collection("Materials").orderBy("Material Name")
        val query = materialsCollection
            .whereEqualTo("Department", Department)
            .whereEqualTo("Language", Language)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                materialsList.clear()
                materialsId.clear()
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val code = document.getString("Material Code")
                    val name = document.getString("Material Name")
                    val semester = document.getString("Semester")

                    if (name != null && id != null) {
                        materialsList.add(name)
                        materialsId.add(id)
                    }

                }
                createCheckboxList(materialsList,materialsId)
            }




        val selectedMaterialIds = mutableListOf<String>()

        val selectButton =view.findViewById<CardView>(R.id.addMaterials)

        selectButton.setOnClickListener {
            selectedMaterialIds.clear() // Clear previous selections

            val checkboxContainer = view.findViewById<LinearLayout>(R.id.checkboxes)
            val db = FirebaseFirestore.getInstance() // Get Firestore instance

            for (i in 0 until checkboxContainer.childCount) {
                val checkbox = checkboxContainer.getChildAt(i) as CheckBox
                if (checkbox.isChecked) {
                    val materialId = checkbox.tag.toString()
                    selectedMaterialIds.add(materialId)


                    // check if not exists then insert
                    if(! checkStsMatExists(StudentId,materialId,yearId))
                    {
                    val studentMaterialRef = db.collection("Students Materials").document()
                    val studentMaterialData = hashMapOf(
                        "Student Id" to StudentId,
                        "Material Id" to materialId,
                        "Academic Year Id" to yearId
                    )
                    studentMaterialRef.set(studentMaterialData)
                        .addOnSuccessListener {

                            //Toast.makeText(requireContext(), "doneee", Toast.LENGTH_SHORT).show()
                        }

                    }

                }
            }
            for (i in 0 until checkboxContainer.childCount) {
                val checkbox = checkboxContainer.getChildAt(i) as CheckBox
                checkbox.isChecked = false
            }


            view.findNavController().navigate(R.id.action_addStudentMaterialFragment_to_addStudentFragment)

        }


        return view
    }

    private fun createCheckboxList(materialsList: List<String>,materialsId: List<String>) {
        val checkboxContainer = view?.findViewById<LinearLayout>(R.id.checkboxes)



        for (i in materialsList.indices) {
            val checkbox =CheckBox(requireContext())
            val material = materialsList[i]
            val id = materialsId[i]
            checkbox.text = material
            checkbox.tag = id
            checkbox.setTextColor(android.graphics.Color.parseColor("#000000"))
            if (checkboxContainer != null) {
                checkboxContainer.addView(checkbox)
            }
        }
    }

private fun checkStsMatExists(std:String , mat:String , year:String):Boolean {
    for (i in stdmat_StdId.indices) {

        if (stdmat_StdId[i] == std) {
            val materialId = stdmat_MatId[i]
            val yearId = stdmat_YearId[i]
            if( materialId == mat && yearId == year)
                return true
        }
    }
    return false
}





}