package com.example.firestoretest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    var fb = FirebaseFirestore.getInstance()
    var data :HashMap<String, String> = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        data.put("name", "Kartik")
        data.put("Hello", "Hello")
        fb.collection("Location").document("27").collection("273001").document("8174033803").set(data as Map<String, Any>)

        fb.collection("Location")
                .get()
                .addOnCompleteListener {
                    for(document in it.result!!) {
                     var id = document.id
                        fb.collection("Location").document(id).get().addOnSuccessListener { i1t ->
                            var id2 = i1t.id
                            Toast.makeText(this,id2 , Toast.LENGTH_SHORT).show()
                        }
                    }

                }
    }
}
