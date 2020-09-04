package com.example.firy.customrecycle

import android.content.Context
import com.example.firy.R
import com.example.firy.customrecycle.items.MessageItem
import com.example.firy.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_text_message.*

class TextMessageItem(
    val message: TextMessage,
    val context: Context
) : MessageItem(message){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
       viewHolder.textViewMessageItem.text = message.text
        super.bind(viewHolder, position)
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is TextMessageItem) return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as TextMessageItem)
    }

    override fun getLayout(): Int = R.layout.item_text_message
    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }
}