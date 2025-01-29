package com.example.visionary.DataClass

import android.os.Parcel
import android.os.Parcelable

data class UserData(
    var name:String?="",
    var email:String?="",
    var userName:String?="",
    var bio:String?="",
    var userId:String?="",
    var profileImage:String?="",
    var professon:String?="",
    var password:String?="",
    var isSubscribe:String?=""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(userName)
        parcel.writeString(bio)
        parcel.writeString(userId)
        parcel.writeString(profileImage)
        parcel.writeString(professon)
        parcel.writeString(password)
        parcel.writeString(isSubscribe)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}