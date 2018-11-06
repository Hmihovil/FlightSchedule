package io.capsella.almasii.imagefeeds.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class Database {

    private val TAG = Database::class.java.simpleName

    private var context: Context
    private var ourHandler: DbHandler? = null
    private var sqliteDatabase: SQLiteDatabase? = null

    constructor(context: Context) {
        this.context = context
        ourHandler = DbHandler(this.context, DATABASE_NAME, null, DATABASE_VERSION)
    }

    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "almasii_database"

        //TABLE NAMES
        const val TABLE_IMAGES = "images"

        //COLUMN NAMES
        const val COLUMN_ID = "id"
        const val COLUMN_URL = "url"
        const val COLUMN_DOWNLOAD_TOKEN = "downloadToken"
        const val COLUMN_NAME = "name"
        const val COLUMN_CREATED_AT_MILLIS = "createdAtMillis"
    }

    private inner class DbHandler(context: Context, DATABASE_NAME: String, k: SQLiteDatabase.CursorFactory?, DATABASE_VERSION: Int)
        : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            // TODO Auto-generated method stub
            createDatabases(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // TODO Auto-generated method stub
            dropDatabases(db)
            onCreate(db)
        }
    }

    @Throws(SQLiteConstraintException::class)
    fun createDatabases(db: SQLiteDatabase) {

        //IMAGES TABLE
        db.execSQL("CREATE TABLE $TABLE_IMAGES ( " +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_URL TEXT NOT NULL, " +
                "$COLUMN_DOWNLOAD_TOKEN TEXT NOT NULL, " +
                "$COLUMN_NAME TEXT NOT NULL, " +
                "$COLUMN_CREATED_AT_MILLIS INTEGER NOT NULL);")
    }

    @Throws(SQLiteConstraintException::class)
    fun dropDatabases(db: SQLiteDatabase) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_IMAGES")
    }

    @Throws(SQLiteConstraintException::class)
    fun open(): Database {

        if (sqliteDatabase == null) {
            sqliteDatabase = ourHandler!!.writableDatabase
        } else if (!sqliteDatabase!!.isOpen) {
            sqliteDatabase = ourHandler!!.writableDatabase
        }

        return this
    }

    fun getSQLiteDatabase(): SQLiteDatabase {
        return sqliteDatabase!!
    }

    @Throws(SQLiteConstraintException::class)
    fun close() {
        sqliteDatabase!!.close()
    }

    @Throws(SQLiteConstraintException::class)
    fun clear() {
        sqliteDatabase!!.delete(TABLE_IMAGES, null, null)
    }

    fun saveImages(jsonArray: JSONArray): Boolean {
        try {

            val cursor = sqliteDatabase!!.query(TABLE_IMAGES, null, null, null, null, null, null)
            sqliteDatabase!!.beginTransaction()

            for (i in 0 until jsonArray.length()) {
                Log.d(TAG, "Saving ${i + 1} of ${jsonArray.length()} images")
                insertImage(jsonArray.getJSONObject(i))
            }

            sqliteDatabase!!.setTransactionSuccessful()
            sqliteDatabase!!.endTransaction()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun insertImage(jsonObject: JSONObject): Boolean {

        return try {

            val values = ContentValues()
            values.put(COLUMN_URL, jsonObject.getString(COLUMN_URL))
            values.put(COLUMN_DOWNLOAD_TOKEN, jsonObject.getString(COLUMN_DOWNLOAD_TOKEN))
            values.put(COLUMN_NAME, jsonObject.getString(COLUMN_NAME))
            values.put(COLUMN_CREATED_AT_MILLIS, jsonObject.getLong(COLUMN_CREATED_AT_MILLIS))

            val id = sqliteDatabase!!.insertOrThrow(TABLE_IMAGES, null, values)
            Log.d(TAG, "Image (${jsonObject.getString(COLUMN_NAME)}) with Created At - ${jsonObject.getInt(COLUMN_CREATED_AT_MILLIS)} saved.")

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
