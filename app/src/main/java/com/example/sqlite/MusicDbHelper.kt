package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqlite.ActivityLogDatabaseHelper.Companion.DATABASE_NAME
import com.example.sqlite.ActivityLogDatabaseHelper.Companion.DATABASE_VERSION

// Define the table schema for storing music data
private const val TABLE_NAME = "music"
private const val COLUMN_ID = "id"
private const val COLUMN_TITLE = "title"
private const val COLUMN_FILE_PATH = "file_path"

// Create a helper class for managing the database
class MusicDbHelper(context: Context) : SQLiteOpenHelper(context, "music.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the music table
        val createTableSql = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_FILE_PATH TEXT)"
        db.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle upgrading the database schema
    }

    // Insert a new music record into the database
    fun insertMusic(title: String, filePath: String) {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_FILE_PATH, filePath)
        }
        writableDatabase.insert(TABLE_NAME, null, values)
    }

    // Delete all music records from the database
    fun deleteAllMusic() {
        writableDatabase.delete(TABLE_NAME, null, null)
    }

    // Query the database for music records
    fun getMusic(): ArrayList<Music> {
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = readableDatabase.rawQuery(query, null)

        val musicList = ArrayList<Music>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val filePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH))

            musicList.add(Music(title, filePath))
        }

        cursor.close()
        return musicList
    }
}
