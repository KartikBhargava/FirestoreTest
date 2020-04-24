package com.example.firestoretest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.toObject

class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    var list_user: ArrayList<User> = ArrayList<User>()
    var user: User? = null
    var latLong: lat_long? = null
    var locationType: location_type? = null
    var pincode: pincode? = null
    var region: region? = null
    var check: Boolean? = false
    var list_lat_long: ArrayList<lat_long> = ArrayList<lat_long>()
    var list_location_type: ArrayList<location_type> = ArrayList<location_type>()
    var list_pinCode: ArrayList<pincode> = ArrayList<pincode>()
    var list_region: ArrayList<region> = ArrayList<region>()

    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        FirebaseUtil().fb.collection("Location").get().addOnSuccessListener { it1 ->
            for (document in it1!!) {
                val id = document.id
                FirebaseUtil().fb.collection("Location").document(id).collection("pincodes")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document1 in result!!) {
                            val id1 = document1.id
                            FirebaseUtil().fb.collection("Location").document(id)
                                .collection("pincodes")
                                .document(id1).collection("type")
                                .get().addOnSuccessListener {
                                    for (document2 in it!!) {
                                        val id2 = document2.id
                                        FirebaseUtil().fb.collection("Location").document(id)
                                            .collection("pincodes")
                                            .document(id1).collection("type").document(id2)
                                            .collection("users")
                                            .get().addOnSuccessListener {
                                                for (document3 in it) {
                                                    val id3 = document3.id
                                                    val long: Double =
                                                        document3.data.get("Longitude") as Double
                                                    val lat: Double =
                                                        document3.data.get("Latitude") as Double
                                                    latLong =
                                                        lat_long(id2, lat, long)
                                                    list_lat_long.add(latLong!!)
                                                }
                                            }
                                            .addOnFailureListener {
                                                latLong = null
                                            }
                                        locationType = location_type(id2, latLong)
                                        list_location_type.add(locationType!!)
                                    }
                                }.addOnFailureListener {
                                    locationType = null
                                }
                            pincode = pincode(locationType, id1)
                            list_pinCode.add(pincode!!)
                        }
                    }.addOnFailureListener {
                        pincode = null
                    }
                region = region(pincode, id)
                list_region.add(region!!)
            }
        }
            .addOnFailureListener {
            region = null
        }
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service Kotlin Example")
            .setContentText(input)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        return START_STICKY
    }

    private fun add_data() {
        for (user in list_user)
        {

        }
    }

    private fun send() {
        val intent2 = Intent()
        intent2.action = "me.proft.sendbroadcast"
        intent2.putExtra("list_user", list_user)
        intent2.putExtra("list_lat_long", list_lat_long)
        intent2.putExtra("list_user", list_location_type)
        intent2.putExtra("list_pinCode", list_pinCode)
        intent2.putExtra("list_region", list_region)
        sendBroadcast(intent2)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

}

