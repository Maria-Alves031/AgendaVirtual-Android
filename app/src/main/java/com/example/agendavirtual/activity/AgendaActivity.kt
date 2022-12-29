package com.example.agendavirtual.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendavirtual.R
import com.example.agendavirtual.SQLiteHelper.SQLiteHelper
import com.example.agendavirtual.adapter.ListContactAdapter
import com.example.agendavirtual.model.ContactModel
import com.example.agendavirtual.util.Constants
import com.example.agendavirtual.util.Constants.Companion.ADDED
import com.example.agendavirtual.util.Constants.Companion.ATTENTION
import com.example.agendavirtual.util.Constants.Companion.CONFIRMATION
import com.example.agendavirtual.util.Constants.Companion.DELETED
import com.example.agendavirtual.util.Constants.Companion.EDITED
import com.example.agendavirtual.util.Constants.Companion.EQUALS
import com.example.agendavirtual.util.Constants.Companion.FILL_FIELDS
import com.example.agendavirtual.util.Constants.Companion.NO
import com.example.agendavirtual.util.Constants.Companion.NO_ADDED
import com.example.agendavirtual.util.Constants.Companion.NO_EDITED
import com.example.agendavirtual.util.Constants.Companion.YES

class AgendaActivity : AppCompatActivity() {

    private lateinit var edName: EditText
    private lateinit var edPhone: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnConsult: Button
    private lateinit var btnEdit: Button
    private lateinit var recyclerView: RecyclerView

    private var adapter: ListContactAdapter? = null
    private lateinit var sqLiteHelper: SQLiteHelper
    private var contactModel: ContactModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        initViews()

        initRecycleView()

        sqLiteHelper = SQLiteHelper(this)

        setClickButtons()
    }

    private fun initViews() {
        edName = findViewById(R.id.editName)
        edPhone = findViewById(R.id.editPhone)
        btnAdd = findViewById(R.id.btnAdd)
        btnEdit = findViewById(R.id.btnEdit)
        btnConsult = findViewById(R.id.btnConsult)
        recyclerView = findViewById(R.id.recycleView)
    }

    private fun initRecycleView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListContactAdapter()
        recyclerView.adapter = adapter
    }

    private fun setClickButtons() {
        btnAdd.setOnClickListener {
            addContact()
        }
        btnConsult.setOnClickListener {
            getContacts()
        }
        btnEdit.setOnClickListener {
            updateContacts()
        }

        adapter?.setOnClickItem {
            edName.setText(it.name)
            edPhone.setText(it.phone)
            contactModel = it
        }

        adapter?.setOnClickItemDelete {
            deleteContact(it.id)
        }

    }

    private fun validateFields(): Boolean {
        val name = edName.text.toString()
        val phone = edPhone.text.toString()

        if (name.isEmpty() && name.isBlank() || phone.isEmpty() && phone.isBlank()) {
            Toast.makeText(this, FILL_FIELDS, Toast.LENGTH_SHORT).show()
            return false
        } else if (contactModel?.phone == phone || contactModel?.name == name) {
            Toast.makeText(this, Constants.CONTACT_EXIST, Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    private fun clearFields() {
        edName.text.clear()
        edPhone.text.clear()
        edName.requestFocus()
    }

    private fun updateContacts() {
        val name = edName.text.toString()
        val phone = edPhone.text.toString()

        if (name == contactModel?.name && phone == contactModel?.phone) {
            Toast.makeText(this, EQUALS, Toast.LENGTH_SHORT).show()
            return
        } else if (name.isBlank() || phone.isBlank()){
            Toast.makeText(this, FILL_FIELDS, Toast.LENGTH_SHORT).show()
            return
        }
        if (contactModel == null) return

        val contact = ContactModel(id = contactModel!!.id, name = name, phone = phone)
        val status = sqLiteHelper.updateContacts(contact)

        if (status > -1) {
            clearFields()
            getContacts()
            Toast.makeText(this, EDITED, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, NO_EDITED, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteContact(id: Int) {
        setAlertDialog(id)
        clearFields()
        getContacts()
    }

    private fun setAlertDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(CONFIRMATION)
        builder.setCancelable(true)

        builder.setPositiveButton(YES) { dialog, _ ->
            sqLiteHelper.deleteContact(id)
            getContacts()
            Toast.makeText(this, DELETED, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setNegativeButton(NO) { dialog, _ ->
            dialog.dismiss()
        }

        builder.setIcon(R.drawable.atencao)

        builder.setTitle(ATTENTION)

        val alert = builder.create()
        alert.show()
    }

    private fun addContact() {
        if (validateFields()) {

            val contact = ContactModel(name = edName.text.toString(), phone = edPhone.text.toString())
            val status = sqLiteHelper.insertContact(contact)
            clearFields()
            getContacts()
            if (status > -1) {
                Toast.makeText(this, ADDED, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, NO_ADDED, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getContacts() {
        val contactList = sqLiteHelper.getAllContacts()

        adapter?.addContacts(contactList)
    }

}