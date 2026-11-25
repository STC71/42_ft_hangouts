package com.ft_hangouts.data.repository

import android.content.ContentValues
import android.content.Context
import com.ft_hangouts.data.database.DatabaseContract
import com.ft_hangouts.data.database.DatabaseHelper
import com.ft_hangouts.domain.model.Contact

class ContactRepository(context: Context) {

    private val helper = DatabaseHelper(context.applicationContext)

    fun insert(contact: Contact): Long {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(DatabaseContract.Contacts.FIRST_NAME, contact.firstName)
            put(DatabaseContract.Contacts.LAST_NAME, contact.lastName)
            put(DatabaseContract.Contacts.PHONE, contact.phone)
            put(DatabaseContract.Contacts.EMAIL, contact.email)
            put(DatabaseContract.Contacts.ADDRESS, contact.address)
            put(DatabaseContract.Contacts.NOTES, contact.notes)
            put(DatabaseContract.Contacts.CREATED_AT, now)
            put(DatabaseContract.Contacts.UPDATED_AT, now)
        }

        return helper.writableDatabase.insertOrThrow(
            DatabaseContract.Contacts.TABLE,
            null,
            values
        )
    }

    fun getAll(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = helper.readableDatabase

        db.query(
            DatabaseContract.Contacts.TABLE,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseContract.Contacts.FIRST_NAME} ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                contacts.add(
                    Contact(
                        id = cursor.getLong(0),
                        firstName = cursor.getString(1),
                        lastName = cursor.getString(2),
                        phone = cursor.getString(3),
                        email = cursor.getString(4),
                        address = cursor.getString(5),
                        notes = cursor.getString(6),
                        createdAt = cursor.getLong(7),
                        updatedAt = cursor.getLong(8)
                    )
                )
            }
        }

        return contacts
    }

    fun getById(id: Long): Contact? {
        return getAll().firstOrNull { it.id == id }
    }

    fun update(contact: Contact): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.Contacts.FIRST_NAME, contact.firstName)
            put(DatabaseContract.Contacts.LAST_NAME, contact.lastName)
            put(DatabaseContract.Contacts.PHONE, contact.phone)
            put(DatabaseContract.Contacts.EMAIL, contact.email)
            put(DatabaseContract.Contacts.ADDRESS, contact.address)
            put(DatabaseContract.Contacts.NOTES, contact.notes)
            put(
                DatabaseContract.Contacts.UPDATED_AT,
                System.currentTimeMillis()
            )
        }

        return helper.writableDatabase.update(
            DatabaseContract.Contacts.TABLE,
            values,
            "${DatabaseContract.Contacts.ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    fun delete(id: Long): Int {
        return helper.writableDatabase.delete(
            DatabaseContract.Contacts.TABLE,
            "${DatabaseContract.Contacts.ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun close() {
        helper.close()
    }
}
