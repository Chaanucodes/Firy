package com.example.firy.model

data class ChatChannel(
    val userIds : MutableList<String>
) {
    constructor() : this(mutableListOf())
}