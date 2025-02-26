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
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore


class AdminAddMaterialsToStdFragment : Fragment() {


    val materialsId = ArrayList<String>()
    val deptName = mutableListOf<String>()
    val materialsName = mutableListOf<String>()
    val materialsStdId = ArrayList<String>()
    val db = FirebaseFirestore.getInstance()
    lateinit var deptId:String
    lateinit var langId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_add_materials_to_std, container, false)

        val stdId=AdminAddMaterialsToStdFragmentArgs.fromBundle(requireArguments()).stdId
         deptId=AdminAddMaterialsToStdFragmentArgs.fromBundle(requireArguments()).deptId
        val yearId=AdminAddMaterialsToStdFragmentArgs.fromBundle(requireArguments()).yearId
        val button=view.findViewById<CardView>(R.id.updateAdd)



        db.collection("Students").document(stdId).get()
            .addOnSuccessListener {
                langId = it.get("Language").toString()


                db.collection("Students Materials")
                    .whereEqualTo("Student Id", stdId)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val materialId = document.getString("Material Id")
                            if (materialId != null) {
                                materialsStdId.add(materialId)
                            }
                        }

                        getMaterials()

                    }
            }

//        db.collection("Materials")
//            .whereEqualTo("Department",deptId)
//            .get()
//            .addOnSuccessListener { materials->
//                materialsName.clear()
//                materialsId.clear()
//
//                for(material in materials){
//                    val mId=material.id
//                    if(!materialsStdId.contains(mId)){
//                        val name = material.getString("Material Name")
//                        val id = material.id
//                        if (name != null) {
//                            materialsName.add(name)
//                            materialsId.add(id)
//
//
//                        }
//                    }
//                }
//
//                createCheckboxList()
//            }

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
         //   Toast.makeText(requireContext(), checkedTags.toString(), Toast.LENGTH_SHORT).show()
            for (materialId in checkedTags) {
                val stdData = hashMapOf(
                    "Student Id" to stdId,
                    "Material Id" to materialId,
                    "Academic Year Id" to yearId
                )

                db.collection("Students Materials")
                    .add(stdData)
            }
            for (i in 0 until linearLayout.childCount) {
                val child = linearLayout.getChildAt(i)
                if (child is CheckBox && child.isChecked) {
                    child.isChecked = false
                }
            }
            Toast.makeText(requireContext(),"Materials are added",Toast.LENGTH_SHORT).show()

           //view.findNavController().navigate(R.id.action_adminAddMaterialsToStdFragment_to_adminUpdateMaterialsStdFragment)
        }




        return view
    }

    fun getMaterials(){
        db.collection("Materials")
            .whereEqualTo("Department",deptId)
            .whereEqualTo("Language",langId)
            .orderBy("Material Name")
            .get()
            .addOnSuccessListener { materials->
                materialsName.clear()
                materialsId.clear()

                for(material in materials){
                    val mId=material.id
                    if(!materialsStdId.contains(mId)){
                        val name = material.getString("Material Name")
                        val id = material.id
                        if (name != null) {
                            materialsName.add(name)
                            materialsId.add(id)


                        }
                    }
                }

                createCheckboxList()
            }
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
        textView2b.text = "Please check to add the material to the student"
        textView2b.setTextColor(Color.parseColor("#000000"))
        textView2b.textSize = 15f
        textView2b.isEnabled = false
        checkboxContainer2.addView(textView2b)


                // check materials if same lang and dept display
                for (iMat in materialsId.indices) {
                    val name = materialsName[iMat]
                    val id = materialsId[iMat]
                        val checkbox = CheckBox(requireContext())
                        checkbox.text = name
                        checkbox.tag = id
                        checkbox.setTextColor(Color.parseColor("#000000"))
                        checkboxContainer2.addView(checkbox)

                    }

    }
}