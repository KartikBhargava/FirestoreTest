package com.example.firestoretest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.*
import com.google.gson.Gson
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var locations_broadcast: BroadcastReceiver
    var location_hashmap: HashMap<String, ArrayList<region>> = HashMap<String, ArrayList<region>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        ForegroundService.startService(this, "Start Service")
        locations_broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                makeText(this@MainActivity, "recieved", LENGTH_SHORT).show()
                ForegroundService.stopService(this@MainActivity)
              //  location_hashmap =
              //      intent.getSerializableExtra("map") as HashMap<String, ArrayList<region>>
              //  var gson = Gson()
              //  var jsonobject = gson.toJson(location_hashmap)
             //   val file: String = "FireStoreTest"
                button.setOnClickListener {
                    val fileOutputStream: FileOutputStream
                    try {
                  //      fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)
                   //     fileOutputStream.write(data.toByteArray())
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
