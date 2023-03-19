package com.example.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ActivityLogDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "activity_log.db", null, 1) {

    companion object {
        const val DATABASE_NAME = "activity_log.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "activity_log"
        const val COLUMN_ID = "_id"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_ACTIVITY = "activity"
    }


    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_START_TIME TEXT, " +
                "$COLUMN_DURATION INTEGER, " +
                "$COLUMN_ACTIVITY TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not needed for this example
    }
}
