package com.ft_hangouts.data.repository

import android.content.ContentValues
import android.content.Context
import com.ft_hangouts.data.database.DatabaseContract
import com.ft_hangouts.data.database.DatabaseHelper
import com.ft_hangouts.domain.model.Message
import com.ft_hangouts.domain.model.MessageDirection

class MessageRepository(context: Context) {

    private val helper = DatabaseHelper(context.applicationContext)

    fun insert(message: Message): Long {
        val values = ContentValues().apply {
            put(DatabaseContract.Messages.CONTACT_ID, message.contactId)
            put(DatabaseContract.Messages.SENDER, message.sender)
            put(DatabaseContract.Messages.RECIPIENT, message.recipient)
            put(DatabaseContract.Messages.BODY, message.body)
            put(DatabaseContract.Messages.TIMESTAMP, message.timestamp)
            put(DatabaseContract.Messages.DIRECTION, message.direction.name)
            put(DatabaseContract.Messages.STATUS, message.status)
        }

        return helper.writableDatabase.insertOrThrow(
            DatabaseContract.Messages.TABLE,
            null,
            values
        )
    }

    fun getConversation(contactId: Long): List<Message> {
        val messages = mutableListOf<Message>()

        helper.readableDatabase.query(
            DatabaseContract.Messages.TABLE,
            null,
            "${DatabaseContract.Messages.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null,
            null,
            "${DatabaseContract.Messages.TIMESTAMP} ASC, " +
                "${DatabaseContract.Messages.ID} ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                messages.add(
                    Message(
                        id = cursor.getLong(0),
                        contactId = cursor.getLong(1),
                        sender = cursor.getString(2),
                        recipient = cursor.getString(3),
                        body = cursor.getString(4),
                        timestamp = cursor.getLong(5),
                        direction = MessageDirection.valueOf(
                            cursor.getString(6)
                        ),
                        status = cursor.getString(7)
                    )
                )
            }
        }

        return messages
    }

    fun updateStatus(id: Long, status: String): Int {
        val values = ContentValues().apply {
            put(DatabaseContract.Messages.STATUS, status)
        }

        return helper.writableDatabase.update(
            DatabaseContract.Messages.TABLE,
            values,
            "${DatabaseContract.Messages.ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteConversation(contactId: Long): Int {
        return helper.writableDatabase.delete(
            DatabaseContract.Messages.TABLE,
            "${DatabaseContract.Messages.CONTACT_ID} = ?",
            arrayOf(contactId.toString())
        )
    }

    fun close() {
        helper.close()
    }
}
