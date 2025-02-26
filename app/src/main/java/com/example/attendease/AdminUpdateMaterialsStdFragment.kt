package com.example.attendease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController

class AdminUpdateMaterialsStdFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_update_materials_std, container, false)
        val stdId=AdminUpdateMaterialsStdFragmentArgs.fromBundle(requireArguments()).stdId
        val deptId=AdminUpdateMaterialsStdFragmentArgs.fromBundle(requireArguments()).deptId
        val yearId=AdminUpdateMaterialsStdFragmentArgs.fromBundle(requireArguments()).yearId



        val add=view.findViewById<CardView>(R.id.addM)
        val remove=view.findViewById<CardView>(R.id.removeM)

        add.setOnClickListener {
            val action=AdminUpdateMaterialsStdFragmentDirections.actionAdminUpdateMaterialsStdFragmentToAdminAddMaterialsToStdFragment(stdId,deptId,yearId)
            view.findNavController().navigate(action)
        }
        remove.setOnClickListener {
            val action=AdminUpdateMaterialsStdFragmentDirections.actionAdminUpdateMaterialsStdFragmentToAdminRemoveMaterialFromStdFragment(stdId,yearId)
            view.findNavController().navigate(action)
        }


        return view
    }
}