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
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore


class EditDrMaterialsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_edit_dr_materials, container, false)


        val db = FirebaseFirestore.getInstance()

        val drSpinner = view.findViewById<Spinner>(R.id.selectDr)
        val button = view.findViewById<CardView>(R.id.go_edit)

        val doctors= mutableListOf<String>()
        val drId= mutableListOf<String>()


        db.collection("Doctors")
            .orderBy("Doctor Name")
            .get()
            .addOnSuccessListener { result ->
                doctors.clear()
                drId.clear()
                doctors.add("Pick a Doctor")
                drId.add("0")
                for (document in result) {
                    val name = document.getString("Doctor Name")
                    val id = document.id

                    if (name != null) {
                        doctors.add(name)
                        drId.add(id)
                    }

                }

                // Add the string to the spinner
                val drAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, doctors)
                drAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                drSpinner.adapter = drAdapter
                drSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        (parent.getChildAt(0) as TextView).setTextColor(Color.parseColor("#000000"))
                        (parent.getChildAt(0) as TextView).textSize=18f


                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }


        button.setOnClickListener {
            val selectedDr = drSpinner.selectedItem.toString()
            if (drSpinner.selectedItemPosition == 0 || selectedDr == "Doctor") {
                Toast.makeText(requireContext(), "No Doctor Selected", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val dr = findString(selectedDr, doctors, drId).toString()

            val acion=EditDrMaterialsFragmentDirections.actionEditDrMaterialsFragmentToAdminUpdateMaterialsDrFragment(dr)
            view.findNavController().navigate(acion)


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