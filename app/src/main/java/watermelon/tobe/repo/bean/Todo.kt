/*
package watermelon.tobe.repo.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

*/
/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/19 10:30
 *//*

@Entity
data class Todo(
    val completeDate: String = "",
    val completeDateStr: String = "",
    var content: String = "",
    val date: Long = 0,
    val dateStr: String = "",
    @PrimaryKey
    val id: Long = 0,
    val priority: Int = 0,
    val status: Int = 0,
    var title: String = "",
    val type: Long = 0,
    val userId: Int = 0
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(completeDate)
        parcel.writeString(completeDateStr)
        parcel.writeString(content)
        parcel.writeLong(date)
        parcel.writeString(dateStr)
        parcel.writeLong(id)
        parcel.writeInt(priority)
        parcel.writeInt(status)
        parcel.writeString(title)
        parcel.writeLong(type)
        parcel.writeInt(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Todo> {
        override fun createFromParcel(parcel: Parcel): Todo {
            return Todo(parcel)
        }

        override fun newArray(size: Int): Array<Todo?> {
            return arrayOfNulls(size)
        }
    }
}*/
