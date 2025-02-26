
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

class AdminRemoveMaterialFromStdFragment : Fragment() {
    val studentMaterialsIdList= mutableListOf<String>()
    val materialsName = mutableListOf<String>()
    val materialsId = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_admin_remove_material_from_std, container, false)

        val stdId=AdminRemoveMaterialFromStdFragmentArgs.fromBundle(requireArguments()).stdId
        val yearId=AdminRemoveMaterialFromStdFragmentArgs.fromBundle(requireArguments()).yearId

        val button=view.findViewById<CardView>(R.id.updateRemove)

        val db = FirebaseFirestore.getInstance()

        db.collection("Students Materials")
            .whereEqualTo("Student Id", stdId)
            .whereEqualTo("Academic Year Id",yearId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Log the exception
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    studentMaterialsIdList.clear()
                    for (document in snapshot.documents) {
                        val materialId = document.getString("Material Id")
                        if (materialId != null) {
                            studentMaterialsIdList.add(materialId)
                        }
                    }
                }
            }

        db.collection("Materials")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Log the exception
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    materialsName.clear()
                    materialsId.clear()
                    for(material in snapshot.documents){
                        val mId=material.id
                        if(studentMaterialsIdList.contains(mId)){
                            val name = material.getString("Material Name")
                            val id = material.id
                            if (name != null ) {
                                materialsName.add(name)
                                materialsId.add(id)
                            }
                        }
                    }
                    createCheckboxList()
                }
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

            db.collection("Students Materials")
                .whereIn("Material Id",uncheckedTags)
                .get()
                .addOnSuccessListener {materials->
                    for(material in materials){
                        val id=material.id
                        db.collection("Students Materials").document(id).delete()
                    }
                }
            Toast.makeText(requireContext(),"Materials are removed",Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun createCheckboxList() {
        val checkboxContainer = view?.findViewById<LinearLayout>(R.id.linearLayout)
        checkboxContainer!!.removeAllViews()
        val textView1 = TextView(requireContext())
        textView1.text = "The student 's materials:"
        textView1.setTextColor(Color.parseColor("#FF0000"))
        textView1.textSize = 30f
        textView1.isEnabled = false
        checkboxContainer.addView(textView1)
        val textView1b = TextView(requireContext())
        textView1b.text = "Please uncheck to remove the material from the student"
        textView1b.setTextColor(Color.parseColor("#000000"))
        textView1b.textSize = 15f
        textView1b.isEnabled = false
        checkboxContainer.addView(textView1b)

                for (iMat in materialsId.indices) {
                    val name = materialsName[iMat]
                    val id = materialsId[iMat]

                        val checkbox = CheckBox(requireContext())
                        checkbox.text = name
                        checkbox.tag = id
                        checkbox.isChecked=true
                        checkbox.setTextColor(Color.parseColor("#000000"))
                        checkboxContainer.addView(checkbox)
                    }
                }


}