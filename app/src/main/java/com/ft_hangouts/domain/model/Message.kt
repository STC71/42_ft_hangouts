package com.ft_hangouts.domain.model

enum class MessageDirection {
    INCOMING,
    OUTGOING
}

data class Message(
    val id: Long = 0,
    val contactId: Long,
    val sender: String,
    val recipient: String,
    val body: String,
    val timestamp: Long,
    val direction: MessageDirection,
    val status: String = "PENDING"
)
