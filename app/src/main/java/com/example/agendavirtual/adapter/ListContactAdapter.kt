package com.example.agendavirtual.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendavirtual.R
import com.example.agendavirtual.model.ContactModel

class ListContactAdapter: RecyclerView.Adapter<ListContactAdapter.ContactViewHolder>() {

    private var contactList: ArrayList<ContactModel> = ArrayList()
    private var onClickItem: ((ContactModel) -> Unit)? = null
    private var onClickItemDelete: ((ContactModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContactViewHolder (LayoutInflater.from(parent.context)
            .inflate(R.layout.items_contact_agend, parent, false))

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bindView(contact)

        holder.itemView.setOnClickListener{
            onClickItem?.invoke(contact)
        }
        holder.btnDelete.setOnClickListener{
            onClickItemDelete?.invoke(contact)
        }
    }

    override fun getItemCount(): Int = contactList.size

    fun setOnClickItem(callback: (ContactModel) -> Unit){
        this.onClickItem = callback
    }

    fun setOnClickItemDelete(callback: (ContactModel) -> Unit){
        this.onClickItemDelete = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addContacts(contacts: ArrayList<ContactModel>){
        this.contactList = contacts
        notifyDataSetChanged()
    }

    class ContactViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var id = view.findViewById<TextView>(R.id.tvItemID)
        private var name = view.findViewById<TextView>(R.id.tvItemName)
        private var phone = view.findViewById<TextView>(R.id.tvItemPhone)
        var btnDelete: TextView = view.findViewById(R.id.btnDelete)

        fun bindView(contact: ContactModel){
            id.text = contact.id.toString()
            name.text = contact.name
            phone.text = contact.phone
        }
    }
}