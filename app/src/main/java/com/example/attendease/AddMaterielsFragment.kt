package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore


class AddMaterielsFragment : Fragment() {

    private val departments = mutableListOf<String>()
    private val semester = mutableListOf<String>()
    private val deptId = mutableListOf<String>()
    private val semId = mutableListOf<String>()
    private val langu = mutableListOf<String>()
    private val langId = mutableListOf<String>()

    // used to check if the material exists (if same code , dept , lang exists -> no insert)
    private val MCode = mutableListOf<String>()
    private val MLang = mutableListOf<String>()
    private val MDept = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_add_materiels, container, false)


        val db = FirebaseFirestore.getInstance()


        val code=view.findViewById<TextView>(R.id.MaterialCode)
        val name=view.findViewById<TextView>(R.id.MaterialName)
        val lang=view.findViewById<RadioGroup>(R.id.language)
        val sem=view.findViewById<RadioGroup>(R.id.semester)
        val add=view.findViewById<CardView>(R.id.addMateriel)
        val deptSpinner = view.findViewById<Spinner>(R.id.selectdept)


        // Reference to the Materials collection
        val MaterialsRef = db.collection("Materials").orderBy("Material Name")
        // Retrieve all documents
        MaterialsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(view.context,"failed to retrieve materials",
                    Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                MCode.clear() // Clear existing data before update
                MLang.clear()
                MDept.clear()
                for (document in snapshot.documents) {
                    val codemat = document.getString("Material Code")
                    val langmat = document.getString("Language")
                    val dept = document.getString("Department")
                    if (codemat != null && langmat != null && dept !=null) {
                        // Toast.makeText(view.context,"$code $lang , $dept",Toast.LENGTH_SHORT).show()
                        MCode.add(codemat)
                        MLang.add(langmat)
                        MDept.add(dept)
                    }
                }
            }
        }


        departments.add("Departments")
        deptId.add("0")

        // Get departments
        db.collection("Departments").orderBy("deptName")
            .get()
            .addOnSuccessListener { result ->
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



        db.collection("Languages").orderBy("Language Name")
            .addSnapshotListener { result, error ->
                if(result != null) {
                    langu.clear()
                    langId.clear()
                    for (document in result.documents) {
                        val langName =
                            document.getString("Language Name") // get the language name
                        val id = document.id // get the document id

                        if (langName != null) {
                            langu.add(langName) // add the department name to the departments list
                            langId.add(id) // add the id to the deptId list
                        }
                    }
                    lang.removeAllViews()
                    for (langName in langu) {
                        val radioButton = RadioButton(context)
                        radioButton.text = langName
                        val index = langu.indexOf(langName)
                        val layoutParams = RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        radioButton.layoutParams = layoutParams
                        radioButton.setTextColor(Color.parseColor("#000000"))             // Add the RadioButton to the RadioGroup using the placeholder view as reference
                        lang.addView(radioButton)
                        // Toast.makeText(view.context ,"radio done ",Toast.LENGTH_SHORT ).show()
                    }
                }
            }

        db.collection("Semesters").orderBy("Semester Name")
            .addSnapshotListener { result, error ->
                semester.clear()
                semId.clear()
                if (result != null) {
                    for (document in result.documents) {
                        val semName = document.getString("Semester Name") // get the department name
                        val id = document.id // get the document id

                        if (semName != null) {
                            semester.add(semName) // add the department name to the departments list
                            semId.add(id) // add the id to the deptId list
                        }
                    }
                    sem.removeAllViews()
                    for (SemName in semester) {
                        val radioButton = RadioButton(context)
                        radioButton.text = SemName
                        val index = langu.indexOf(SemName)
                        val layoutParams = RadioGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        radioButton.layoutParams = layoutParams
                        radioButton.setTextColor(Color.parseColor("#000000"))             // Add the RadioButton to the RadioGroup using the placeholder view as reference
                        sem.addView(radioButton)
                    }

                }
            }


        add.setOnClickListener {
            val codeM=code.text.toString().uppercase().trim()
            val nameM=name.text.toString().lowercase().trim()
            val selectedLang=lang.checkedRadioButtonId
            val selectedSem=sem.checkedRadioButtonId
            val selectedDept = deptSpinner.selectedItem.toString()

            if (deptSpinner.selectedItemPosition == 0 || selectedDept=="Dept") {
                Toast.makeText(requireContext(), "No Department Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var selectedLanguage = ""
            if (selectedLang != -1) { // if any RadioButton is selected
                val radioButton = view.findViewById<RadioButton>(selectedLang)
                selectedLanguage = radioButton.text.toString()

            } else {
                Toast.makeText(requireContext(), "No Language Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (codeM.isEmpty() || nameM.isEmpty()) {
                Toast.makeText(requireContext(), "Name and Code are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var selectedSemester = ""
            if (selectedSem != -1) { // if any RadioButton is selected
                val radioButton = view.findViewById<RadioButton>(selectedSem)
                selectedSemester = radioButton.text.toString()

            } else {
                Toast.makeText(requireContext(), "No Semester Selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val l=findString(selectedLanguage,langu,langId)
            val s=findString(selectedSemester,semester,semId)
            val dept=findString(selectedDept,departments,deptId)
            val t=view.findViewById<TextView>(R.id.t1)
            //  var m="code "+codeM+" name :"+nameM+"  lang "+selectedLanguage+" sem "+selectedSemester+" id l  "+l+"  id sem  "+s+" dept  "+selectedDept+" id "+dept
            //t.text=m
            //Toast.makeText(view.context,"dept: $selectedDept , id : $dept",Toast.LENGTH_LONG).show()

            if(l!= null && s!= null && dept !=null ) {
                if (checkMaterialExists(codeM, l, dept)) {
                    Toast.makeText(requireContext(), "Material Alerady Exists", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val materialData = hashMapOf(
                    "Material Name" to nameM,
                    "Material Code" to codeM,
                    "Semester" to s,
                    "Department" to dept.toString(),
                    "Language" to l.toString()
                )

                db.collection("Materials")
                    .document()
                    .set(materialData)
                    .addOnSuccessListener {

                        // Clear all fields
                        deptSpinner.setSelection(0)
                        lang.clearCheck()
                        sem.clearCheck()
                        code.text = ""
                        name.text = ""


                        Toast.makeText(
                            requireContext(),
                            "Material Added Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Error adding material",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }

        }




        return view
    }

    // array 1 names    array 2 ids
    fun findString(input: String, array1: MutableList<String>, array2: MutableList<String>): String? {
        val index = array1.indexOf(input)
        return if (index != -1 && index < array2.size) {
            array2[index]
        } else {
            null
        }
    }

    private fun checkMaterialExists(mcode: String, selectedlang:String, selectedDept : String):Boolean {
        if (MCode.contains(mcode)) {
            val indexOfCode = MCode.indexOf(mcode)
            val lang = MLang[indexOfCode]
            val dept = MDept[indexOfCode]
            if (lang == selectedlang && dept == selectedDept)
                return true
            else
                return false
        }
        else return false
    }

}