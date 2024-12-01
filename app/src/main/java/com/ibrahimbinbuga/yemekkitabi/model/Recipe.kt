package com.ibrahimbinbuga.yemekkitabi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe (

    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "ingredients")
    var ingredients: String,
    @ColumnInfo(name = "image")
    var image: ByteArray?
){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}