package com.ibrahimbinbuga.yemekkitabi.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ibrahimbinbuga.yemekkitabi.model.Recipe

@Dao
interface RecipeDAO {
    @Query("SELECT * FROM Recipe")
    fun getAll(): List<Recipe>

    @Query("SELECT * FROM Recipe WHERE id = :id")
    fun findById(id: Int): Recipe

    @Insert
    fun insert(recipe: Recipe)

    @Delete
    fun delete(recipe: Recipe)
}