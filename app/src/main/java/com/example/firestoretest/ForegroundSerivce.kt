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
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.toObject

class ForegroundService : Service() {
    private val CHANNEL_ID = "ForegroundService Kotlin"
    var list_user: ArrayList<User> = ArrayList<User>()
    var user: User? = null
    private var latLong: lat_long? = null
    var locationType: location_type? = null
    var pincode: pincode? = null
    var region: region? = null
    var hashMapUserDetails: HashMap<String, User> = HashMap<String, User>()
    var hashMapLatLong: HashMap<String, lat_long> = HashMap<String, lat_long>()
    var hashMapLocationType: HashMap<String, HashMap<String, lat_long>> =
        HashMap<String, HashMap<String, lat_long>>()
    var hashMapPincode: HashMap<String, HashMap<String, HashMap<String, lat_long>>> =
        HashMap<String, HashMap<String, HashMap<String, lat_long>>>()
    var hashMapRegion: HashMap<String, HashMap<String, HashMap<String, HashMap<String, lat_long>>>> =
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, lat_long>>>>()
    var hashMapLocation: HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashMap<String, lat_long>>>>> =
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashMap<String, lat_long>>>>>()

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
        FirebaseUtil.fb.collection("Location").get().addOnSuccessListener { it1 ->
            for (document in it1!!) {
                val regionID = document.id
                FirebaseUtil.fb.collection("Location").document(regionID).collection("pincodes")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document1 in result!!) {
                            val pinCodeID = document1.id
                            FirebaseUtil.fb.collection("Location").document(regionID)
                                .collection("pincodes")
                                .document(pinCodeID).collection("type")
                                .get().addOnSuccessListener {
                                    for (document2 in it!!) {
                                        val typeOfLocationID = document2.id
                                        FirebaseUtil.fb.collection("Location").document(regionID)
                                            .collection("pincodes")
                                            .document(pinCodeID).collection("type")
                                            .document(typeOfLocationID)
                                            .collection("users")
                                            .get().addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    for (document3 in it.result!!) {
                                                        val userID = document3.id
                                                        fetchUserData(userID)
                                                        val long: Double =
                                                            document3.data.get("Longitude") as Double
                                                        val lat: Double =
                                                            document3.data.get("Latitude") as Double
                                                        val latLng = LatLng(lat, long)
                                                        latLong = lat_long(userID, latLng)
                                                        hashMapLatLong[userID] = latLong!!
                                                    }
                                                    hashMapLocation.put("Location", hashMapRegion)
                                                    val broadcastIntent = Intent()
                                                    broadcastIntent.action =
                                                        "me.proft.sendbroadcast"
                                                    broadcastIntent.putExtra("hashMapLocation", hashMapLocation)
                                                    sendBroadcast(broadcastIntent)
                                                }
                                            }
                                            .addOnFailureListener {
                                                latLong = null
                                            }

                                        hashMapLocationType[typeOfLocationID] = hashMapLatLong
                                    }
                                }.addOnFailureListener {
                                    locationType = null
                                }

                            hashMapPincode[pinCodeID] = hashMapLocationType
                        }
                    }.addOnFailureListener {
                        pincode = null
                    }
                hashMapRegion[regionID] = hashMapPincode
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

    private fun fetchUserData(userID: String) {
        FirebaseUtil.fb.collection("Users").document(userID).get().addOnSuccessListener {
            user = it.toObject<User>()
            hashMapUserDetails[userID] = user!!
        }
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

