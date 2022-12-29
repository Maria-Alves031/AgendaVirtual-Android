package com.example.agendavirtual.SQLiteHelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.agendavirtual.model.ContactModel
import java.lang.Exception

class SQLiteHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "agenda.db"
        private const val TBL_AGENDA = "tbl_agenda"
        private const val ID = "id"
        private const val NAME = "name"
        private const val PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTblAgenda = ("CREATE TABLE " + TBL_AGENDA + " ("
                + ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT," +
                PHONE + " TEXT" + ")")

        db?.execSQL(createTblAgenda)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_AGENDA")
        onCreate(db)
    }

    fun insertContact(contact: ContactModel): Long {
        val (db, contentValues) = writableDataBase(contact)
        val success = db.insert(TBL_AGENDA, null, contentValues)
        db.close()
        return success
    }

    fun updateContacts(contact: ContactModel): Int {
        val (db, contentValues) = writableDataBase(contact)
        val success = db.update(TBL_AGENDA, contentValues, "id=${contact.id}", null)
        db.close()
        return success
    }

    private fun writableDataBase(contact: ContactModel): Pair<SQLiteDatabase, ContentValues> {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, contact.id)
        contentValues.put(NAME, contact.name)
        contentValues.put(PHONE, contact.phone)
        return Pair(db, contentValues)
    }

    fun deleteContact(id: Int): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id)
        val success = db.delete(TBL_AGENDA,"id=$id", null )
        db.close()
        return success
    }

    @SuppressLint("Recycle", "Range")
    fun getAllContacts(): ArrayList<ContactModel> {
        val contactList: ArrayList<ContactModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_AGENDA"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var phone: String

        if (cursor.moveToNext()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("name"))
                phone = cursor.getString(cursor.getColumnIndex("phone"))
                val contact = ContactModel(id = id, name = name, phone = phone)
                contactList.add(contact)

            } while (cursor.moveToNext())
        }
        return contactList
    }

}