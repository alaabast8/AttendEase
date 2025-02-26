package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminCheckArchiveFragment : Fragment() {

    private val db = Firebase.firestore

    val sessionsIds = mutableListOf<String>()
    val AllStudentsId = mutableListOf<String>()
    val AllStudentsName = mutableListOf<String>()
    val stdAttIds = mutableListOf<String>()
    val AllStdAttIds = mutableListOf<String>()
    lateinit var materialDesc: TextView
    lateinit var nbOfLectures: TextView
    lateinit var stdInfo: TextView
    lateinit var materialId: String
    lateinit var  selectedyear: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_check_archive, container, false)

        // get material id and selected year from bundle
        materialId = AdminCheckArchiveFragmentArgs.fromBundle(requireArguments()).materialID
        selectedyear = AdminCheckArchiveFragmentArgs.fromBundle(requireArguments()).yearStr.trim()

        //Toast.makeText(view.context,selectedyear,Toast.LENGTH_SHORT).show()

        materialDesc = view.findViewById<TextView>(R.id.MaterialDesc2Ar)
        nbOfLectures = view.findViewById<TextView>(R.id.nbOfLectures2Ar)
        stdInfo = view.findViewById<TextView>(R.id.StdInfo2Ar)

        val button = view.findViewById<CardView>(R.id.detailsAr)
        button.setOnClickListener {
            val action = AdminCheckArchiveFragmentDirections.actionAdminCheckArchiveFragmentToAdminCheckArchiveDetailsFragment(materialId,selectedyear)
            view.findNavController().navigate(action)
        }


        // get name and coe for material
        db.collection("Materials")
            .document(materialId)
            .get()
            .addOnSuccessListener {
                val name = it.getString("Material Name")
                val code = it.getString("Material Code")
                if (name != null && code != null) {
                    materialDesc.text = "$name - $code"
//                Toast.makeText(view.context,"$name  $code",Toast.LENGTH_SHORT).show()
                }
             }
        // get all student id and name from db
        db.collection("Students $selectedyear")
            .get()
            .addOnSuccessListener { result ->
            AllStudentsName.clear()
            AllStudentsId.clear()
            for (document in result.documents) {
                val name = document.getString("Name")
                val id = document.getString("Id")
                if (name != null && id != null) {
                    AllStudentsName.add(name)
                    AllStudentsId.add(id)
                }
            }
            //  Toast.makeText(view.context,"${AllStudentsId.toString()}",Toast.LENGTH_LONG).show()
            getAllSessionId()

        }// end getting students


        return view
    }

    private fun getAllSessionId() {

        db.collection("Sessions $selectedyear")
            .whereEqualTo("Material Id", materialId)
            .addSnapshotListener { value, error ->
                sessionsIds.clear()
                for (document in value!!.documents) {
                    val getSessionId = document.getString("Session Id").toString()
                    sessionsIds.add(getSessionId)
                }
                //   stdInfo.text=sessionsIds.toString()
                nbOfLectures.text = "Number Of Lectures : ${sessionsIds.count()}"
                // we have all sessionId
                getAllStdIdFromAtt()// get all the std id
            }
    }

    private  fun getAllStdIdFromAtt() {
        // Track the number of sessions to process
        var sessionsToProcess = sessionsIds.size
        // Clear AllStdAttIds before starting
        AllStdAttIds.clear()

        for (sessId in sessionsIds) {
            // stdInfo.text = sessId

            getAllStdBySession(sessId) {
                // Decrement counter after each session's data retrieval
                sessionsToProcess--
                // Call getDistinctStdId only after all sessions are processed
                if (sessionsToProcess == 0) {
                    stdAttIds.clear()
                    getDistinctStdId()


                }
            }
        }
    }

    private  fun getAllStdBySession(sessId: String, callback: () -> Unit) {


        db.collection("Attendances $selectedyear").whereEqualTo("Session Id", sessId).get()
            .addOnSuccessListener { valueFromAtt ->
                // Process retrieved data as before (get student IDs and add to AllStdAttIds)
                for (AttId in valueFromAtt!!.documents) {
                    val stdid = AttId.getString("Student Id")
                    if (stdid != null) {
                        AllStdAttIds.add(stdid)
                    }
                }
                // Call the callback after processing this session's data
                callback()
//                stdInfo.text=AllStdAttIds.toString()
            }
    }

    private fun getDistinctStdId(){
        // get distinct std id

        val checkboxContainer = view?.findViewById<LinearLayout>(R.id.stdAtt2Ar)
        db.collection("Students Materials $selectedyear")
            .whereEqualTo("Material Id",materialId)
            .get()
            .addOnSuccessListener {
                stdAttIds.clear()
                for(stdMat in it.documents)
                {   val stdid=stdMat.getString("Student Id")
                    if(stdid != null)
                    {
                        stdAttIds.add(stdid)
                        //Toast.makeText(view.context,"$stdid",Toast.LENGTH_SHORT).show()
                        val stdindex = AllStudentsId.indexOf(stdid)
                        if(stdindex <0 ){
                            break
                        }
                        val name = AllStudentsName[stdindex]
//                stdInfo.append( "$name $stdid \n  ")
                        var nbOfAtt:Int=0
                        for(i in AllStdAttIds){
                            if(i == stdid)
                            {
                                nbOfAtt+=1
                            }
                        }

                        val perc = (nbOfAtt.toFloat() / sessionsIds.count().toFloat()) * 100.0f
                        val nbWithTwoDecimals = perc * 100.0f
                        // Cast to an integer to remove the decimal part
                        val roundedInt = nbWithTwoDecimals.toInt()
                        // Divide by 100.0f to get the original value with two decimals
                        val formattedNb = roundedInt / 100.0f
                        val textView = TextView(requireContext())
                        textView.text = "  $name - $stdid : $nbOfAtt  $formattedNb %"
                        textView.textSize = 18f
                        if (perc >= 50)
                            textView.setTextColor(Color.parseColor("#0c5e38"))
                        else
                            textView.setTextColor(Color.parseColor("#da251c"))

                        if (checkboxContainer != null) {
                            checkboxContainer.addView(textView)
                        }

                    }
                }
            }
    }
}