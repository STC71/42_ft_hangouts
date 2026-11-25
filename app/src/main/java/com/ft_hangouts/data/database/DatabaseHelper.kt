package com.ft_hangouts.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createContactsTable(db)
        createMessagesTable(db)
        createIndexes(db)
    }

    private fun createContactsTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${DatabaseContract.Contacts.TABLE} (
                ${DatabaseContract.Contacts.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.Contacts.FIRST_NAME} TEXT NOT NULL,
                ${DatabaseContract.Contacts.LAST_NAME} TEXT NOT NULL DEFAULT '',
                ${DatabaseContract.Contacts.PHONE} TEXT NOT NULL,
                ${DatabaseContract.Contacts.EMAIL} TEXT NOT NULL DEFAULT '',
                ${DatabaseContract.Contacts.ADDRESS} TEXT NOT NULL DEFAULT '',
                ${DatabaseContract.Contacts.NOTES} TEXT NOT NULL DEFAULT '',
                ${DatabaseContract.Contacts.CREATED_AT} INTEGER NOT NULL,
                ${DatabaseContract.Contacts.UPDATED_AT} INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    private fun createMessagesTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${DatabaseContract.Messages.TABLE} (
                ${DatabaseContract.Messages.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.Messages.CONTACT_ID} INTEGER NOT NULL,
                ${DatabaseContract.Messages.SENDER} TEXT NOT NULL,
                ${DatabaseContract.Messages.RECIPIENT} TEXT NOT NULL,
                ${DatabaseContract.Messages.BODY} TEXT NOT NULL,
                ${DatabaseContract.Messages.TIMESTAMP} INTEGER NOT NULL,
                ${DatabaseContract.Messages.DIRECTION} TEXT NOT NULL
                    CHECK (${DatabaseContract.Messages.DIRECTION}
                    IN ('INCOMING', 'OUTGOING')),
                ${DatabaseContract.Messages.STATUS} TEXT NOT NULL DEFAULT 'PENDING',
                FOREIGN KEY (${DatabaseContract.Messages.CONTACT_ID})
                    REFERENCES ${DatabaseContract.Contacts.TABLE}
                    (${DatabaseContract.Contacts.ID})
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    private fun createIndexes(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE INDEX idx_messages_contact_timestamp
            ON ${DatabaseContract.Messages.TABLE}
            (${DatabaseContract.Messages.CONTACT_ID},
            ${DatabaseContract.Messages.TIMESTAMP})
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX idx_contacts_phone
            ON ${DatabaseContract.Contacts.TABLE}
            (${DatabaseContract.Contacts.PHONE})
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        throw IllegalStateException(
            "Database migration required: $oldVersion -> $newVersion"
        )
    }
}
