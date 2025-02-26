package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AddStudentFragment : Fragment() {

    private val departments = mutableListOf<String>()
    private val academicYears = mutableListOf<String>()
    private val deptId = mutableListOf<String>()
    private val yearId = mutableListOf<String>()
    private val lang = mutableListOf<String>()
    private val langId = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_student, container, false)

        val db = FirebaseFirestore.getInstance()

        val deptSpinner = view.findViewById<Spinner>(R.id.selectdept)
        val yearSpinner = view.findViewById<Spinner>(R.id.selectyear)
        val pass = view.findViewById<TextView>(R.id.stdpassword)
        val email=view.findViewById<TextView>(R.id.stdemail)
        val stdname=view.findViewById<TextView>(R.id.stdname)
        val id=view.findViewById<TextView>(R.id.stdid)
        val radioGroup = view.findViewById<RadioGroup>(R.id.language)
        val button = view.findViewById<CardView>(R.id.button)


        departments.add("Departments")
        academicYears.add("Academic Years")
        deptId.add("0")
        yearId.add("0")

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

                deptSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        (parent.getChildAt(0) as TextView).setTextColor(Color.parseColor("#da251c"))
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
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


                yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        (parent.getChildAt(0) as TextView).setTextColor(Color.parseColor("#da251c"))
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }



        db.collection("Languages").orderBy("Language Name")
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
                    radioGroup.removeAllViews()
                    for (langName in lang) {
                        val radioButton = RadioButton(context)
                        radioButton.text = langName
                        val index = lang.indexOf(langName)
                        val layoutParams = RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        radioButton.layoutParams = layoutParams
                        radioButton.setTextColor(Color.parseColor("#000000"))
                        radioGroup.addView(radioButton)
                    }
                }
            }

        button.setOnClickListener {
            val selectedDept = deptSpinner.selectedItem.toString()
            val selectedYear = yearSpinner.selectedItem.toString()
            val selectedId = radioGroup.checkedRadioButtonId
            val password = pass.text.toString()
            val emailText = email.text.toString()
            val studentName = stdname.text.toString()
            val studentId = id.text.toString()


            if (deptSpinner.selectedItemPosition == 0 || selectedDept=="Dept") {
                Toast.makeText(requireContext(), "No Department Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (yearSpinner.selectedItemPosition == 0 || selectedYear=="Year") {
                Toast.makeText(requireContext(), "No Academic Year Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var selectedLanguage = ""
            if (selectedId != -1) { // if any RadioButton is selected
                val radioButton = view.findViewById<RadioButton>(selectedId)
                selectedLanguage = radioButton.text.toString()

            } else {
                Toast.makeText(requireContext(), "No Language Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "No Password Entered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (emailText.isEmpty()) {
                Toast.makeText(requireContext(), "No Email Entered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (studentName.isEmpty()) {
                Toast.makeText(requireContext(), "No Student Name Entered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (studentId.isEmpty()) {
                Toast.makeText(requireContext(), "No Student ID Entered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dept=findString(selectedDept,departments,deptId)
            val academic=findString(selectedYear,academicYears,yearId)
            val langIdSelected=findString(selectedLanguage,lang,langId)

            // If all fields are filled, add the data to Firestore
            val studentData = hashMapOf(
                "Department" to dept,
                "Academic Year" to academic,
                "Language" to langIdSelected,
                "Password" to password,
                "Email" to emailText,
                "Name" to studentName,
                "ID" to studentId
            )

            db.collection("Students")
                .document(studentId)
                .set(studentData)
                .addOnSuccessListener {

                    // Clear all fields
                    deptSpinner.setSelection(0)
                    yearSpinner.setSelection(0)
                    radioGroup.clearCheck()
                    pass.text = ""
                    email.text = ""
                    stdname.text = ""
                    id.text = ""

                    if(dept != null && langIdSelected != null && academic != null)
                    {Toast.makeText(requireContext(), "Student Added Successfully", Toast.LENGTH_SHORT).show()
                    val action = AddStudentFragmentDirections.actionAddStudentFragmentToAddStudentMaterialFragment(studentId,dept,langIdSelected,academic)
                    view.findNavController().navigate(action)}
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error adding student", Toast.LENGTH_SHORT).show()
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