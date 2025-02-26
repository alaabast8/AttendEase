package com.example.attendease

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.math.RoundingMode


class DrShowAttendanceFragment : Fragment() {

    private val  db = Firebase.firestore

    val sessionsIds = mutableListOf<String>()
    val AllStudentsId = mutableListOf<String>()
    val AllStudentsName = mutableListOf<String>()
    val stdAttIds = mutableListOf<String>()
    val AllStdAttIds = mutableListOf<String>()
    lateinit var materialDesc:TextView
    lateinit var nbOfLectures:TextView
    lateinit var stdInfo:TextView
    lateinit var  materialId:String

    val isDataRetrieved:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_dr_show_attendance, container, false)

        materialDesc = view.findViewById<TextView>(R.id.MaterialDesc)
        nbOfLectures = view.findViewById<TextView>(R.id.nbOfLectures)
        stdInfo = view.findViewById<TextView>(R.id.StdInfo)


        materialId = DrShowAttendanceFragmentArgs.fromBundle(requireArguments()).materialId
        Toast.makeText(view.context,"$materialId",Toast.LENGTH_SHORT).show()

        // get name and coe for material
        db.collection("Materials").document(materialId).get().addOnSuccessListener {
            val name = it.getString("Material Name")
            val code = it.getString("Material Code")
            if(name != null && code != null  )
            {
                materialDesc.text="$name - $code"
//                Toast.makeText(view.context,"$name  $code",Toast.LENGTH_SHORT).show()
            }
        }
        // get all student id and name from db
        db.collection("Students")
            .orderBy("Name")
            .get().addOnSuccessListener { result ->
            AllStudentsName.clear()
            AllStudentsId.clear()
            for (document in result.documents) {
                val name = document.getString("Name")
                val id = document.id
                if (name != null) {
                    AllStudentsName.add(name)
                    AllStudentsId.add(id)
                }
            }
//            Toast.makeText(requireContext(),AllStudentsId.toString(),Toast.LENGTH_SHORT).show()

            getAllSessionId()

        }// end getting students



        return view
    }
    fun getAllSessionId(){

        db.collection("Sessions").whereEqualTo("Material Id", materialId)
            .addSnapshotListener { value, error ->
                sessionsIds.clear()
                for (document in value!!.documents) {
                    val getSessionId = document.id
                    sessionsIds.add(getSessionId)
                }
                nbOfLectures.text = "Number Of Lectures : ${sessionsIds.count()}"
//                Toast.makeText(requireContext(),"nb of lec done",Toast.LENGTH_SHORT).show()

                // we have all sessionId
                getAllStdIdFromAtt()// get all the std id
//                Toast.makeText(requireContext(),"after call get all std from att",Toast.LENGTH_SHORT).show()

            }
    }
    fun getAllStdIdFromAtt() {
        // Track the number of sessions to process
        var sessionsToProcess = sessionsIds.size
        Toast.makeText(requireContext(),sessionsToProcess.toString(),Toast.LENGTH_SHORT).show()


        // Clear AllStdAttIds before starting
        AllStdAttIds.clear()

        for (sessId in sessionsIds) {
            getAllStdBySession(sessId) {
                // Decrement counter after each session's data retrieval
                sessionsToProcess--
                //Toast.makeText(requireContext(),sessionsToProcess,Toast.LENGTH_SHORT).show()

                // Call getDistinctStdId only after all sessions are processed
                if (sessionsToProcess == 0) {
                    getDistinctStdId()
                }
            }
        }
    }

    fun getAllStdBySession(sessId: String, callback: () -> Unit) {
        db.collection("Attendances").whereEqualTo("Session Id", sessId).get()
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
            }
    }


    private fun getDistinctStdId(){
        // get distinct std id
        Toast.makeText(requireContext(),"in get distinct std id",Toast.LENGTH_SHORT).show()

        val checkboxContainer = view?.findViewById<LinearLayout>(R.id.stdAtt)
        db.collection("Students Materials").whereEqualTo("Material Id",materialId).get()
            .addOnSuccessListener {
                for(stdMat in it.documents)
                {   val stdid=stdMat.getString("Student Id")

                    if(stdid != null)
                    {
                        stdAttIds.add(stdid)
                        //Toast.makeText(view.context,"$stdid",Toast.LENGTH_SHORT).show()
                        val stdindex = AllStudentsId.indexOf(stdid)
                        val name = AllStudentsName[stdindex]
//                stdInfo.append( "$name $stdid \n  ")
                        var nbOfAtt:Int=0
                        for(i in AllStdAttIds){
                            if(i == stdid)
                            {

                                nbOfAtt+=1
                            }
                        }
                        Toast.makeText(requireContext(),nbOfAtt.toString(),Toast.LENGTH_SHORT).show()

                        val perc = (nbOfAtt.toFloat() / sessionsIds.count().toFloat()) * 100.0f
                        val nbWithTwoDecimals = perc * 100.0f
                        // Cast to an integer to remove the decimal part
                        val roundedInt = nbWithTwoDecimals.toInt()
                        // Divide by 100.0f to get the original value with two decimals
                        val formattedNb = roundedInt / 100.0f
                        val textView = TextView(requireContext())
                        textView.text = "$name - $stdid : $nbOfAtt  $formattedNb %"
                        textView.textSize = 18f
                        if (perc >= 50)
                            textView.setTextColor(Color.parseColor("#0c5e38"))
                        else
                            textView.setTextColor(Color.parseColor("#da251c"))

                        checkboxContainer?.addView(textView)
                    }
                }
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(),"failed to retreive std materials",Toast.LENGTH_SHORT).show()

            }
    }
//        stdInfo.append(stdAttIds.toString())

    // stdInfo.append(AllStudentsName.toString())// awal chi 5lst

}