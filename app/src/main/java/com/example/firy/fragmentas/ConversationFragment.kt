package com.example.firy.fragmentas

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firy.AppConstants
import com.example.firy.ChatActivity
import com.example.firy.R
import com.example.firy.customrecycle.items.PersonItem
import com.example.firy.databinding.FragmentConversationBinding
import com.example.firy.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.*
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.fragment_conversation.*

const val INTENT_FOR_CONVERSATIONSNAME = "INTENT_FOR_CONVERSATIONSNAME"
class ConversationFragment : Fragment() {

    private lateinit var binding : FragmentConversationBinding
    private lateinit var userListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var peopleSection : Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation, container, false)

        userListenerRegistration = FirestoreUtil.addUsersListener(this.activity!!, this::updateRecyclerView)
        return binding.root
    }

    private fun updateRecyclerView(items : List<Item>){
        fun init(){
            recyclerViewConvo.apply {
                layoutManager = LinearLayoutManager(this@ConversationFragment.context)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }

            //TODO: RESO CODER didn't comment the below line. However, using it, users are not able to fetch new users in the conversation screen
//            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if (shouldInitRecyclerView) init()
        else updateItems()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "SPLASH (beta)"
    }

    private val onItemClick = OnItemClickListener{item, view->
        if(item is PersonItem){
            startActivity(Intent(activity, ChatActivity::class.java).apply {
                putExtra(
                    //TODO: Need to check on this later as ANKO not working and we cannot pass map in Intents
                    AppConstants.USER_NAME, item.person.name
                )
                putExtra(
                    AppConstants.USER_ID, item.userID
                )
            })
        }

    }

}