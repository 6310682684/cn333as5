package com.example.phoneapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "Name") val title: String,
    @ColumnInfo(name = "Numbers") val content: String,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_NOTES = listOf(
            PhoneDbModel(1, "Anna", "09X - XXX - XXXXX", 5,  1,false),
            PhoneDbModel(2, "Bob", "08X - XXX - XXXX", 6, 2,false),
            PhoneDbModel(3, "Peter", "08X - XXX -XXXX", 2,  3,false),
        )
    }
}