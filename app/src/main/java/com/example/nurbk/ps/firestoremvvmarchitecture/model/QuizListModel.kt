package com.example.nurbk.ps.firestoremvvmarchitecture.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

class QuizListModel(
    @DocumentId val quiz_id: String,
    val name: String,
    val desc: String,
    val image: String,
    val level: String,
    val visibility: String,
    val questions: Long
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()
    ) {
    }

    constructor() :
            this(
                "",
                "",
                "",
                "",
                "",
                "",
                0
            )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(quiz_id)
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeString(image)
        parcel.writeString(level)
        parcel.writeString(visibility)
        parcel.writeLong(questions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizListModel> {
        override fun createFromParcel(parcel: Parcel): QuizListModel {
            return QuizListModel(parcel)
        }

        override fun newArray(size: Int): Array<QuizListModel?> {
            return arrayOfNulls(size)
        }
    }

}