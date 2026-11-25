package com.ft_hangouts.domain.model

data class Contact(
    val id: Long = 0,
    val firstName: String,
    val lastName: String = "",
    val phone: String,
    val email: String = "",
    val address: String = "",
    val notes: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)
