package com.example.attendease

import android.graphics.Color
import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddDrMaterialFragment : Fragment() {

    private val db = Firebase.firestore
    val materialsName = mutableListOf<String>()
    val materialsId = mutableListOf<String>()
    val materialsDept = mutableListOf<String>()
    val materialsLang = mutableListOf<String>()
    val langName = mutableListOf<String>()
    val langId = mutableListOf<String>()
    val deptName = mutableListOf<String>()
    val deptId = mutableListOf<String>()
    val drMat_drId = mutableListOf<String>()
    val drMat_MatId = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_dr_material, container, false)

        // get dr Id
        val doctorId = AddDrMaterialFragmentArgs.fromBundle(requireArguments()).doctorId
        val btn = view.findViewById<CardView>(R.id.addDrMaterials)
        // Toast.makeText(view.context,"dr id : $doctorId",Toast.LENGTH_SHORT).show()


        val drMatRef = db.collection("Doctors Materials")

        // Retrieve all documents
        drMatRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(view.context,"failed to retrieve students materials, plz try again",Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                drMat_MatId.clear() // Clear existing data before update
                drMat_drId.clear()
                for (document in snapshot.documents) {
                    val drId = document.getString("Doctor Id")
                    val matId = document.getString("Material Id")

                    if (drId != null && matId != null ) {
                        drMat_MatId.add(matId)
                        drMat_drId.add(drId)
                    }
                }
            }
        }



        db.collection("Languages").orderBy("Language Name")
            .get().addOnSuccessListener { documents ->
            langName.clear()
            langId.clear()
            for (document in documents.documents) {
                val name = document.getString("Language Name") // get the department name
                val id = document.id
                if (name != null) {
                    langName.add(name)
                    langId.add(id)

                }
            }
            // Toast.makeText(view.context,"lang in the list",Toast.LENGTH_SHORT).show()

            // done lang
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
                // Toast.makeText(view.context,"dept in the list",Toast.LENGTH_SHORT).show()

                // done dept
                db.collection("Materials").orderBy("Material Name")
                    .get().addOnSuccessListener { documents ->
                    materialsId.clear()
                    materialsDept.clear()
                    materialsLang.clear()
                    materialsName.clear()
                    for (document in documents.documents) {
                        val name = document.getString("Material Name")
                        val dept = document.getString("Department")
                        val lang = document.getString("Language")
                        val id = document.id
                        if (name != null && lang != null && dept !=null ) {
                            materialsName.add(name)
                            materialsId.add(id)
                            materialsLang.add(lang)
                            materialsDept.add(dept)
                            // Toast.makeText(view.context,"mat is added",Toast.LENGTH_SHORT).show()

                        }
                    }
                    //Toast.makeText(view.context,"$materialsName",Toast.LENGTH_SHORT).show()

                    createCheckboxList()

                }


            }

        }



        val selectedMaterialIds = mutableListOf<String>()

        btn.setOnClickListener {
            selectedMaterialIds.clear() // Clear previous selections

            val checkboxContainer = view.findViewById<LinearLayout>(R.id.checkboxesFordr)

            for (i in 0 until checkboxContainer.childCount) {
                val checkbox = checkboxContainer.getChildAt(i)
                if (checkbox.isEnabled) {

                    checkbox as CheckBox
                    if (checkbox.isChecked) {
                        val materialId = checkbox.tag.toString()
                        selectedMaterialIds.add(materialId)

                        // check if not exists then insert
                        if(! checkDrsMatExists(doctorId,materialId))
                        {
                            val drMaterialRef = db.collection("Doctors Materials").document()
                            val doctorMaterialData = hashMapOf(
                                "Doctor Id" to doctorId,
                                "Material Id" to materialId,
                            )
                            // Toast.makeText(view.context, "$materialId", Toast.LENGTH_LONG).show()
                            drMaterialRef.set(doctorMaterialData)
                                .addOnSuccessListener {
                                    createCheckboxList()
                                }
                        }
                    }
                }
            }
            for (i in 0 until checkboxContainer.childCount) {

                val checkbox = checkboxContainer.getChildAt(i)
                if(checkbox.isEnabled) {
                    checkbox as CheckBox
                    checkbox.isChecked = false
                }
            }

        }




        return view
    }

    private fun createCheckboxList() {
        val checkboxContainer = view?.findViewById<LinearLayout>(R.id.checkboxesFordr)
        checkboxContainer!!.removeAllViews()
        for (iDept in deptId.indices) {
            val deptname = deptName[iDept]
            val deptId = deptId[iDept]
            val textViewDept = TextView(requireContext())
            textViewDept.text = deptname
            // Toast.makeText(requireContext(),"$deptname ",Toast.LENGTH_SHORT).show()
            textViewDept.setTextColor(Color.parseColor("#FF0000"))
            textViewDept.textSize = 25f
            textViewDept.isEnabled = false

            checkboxContainer.addView(textViewDept)
            // loop on languages
            for (iLang in langId.indices) {
                val langname = langName[iLang]
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
                    if (lang == langId && dept == deptId && !drMat_MatId.contains(id)) {
                        val checkbox = CheckBox(requireContext())
                        checkbox.text = name
                        checkbox.tag = id
                        checkbox.setTextColor(Color.parseColor("#000000"))
                        checkboxContainer.addView(checkbox)
                    }
                }
            }
        }
    }



    private fun checkDrsMatExists(dr:String , mat:String ):Boolean {
        for (i in drMat_drId.indices) {

            if (drMat_drId[i] == dr) {
                val materialId = drMat_MatId[i]
                if( materialId == mat )
                    return true
            }
        }
        return false
    }


}