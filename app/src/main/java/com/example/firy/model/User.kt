package com.example.firy.model

data class User(
    val name: String = "",
    val bio: String = "",
    val profilePicturePath: String? = null,
    val registrationTokens : MutableList<String> = mutableListOf()
)