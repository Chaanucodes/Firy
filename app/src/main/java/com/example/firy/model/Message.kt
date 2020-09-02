package com.example.firy.model

import java.util.*

object MessageType{
    const val IMAGE = "IMAGE"
    const val TEXT = "TEXT"
}

interface Message {
    val time: Date
    val senderId: String
    val type: String

}