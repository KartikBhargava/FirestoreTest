package com.example.firestoretest

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fb: FirebaseFirestore
    var data: HashMap<String, String> = HashMap<String, String>()
    lateinit var button: Button
    var idList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fb = FirebaseFirestore.getInstance()
        button = findViewById(R.id.button)

        data.put("name", "Kartik")
        data.put("Hello", "Hello")
//        fb.collection("Location").document("81").collection("813001").document("8174033803").set(data as Map<String, Any>)
//        fbb.collection("Location").document("27").collection("273001").document("8174033703").set(data as Map<String, Any>)
//        fb.collection("Location")
//            .get()
//            .addOnSuccessListener {
//                it.forEach {
//                    var id = it.id
//                    fb.collection("Location").document(id).get().addOnSuccessListener { i1t ->
//                        var id2 = i1t.id
//                        Toast.makeText(this,id2 , Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//            }

    }

    override fun onResume() {
        super.onResume()
        button.setOnClickListener {
            fb.collection("Location")
                .get()
                .addOnCompleteListener {
                    for (document in it.result!!) {
                        idList.add(document.id)
                    }
                    idList.forEach {
                        fb.document("Location" + "/$it").get().addOnCompleteListener {
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }


    override fun onPause() {
        super.onPause()
        button.setOnClickListener(null)
    }
}
