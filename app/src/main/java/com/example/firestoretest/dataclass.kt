package com.example.firestoretest

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class About_data(
    @SerializedName("yourself") val yourself: String?,
    @SerializedName("help") val help: String?
) : Parcelable

@Parcelize
data class User(
    @SerializedName("name") var name: String? = null,
    @SerializedName("age") var age: String? = null,
    @SerializedName("expertise") var expertise: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("profession") var profession: String? = null
) : Parcelable

@Parcelize
data class lat_long(
    @SerializedName("id") val id: String?,
    @SerializedName("latLng") val latLng: LatLng?

) : Parcelable

@Parcelize
data class location_type(
    @SerializedName("locationtype") val locationtype: String?,
    @SerializedName("latlong") val latLong: lat_long?
) : Parcelable

@Parcelize
data class pincode(
    @SerializedName("locationType") val locationType: location_type?,
    @SerializedName("pinCode") val pinCode: String?
) : Parcelable

@Parcelize
data class region(
    @SerializedName("pinCode") val pinCode: pincode?,
    @SerializedName("region") val region: String?
) : Parcelable

@Parcelize
data class location(
    @SerializedName("region") var region: region?
) : Parcelable