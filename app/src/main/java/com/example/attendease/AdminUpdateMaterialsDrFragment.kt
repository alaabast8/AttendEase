package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore


class AdminUpdateMaterialsDrFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view=inflater.inflate(R.layout.fragment_admin_update_materials_dr, container, false)
       val drId=AdminUpdateMaterialsDrFragmentArgs.fromBundle(requireArguments()).drId

        val add=view.findViewById<CardView>(R.id.addM)
        val remove=view.findViewById<CardView>(R.id.removeM)

        add.setOnClickListener {
            val action=AdminUpdateMaterialsDrFragmentDirections.actionAdminUpdateMaterialsDrFragmentToAdminAddMateriasToDrFragment(drId)
            view.findNavController().navigate(action)
        }
        remove.setOnClickListener {
            val action=AdminUpdateMaterialsDrFragmentDirections.actionAdminUpdateMaterialsDrFragmentToAdminRemoveMaterialsFromDrFragment(drId)
            view.findNavController().navigate(action)
        }

        return  view
    }

 }