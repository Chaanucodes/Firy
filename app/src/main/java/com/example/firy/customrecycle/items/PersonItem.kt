package com.example.firy.customrecycle.items

import android.content.Context
import com.example.firy.R
import com.example.firy.gliding.GlideApp
import com.example.firy.model.User
import com.example.firy.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_person.*
import java.security.AccessControlContext

data class PersonItem(
    val person : User,
    val userID : String,
    private val context: Context
) : Item(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.textViewConvoName.text = person.name
        viewHolder.textViewConvoBio.text = person.bio
        if(person.profilePicturePath!=null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePicturePath))
                .placeholder(R.drawable.ic_my_acc_nav)
                .into(viewHolder.imageViewConvoProfilePic)
        }
    }


override fun getLayout(): Int = R.layout.item_person
}