package com.example.voiceon

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Room
import com.example.voiceon.ContextUtil.Companion.context

class DBHelper private constructor(context:Context?) {
    private var db : AppDatabase? = null

    fun getDB() : AppDatabase? {
        if (db == null) {
            db = context?.applicationContext?.let {
                Room.databaseBuilder(
                    it,
                    AppDatabase::class.java, "room"
                ).build()
            }
        }
        return db
    }

    companion object {
        @Volatile private var instance: DBHelper? = null

        @JvmStatic fun getInstance(): DBHelper =
            instance ?: synchronized(this) {
                instance ?: DBHelper(context).also {
                    instance = it
                }
            }
    }
}