package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore


class AdminAddMateriasToDrFragment : Fragment() {

    val materialsId = ArrayList<String>()
    val materialsDrId = ArrayList<String>()
    val lang = mutableListOf<String>()
    val langId = mutableListOf<String>()
    val deptName = mutableListOf<String>()
    val deptId = mutableListOf<String>()
    val materialsName = mutableListOf<String>()
    val materialsDept = mutableListOf<String>()
    val materialsLang = mutableListOf<String>()
    val materialsId2 = ArrayList<String>()
    val materialsName2 = mutableListOf<String>()
    val materialsDept2 = mutableListOf<String>()
    val materialsLang2 = mutableListOf<String>()
    val materialsAllId = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_admin_add_materias_to_dr, container, false)
        val drId=AdminAddMateriasToDrFragmentArgs.fromBundle(requireArguments()).drId

        val button=view.findViewById<CardView>(R.id.updateAdd)

        val db = FirebaseFirestore.getInstance()


        db.collection("Doctors Materials")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val materialId = document.getString("Material Id")
                    if (materialId != null) {
                        materialsAllId.add(materialId)
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

                }
            }

        db.collection("Departments").orderBy("deptName")
            .get().addOnSuccessListener { documents ->
            deptId.clear()
            deptName.clear()
            for (document in documents.documents) {
                val name = document.getString("deptName") // get the department name
                val id = document.id
                if (name != null) {
                    deptName.add(name)
                    deptId.add(id)
                }
            }
        }

        db.collection("Materials").orderBy("Material Name")
            .get()
            .addOnSuccessListener { materials->
                materialsName.clear()
                materialsId.clear()
                materialsLang.clear()
                materialsDept.clear()

                for(material in materials){
                    val mId=material.id
                    if(!materialsAllId.contains(mId)){
                        val name = material.getString("Material Name")
                        val dept = material.getString("Department")
                        val lang = material.getString("Language")
                        val id = material.id
                        if (name != null && lang != null && dept !=null ) {
                            materialsName.add(name)
                            materialsId.add(id)
                            materialsLang.add(lang)
                            materialsDept.add(dept)

                        }
                    }
                }

                createCheckboxList()
            }

        button.setOnClickListener {
            val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)
            val checkedTags = mutableListOf<String>()

            for (i in 0 until linearLayout.childCount) {
                val child = linearLayout.getChildAt(i)
                if (child is CheckBox && child.isChecked) {
                    child.tag?.let {
                        checkedTags.add(it.toString())
                    }
                }
            }
           // Toast.makeText(requireContext(), checkedTags.toString(), Toast.LENGTH_SHORT).show()
            for (materialId in checkedTags) {
                val docData = hashMapOf(
                    "Doctor Id" to drId,
                    "Material Id" to materialId
                )

                db.collection("Doctors Materials")
                    .add(docData)
            }
            view.findNavController().navigate(R.id.action_adminAddMateriasToDrFragment_to_editDrMaterialsFragment)
        }

        return  view
    }





    private fun createCheckboxList() {
        val checkboxContainer2 = view?.findViewById<LinearLayout>(R.id.linearLayout)
        checkboxContainer2!!.removeAllViews()
        val textView2 = TextView(requireContext())
        textView2.text = "Add More Materials"
        textView2.setTextColor(Color.parseColor("#FF0000"))
        textView2.textSize = 30f
        textView2.isEnabled = false
        checkboxContainer2.addView(textView2)
        val textView2b = TextView(requireContext())
        textView2b.text = "Please check to add the material to the doctor"
        textView2b.setTextColor(Color.parseColor("#000000"))
        textView2b.textSize = 15f
        textView2b.isEnabled = false
        checkboxContainer2.addView(textView2b)

        for (iDept in deptId.indices) {
            val deptname = deptName[iDept]
            val deptId = deptId[iDept]
            val textViewDept = TextView(requireContext())
            textViewDept.text = deptname
            textViewDept.setTextColor(Color.parseColor("#FF0000"))
            textViewDept.textSize = 25f
            textViewDept.isEnabled = false

            checkboxContainer2.addView(textViewDept)
            // loop on languages
            for (iLang in langId.indices) {
                val langname = lang[iLang]
                val langId = langId[iLang]
                val textViewLang = TextView(requireContext())
                textViewLang.text = "  $langname"
                textViewLang.setTextColor(Color.parseColor("#000000"))
                textViewLang.textSize = 20f
                textViewLang.isEnabled = false


                checkboxContainer2.addView(textViewLang)
                // check materials if same lang and dept display
                for (iMat in materialsId.indices) {
                    val name = materialsName[iMat]
                    val dept = materialsDept[iMat]
                    val lang = materialsLang[iMat]
                    val id = materialsId[iMat]
                    if (lang == langId && dept == deptId) {
                        val checkbox = CheckBox(requireContext())
                        checkbox.text = name
                        checkbox.tag = id
                        checkbox.setTextColor(Color.parseColor("#000000"))
                        checkboxContainer2.addView(checkbox)
                    }
                }
            }
        }
    }
}