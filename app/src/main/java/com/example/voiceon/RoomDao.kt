package com.example.voiceon

import androidx.room.*

@Dao
interface RoomDao {
    @Query("SELECT * FROM room")
    fun getAll(): List<Room>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRooms(vararg rooms: Room)

    @Delete
    fun deleteRoom(entity: Room)
}

@Database(entities = [Room::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
}