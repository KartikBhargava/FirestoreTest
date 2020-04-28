package com.example.firestoretest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ForegroundService : Service() {

    private val CHANNEL_ID = "ForegroundService Kotlin"
    private var user: User? = null
    private var hashMapUserDetails: HashMap<String, User> = HashMap<String, User>()
    private var workplaceLatLong: HashMap<String, UserObj> = HashMap()
    private var residenceLatLong: HashMap<String, UserObj> = HashMap()
    private var hometownLatLong: HashMap<String, UserObj> = HashMap()
    private var workplacePincode: HashMap<String, HashMap<String, UserObj>> = HashMap()
    private var residencePincode: HashMap<String, HashMap<String, UserObj>> = HashMap()
    private var hometownPincode: HashMap<String, HashMap<String, UserObj>> = HashMap()
    private var hashMapFinalWorkplace: HashMap<String, HashMap<String, HashMap<String, UserObj>>> =
        HashMap()
    private var hashMapFinalResidence: HashMap<String, HashMap<String, HashMap<String, UserObj>>> =
        HashMap()
    private var hashMapFinalHometown: HashMap<String, HashMap<String, HashMap<String, UserObj>>> =
        HashMap()
    private var hashMapLocation: HashMap<String, ArrayList<HashMap<String, HashMap<String, HashMap<String, UserObj>>>> > =
        HashMap()
    private var listLocationType:ArrayList<HashMap<String, HashMap<String, HashMap<String, UserObj>>>> = ArrayList()

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
        var job = GlobalScope.launch {
            getUserDetails()
            getLocationDetails()
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

    private suspend fun getLocationDetails() {
        var regionQuery = FirebaseUtil.fb.collection("Location").get().await()
        var regionDocument = regionQuery.documents
        for (documentRegion in regionDocument) {
            var regionId = documentRegion.id
            var pincodeQuery =
                FirebaseUtil.fb.collection("Location").document(regionId).collection("pincodes")
                    .get().await()
            var pincodeDocument = pincodeQuery.documents
            for (documentPinCode in pincodeDocument) {
                var pinCodeID = documentPinCode.id
                var workplaceQuery = FirebaseUtil.fb.collection("Location").document(regionId)
                    .collection("pincodes")
                    .document(pinCodeID).collection("type").whereEqualTo("type", "workplace").get()
                    .await()
                var residenceQuery = FirebaseUtil.fb.collection("Location").document(regionId)
                    .collection("pincodes")
                    .document(pinCodeID).collection("type").whereEqualTo("type", "residence").get()
                    .await()
                var hometownQuery = FirebaseUtil.fb.collection("Location").document(regionId)
                    .collection("pincodes")
                    .document(pinCodeID).collection("type").whereEqualTo("type", "hometown").get()
                    .await()
                var workplaceDocuments = workplaceQuery.documents
                for (documentWorkplace in workplaceDocuments) {
                    var workplaceId = documentWorkplace.id
                    var workplaceUserQuery =
                        FirebaseUtil.fb.collection("Location").document(regionId)
                            .collection("pincodes")
                            .document(pinCodeID).collection("type")
                            .document(workplaceId)
                            .collection("users")
                            .get().await()
                    var workplaceUserUid = workplaceUserQuery.documents
                    for (documentWorkplace in workplaceUserUid) {
                        var workplaceUIDId = documentWorkplace.id
                        var latLngQuery = FirebaseUtil.fb.collection("Location").document(regionId)
                            .collection("pincodes")
                            .document(pinCodeID).collection("type")
                            .document(workplaceId)
                            .collection("users").document(workplaceUIDId)
                            .get().await()
                        var lat_long = lat_long(
                            latLngQuery.get("Latitude") as Double,
                            latLngQuery.get("Longitude") as Double
                        )
                        var latLng = LatLng(lat_long.latitude!!, lat_long.longitude!!)
                        var user = hashMapUserDetails[workplaceUIDId]
                        var userObj = UserObj(user, latLng)
                        workplaceLatLong.put(workplaceUIDId, userObj)
                    }
                }
                var residenceDocuments = residenceQuery.documents
                for (documentResidence in residenceDocuments) {
                    var residenceId = documentResidence.id
                    var residenceUserQuery =
                        FirebaseUtil.fb.collection("Location").document(regionId)
                            .collection("pincodes")
                            .document(pinCodeID).collection("type")
                            .document(residenceId)
                            .collection("users")
                            .get().await()
                    var residenceUserUid = residenceUserQuery.documents
                    for (documentWorkplace in residenceUserUid) {
                        var residenceUIDId = documentWorkplace.id
                        var latLngQuery = FirebaseUtil.fb.collection("Location").document(regionId)
                            .collection("pincodes")
                            .document(pinCodeID).collection("type")
                            .document(residenceId)
                            .collection("users").document(residenceUIDId)
                            .get().await()
                        var lat_long = lat_long(
                            latLngQuery.get("Latitude") as Double,
                            latLngQuery.get("Longitude") as Double
                        )
                        var latLng = LatLng(lat_long.latitude!!, lat_long.longitude!!)
                        var user = hashMapUserDetails[residenceUIDId]
                        var userObj = UserObj(user, latLng)
                        residenceLatLong.put(residenceUIDId, userObj)
                    }
                }
                var hometownDocuments = hometownQuery.documents
                for (documentHometowm in hometownDocuments) {
                    var hometownId = documentHometowm.id
                    var hometownUserQuery =
                        FirebaseUtil.fb.collection("Location").document(regionId)
                            .collection("pincodes")
                            .document(pinCodeID).collection("type")
                            .document(hometownId)
                            .collection("users")
                            .get().await()
                    var hometownUserUid = hometownUserQuery.documents
                    for (documenthometown in hometownUserUid) {
                        var hometownUIDId = documenthometown.id
                        var latLngQuery = FirebaseUtil.fb.collection("Location").document(regionId)
                            .collection("pincodes")
                            .document(pinCodeID).collection("type")
                            .document(hometownId)
                            .collection("users").document(hometownUIDId)
                            .get().await()
                        var lat_long = lat_long(
                            latLngQuery.get("Latitude") as Double,
                            latLngQuery.get("Longitude") as Double
                        )
                        var latLng = LatLng(lat_long.latitude!!, lat_long.longitude!!)
                        var user = hashMapUserDetails[hometownUIDId]
                        var userObj = UserObj(user, latLng)
                        hometownLatLong.put(hometownUIDId, userObj)

                    }
                }
                if (residenceQuery.documents.isEmpty() && hometownQuery.documents.isEmpty()) {
                    workplacePincode.put(pinCodeID, workplaceLatLong)
                } else if (workplaceQuery.documents.isEmpty() && hometownQuery.documents.isEmpty()) {
                    residencePincode.put(pinCodeID, residenceLatLong)
                } else if (workplaceQuery.documents.isEmpty() && residenceQuery.documents.isEmpty()) {
                    hometownPincode.put(pinCodeID, residenceLatLong)
                }
            }
        }
        hashMapFinalWorkplace["workplace"] = workplacePincode
        hashMapFinalHometown["hometown"] = hometownPincode
        hashMapFinalResidence["residence"] = residencePincode
        listLocationType.add(hashMapFinalWorkplace)
        listLocationType.add(hashMapFinalResidence)
        listLocationType.add(hashMapFinalHometown)
        hashMapLocation["Location"] = listLocationType
        Log.d("hello", "hello")
        val intent2 = Intent()
        intent2.action = "me.proft.sendbroadcast"
        intent2.putExtra("Location",hashMapLocation)
        sendBroadcast(intent2)

    }

    private suspend fun getUserDetails() {
        var uidQuery = FirebaseUtil.fb.collection("Users").get().await()
        var userDocuments = uidQuery.documents
        for (document in userDocuments) {
            var userId = document.id
            var userQuery = FirebaseUtil.fb.collection("Users").document(userId).get().await()
            user = userQuery.toObject(User::class.java)
            hashMapUserDetails[userId] = user!!
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

