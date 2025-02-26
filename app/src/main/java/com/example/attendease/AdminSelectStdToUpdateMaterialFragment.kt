package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminSelectStdToUpdateMaterialFragment : Fragment() {

    val departments = mutableListOf<String>()
    val deptId = mutableListOf<String>()
    val languages= mutableListOf<String>()
    val langId = mutableListOf<String>()
    val academicYears = mutableListOf<String>()
    val yearId = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_admin_select_std_to_update_material, container, false)

        val db = FirebaseFirestore.getInstance()

        val deptSpinner = view.findViewById<Spinner>(R.id.selecteddept)
        val langSpinner = view.findViewById<Spinner>(R.id.selectedLang)
        val yearSpinner = view.findViewById<Spinner>(R.id.selectedyear)
        val show=view.findViewById<CardView>(R.id.buttonshowstd)


        db.collection("Departments")
            .orderBy("deptName")
            .get()
            .addOnSuccessListener { result ->
                departments.clear()
                deptId.clear()
                departments.add("Dept")
                deptId.add("0")
                for (document in result) {
                    val deptName = document.getString("deptName")
                    val id = document.id // get the document id

                    if (deptName != null) {
                        departments.add(deptName)
                        deptId.add(id)
                    }
                }
                //Add the string to the spinner
                val deptAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, departments)
                deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                deptSpinner.adapter = deptAdapter
            }


        db.collection("Languages")
            .orderBy("Language Name")
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
                val langAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
                langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                langSpinner.adapter = langAdapter
            }

        db.collection("Academic Years")
            .orderBy("year", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                academicYears.clear()
                yearId.clear()
                academicYears.add("Year")
                yearId.add("0")
                for (document in result) {
                    val yearr = document.getString("year")
                    val id = document.id

                    if (yearr != null) {
                        academicYears.add(yearr)
                        yearId.add(id)
                    }

                }

                // Add the string to the spinner
                val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, academicYears)
                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                yearSpinner.adapter = yearAdapter
            }

        show.setOnClickListener {
            val selectedDept = deptSpinner.selectedItem.toString()
            val selectedLang = langSpinner.selectedItem.toString()
            val selectedYear = yearSpinner.selectedItem.toString()


            if (deptSpinner.selectedItemPosition == 0 || selectedDept == "Dept") {
                Toast.makeText(requireContext(), "No Department Selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (langSpinner.selectedItemPosition == 0 || selectedLang == "Lang") {
                Toast.makeText(requireContext(), "No Language Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (yearSpinner.selectedItemPosition == 0 || selectedYear == "Year") {
                Toast.makeText(requireContext(), "No Academic Year Selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            val dept = findString(selectedDept, departments, deptId)
            val lang = findString(selectedLang, languages, langId)
            val year=findString(selectedYear,academicYears,yearId)


            val radioGroup = view.findViewById<RadioGroup>(R.id.stdtoselect)

            db.collection("Students")
                .whereEqualTo("Language", lang)
                .whereEqualTo("Department", dept)
                .whereEqualTo("Academic Year",year)
                .get()
                .addOnSuccessListener { students ->
                    radioGroup.removeAllViews()
                    for (student in students) {
                        val stdName = student.getString("Name")
                        val stdId = student.id

                        val radioButton = RadioButton(requireContext())
                        radioButton.text = "  $stdId - $stdName "
                        radioButton.tag = stdId

                        radioGroup.addView(radioButton)
                    }
                    radioGroup.setOnCheckedChangeListener { group, checkedId ->
                        val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                        val selectedTag = selectedRadioButton.tag
                        val action =AdminSelectStdToUpdateMaterialFragmentDirections.actionAdminSelectStdToUpdateMaterialFragmentToAdminUpdateMaterialsStdFragment(selectedTag.toString(),year.toString(),dept.toString())
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