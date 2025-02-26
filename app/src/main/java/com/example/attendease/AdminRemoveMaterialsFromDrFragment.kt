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


class AdminRemoveMaterialsFromDrFragment : Fragment() {

    val materialsId = ArrayList<String>()
    val materialsDrId = ArrayList<String>()
    val lang = mutableListOf<String>()
    val langId = mutableListOf<String>()
    val deptName = mutableListOf<String>()
    val deptId = mutableListOf<String>()
    val materialsName = mutableListOf<String>()
    val materialsDept = mutableListOf<String>()
    val materialsLang = mutableListOf<String>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_admin_remove_materials_from_dr, container, false)
        val drId=AdminRemoveMaterialsFromDrFragmentArgs.fromBundle(requireArguments()).drId

        val db = FirebaseFirestore.getInstance()

        val button=view.findViewById<CardView>(R.id.updateRemove)

        db.collection("Doctors Materials")
            .whereEqualTo("Doctor Id", drId)
            .get()
            .addOnSuccessListener { documents ->
                materialsDrId.clear()
                for (document in documents) {
                    val materialId = document.getString("Material Id")
                    if (materialId != null) {
                        materialsDrId.add(materialId)
                    }
                }
              //  Toast.makeText(requireContext(), " $materialsDrId", Toast.LENGTH_SHORT).show()
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
                    if(materialsDrId.contains(mId)){
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
            val uncheckedTags = mutableListOf<String>()

            for (i in 0 until linearLayout.childCount) {
                val child = linearLayout.getChildAt(i)
                if (child is CheckBox && !(child.isChecked)) {
                    child.tag?.let {
                        uncheckedTags.add(it.toString())
                    }
                }
            }
           // Toast.makeText(requireContext(), uncheckedTags.toString(), Toast.LENGTH_SHORT).show()

            db.collection("Doctors Materials")
                .whereIn("Material Id",uncheckedTags)
                .get()
                .addOnSuccessListener {materials->
                    for(material in materials){
                        val id=material.id
                        db.collection("Doctors Materials").document(id).delete()
                    }

                }
            view.findNavController().navigate(R.id.action_adminRemoveMaterialsFromDrFragment_to_editDrMaterialsFragment)
        }


        return  view
    }

    private fun createCheckboxList() {
        val checkboxContainer = view?.findViewById<LinearLayout>(R.id.linearLayout)
        checkboxContainer!!.removeAllViews()
        val textView1 = TextView(requireContext())
        textView1.text = "The doctor's materials:"
        textView1.setTextColor(Color.parseColor("#FF0000"))
        textView1.textSize = 30f
        textView1.isEnabled = false
        checkboxContainer.addView(textView1)
        val textView1b = TextView(requireContext())
        textView1b.text = "Please uncheck to remove the material from the doctor"
        textView1b.setTextColor(Color.parseColor("#000000"))
        textView1b.textSize = 15f
        textView1b.isEnabled = false
        checkboxContainer.addView(textView1b)

        for (iDept in deptId.indices) {
            val deptname = deptName[iDept]
            val deptId = deptId[iDept]
            val textViewDept = TextView(requireContext())
            textViewDept.text = deptname
            textViewDept.setTextColor(Color.parseColor("#FF0000"))
            textViewDept.textSize = 25f
            textViewDept.isEnabled = false

            checkboxContainer.addView(textViewDept)
            // loop on languages
            for (iLang in langId.indices) {
                val langname = lang[iLang]
                val langId = langId[iLang]
                val textViewLang = TextView(requireContext())
                textViewLang.text = "  $langname"
                textViewLang.setTextColor(Color.parseColor("#000000"))
                textViewLang.textSize = 20f
                textViewLang.isEnabled = false


                checkboxContainer.addView(textViewLang)
                // check materials if same lang and dept display
                for (iMat in materialsId.indices) {
                    val name = materialsName[iMat]
                    val dept = materialsDept[iMat]
                    val lang = materialsLang[iMat]
                    val id = materialsId[iMat]
                    if (lang == langId && dept == deptId && materialsDrId.contains(id)) {
                        val checkbox = CheckBox(requireContext())
                        checkbox.text = name
                        checkbox.tag = id
                        checkbox.isChecked=true
                        checkbox.setTextColor(Color.parseColor("#000000"))
                        checkboxContainer.addView(checkbox)
                    }
                }
            }
        }

    }

}