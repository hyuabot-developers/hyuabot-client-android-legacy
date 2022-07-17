package app.kobuggi.hyuabot.ui.shuttle.timetable

import android.os.Parcel
import android.os.Parcelable

data class ShuttleTimetable(val stopID: Int, val shuttleType: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(stopID)
        parcel.writeString(shuttleType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShuttleTimetable> {
        override fun createFromParcel(parcel: Parcel): ShuttleTimetable {
            return ShuttleTimetable(parcel)
        }

        override fun newArray(size: Int): Array<ShuttleTimetable?> {
            return arrayOfNulls(size)
        }
    }
}
