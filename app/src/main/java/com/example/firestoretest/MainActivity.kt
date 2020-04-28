package com.example.firestoretest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var locations_broadcast: BroadcastReceiver
    private var hashMapLocation: HashMap<String, ArrayList<HashMap<String, HashMap<String, HashMap<String, UserObj>>>> > =
        HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        ForegroundService.startService(this, "Start Service")
        locations_broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                makeText(this@MainActivity, "recieved", LENGTH_SHORT).show()
                ForegroundService.stopService(this@MainActivity)
             hashMapLocation =
                  intent.getSerializableExtra("Location") as HashMap<String, ArrayList<HashMap<String, HashMap<String, HashMap<String, UserObj>>>> >
                var gson = Gson()
                var jsonFile = gson.toJson(hashMapLocation)
                button.setOnClickListener {
                    //Get your FilePath and use it to create your File
                    //Get your FilePath and use it to create your File
                    val yourFilePath: String = "$filesDir/FireStoreTest"
                    val yourFile = File(yourFilePath)
//Create your FileOutputStream, yourFile is part of the constructor
                    val fileOutputStream = FileOutputStream(yourFile)
                    makeText(this@MainActivity, "recieved", LENGTH_SHORT).show()
//Convert your JSON String to Bytes and write() it
                    fileOutputStream.write(jsonFile.toByteArray())
//Finally flush and close your FileOutputStream
                    fileOutputStream.flush()
                    fileOutputStream.close()
//make sure this is in a try catch statement
                }
            }
        }
                val filter = IntentFilter()
                filter.addAction("me.proft.sendbroadcast")
                registerReceiver(locations_broadcast, filter)

            }

            override fun onPause() {
                super.onPause()
                button.setOnClickListener(null)
            }

            override fun onDestroy() {
                super.onDestroy()
                unregisterReceiver(locations_broadcast)
            }
        }
