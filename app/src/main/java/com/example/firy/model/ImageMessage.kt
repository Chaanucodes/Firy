package com.example.firy.model

import java.util.*

data class ImageMessage (
    val imagePath: String,
    override val time: Date,
    override val senderId: String,
    override val type: String = MessageType.IMAGE,
    override val recipient: String,
    override val senderName: String
) : Message{
    constructor() : this("", Date(0), "","","","")
}