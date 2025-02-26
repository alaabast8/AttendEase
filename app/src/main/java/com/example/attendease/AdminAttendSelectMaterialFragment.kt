package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore


class AdminAttendSelectMaterialFragment : Fragment() {

    val departments = mutableListOf<String>()
    val deptId = mutableListOf<String>()
    val languages= mutableListOf<String>()
    val langId = mutableListOf<String>()
    val semesters= mutableListOf<String>()
    val semId= mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_admin_attend_select_material, container, false)

        val db = FirebaseFirestore.getInstance()


        val deptSpinner = view.findViewById<Spinner>(R.id.selectdept)
        val langSpinner = view.findViewById<Spinner>(R.id.selectLang)
        val semSpinner = view.findViewById<Spinner>(R.id.selectSem)
        val show=view.findViewById<CardView>(R.id.button)


        // Get departments
        db.collection("Departments").orderBy("deptName")
            .get()
            .addOnSuccessListener { result ->
                departments.clear()
                deptId.clear()
                departments.add("Dept")
                deptId.add("0")
                for (document in result) {
                    val deptName = document.getString("deptName") // get the department name
                    val id = document.id // get the document id

                    if (deptName != null) {
                        departments.add(deptName) // add the department name to the departments list
                        deptId.add(id) // add the id to the deptId list
                    }
                }
                //Add the string to the spinner
                val deptAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, departments)
                deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                deptSpinner.adapter = deptAdapter
            }


        // Get Semesters
        db.collection("Semesters").orderBy("Semester Name")
            .get()
            .addOnSuccessListener { result ->
                semesters.clear()
                semId.clear()
                semesters.add("Sem")
                semId.add("0")
                for (document in result) {
                    val semName = document.getString("Semester Name")
                    val id = document.id

                    if (semName != null) {
                        semesters.add(semName)
                        semId.add(id)
                    }
                }
                //Add the string to the spinner
                val deptAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, semesters)
                deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                semSpinner.adapter = deptAdapter
            }



        // Get Languages
        db.collection("Languages").orderBy("Language Name")
            .get()
            .addOnSuccessListener { result ->
                languages.clear()
                langId.clear()
                languages.add("Lang")
                langId.add("0")
                for (document in result) {
                    val langName = document.getString("Language Name")
                    val id = document.id

                    if (langName != null) {
                        languages.add(langName)
                        langId.add(id)
                    }
                }
                //Add the string to the spinner
                val deptAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
                deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                langSpinner.adapter = deptAdapter
            }


        show.setOnClickListener {
            val selectedDept = deptSpinner.selectedItem.toString()
            val selectedLang = langSpinner.selectedItem.toString()
            val selectedSem = semSpinner.selectedItem.toString()


            if (deptSpinner.selectedItemPosition == 0 || selectedDept == "Dept") {
                Toast.makeText(requireContext(), "No Department Selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (langSpinner.selectedItemPosition == 0 || selectedLang == "Lang") {
                Toast.makeText(requireContext(), "No Language Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (semSpinner.selectedItemPosition == 0 || selectedSem == "Sem") {
                Toast.makeText(requireContext(), "No Semester  Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val dept = findString(selectedDept, departments, deptId)
            val lang = findString(selectedLang, languages, langId)
            val sem = findString(selectedSem, semesters, semId)

            //Toast.makeText(requireContext(), " $dept      $lang     $sem", Toast.LENGTH_SHORT)
              //  .show()

            val radioGroup = view.findViewById<RadioGroup>(R.id.materials)

            db.collection("Materials")
                .whereEqualTo("Semester", sem)
                .whereEqualTo("Language", lang)
                .whereEqualTo("Department", dept)
                .orderBy("Material Name")
                .get()
                .addOnSuccessListener { materials ->
                    radioGroup.removeAllViews()
                    for (material in materials) {
                        val materialName = material.getString("Material Name")
                        val materialCode = material.get("Material Code")
                        val materialId = material.id

                        val radioButton = RadioButton(requireContext())
                        radioButton.text = "$materialName _ $materialCode"
                        radioButton.tag = materialId

                        radioGroup.addView(radioButton)


                    }
                    radioGroup.setOnCheckedChangeListener { group, checkedId ->
                        val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                        val selectedTag = selectedRadioButton.tag
                        val action = AdminAttendSelectMaterialFragmentDirections.actionAdminAttendSelectMaterialFragmentToAdminShowAttendFragment(selectedTag.toString())
                        view.findNavController().navigate(action)

                    }


                }
        }




        return view
    }

    fun findString(input: String, array1: MutableList<String>, array2: MutableList<String>): String? {
        val index = array1.indexOf(input)
        return if (index != -1 && index < array2.size) {
            array2[index]
        } else {
            null
        }
    }


}