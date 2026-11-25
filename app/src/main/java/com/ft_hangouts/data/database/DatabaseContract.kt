package com.ft_hangouts.data.database

object DatabaseContract {

    const val DATABASE_NAME = "ft_hangouts.db"
    const val DATABASE_VERSION = 1

    object Contacts {
        const val TABLE = "contacts"

        const val ID = "id"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val PHONE = "phone"
        const val EMAIL = "email"
        const val ADDRESS = "address"
        const val NOTES = "notes"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }

    object Messages {
        const val TABLE = "messages"

        const val ID = "id"
        const val CONTACT_ID = "contact_id"
        const val SENDER = "sender"
        const val RECIPIENT = "recipient"
        const val BODY = "body"
        const val TIMESTAMP = "timestamp"
        const val DIRECTION = "direction"
        const val STATUS = "status"
    }
}
