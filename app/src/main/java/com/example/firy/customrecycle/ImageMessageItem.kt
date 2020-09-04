package com.example.firy.customrecycle

import android.content.Context
import com.example.firy.R
import com.example.firy.customrecycle.items.MessageItem
import com.example.firy.gliding.GlideApp
import com.example.firy.model.ImageMessage
import com.example.firy.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_image_message.*

class ImageMessageItem(
    val message: ImageMessage,
    val context: Context
) : MessageItem(message){

    override fun getLayout() = R.layout.item_image_message

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        GlideApp.with(context)
            .load(StorageUtil.pathToReference(message.imagePath))
            .placeholder(R.drawable.ic_splash)
            .into(viewHolder.imageViewImageItem)
    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is ImageMessageItem) return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as ImageMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }
}