// Import statements
package com.tracker.expensemanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tracker.expensemanager.R
import com.tracker.expensemanager.model.Data
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.DateFormat

// IncomeFragment class
class IncomeFragment : Fragment() {
    // Properties declaration
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mIncomeDatabase: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var incomeTotalSum: TextView
    private lateinit var edtAmount: EditText
    private lateinit var edtType: EditText
    private lateinit var edtNote: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var type: String
    private lateinit var note: String
    private var amount: Int = 0
    private lateinit var postKey: String

    // onCreateView method
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myview = inflater.inflate(R.layout.fragment_income, container, false)

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance()
        val mUser: FirebaseUser? = mAuth.currentUser
        val uid: String = mUser?.uid ?: ""
        // Get reference to the income data in Firebase Database
        mIncomeDatabase = FirebaseDatabase.getInstance().reference.child("IncomeData").child(uid)

        // Initialize views
        incomeTotalSum = myview.findViewById(R.id.income_txt_result)
        recyclerView = myview.findViewById(R.id.recycler_id_income)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        // Calculate total income
        mIncomeDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalValue = 0
                for (mysnapshot in dataSnapshot.children) {
                    val data = mysnapshot.getValue(Data::class.java)
                    totalValue += data?.amount ?: 0
                }
                incomeTotalSum.text = totalValue.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        return myview
    }

    // onStart method
    override fun onStart() {
        super.onStart()

        // Set up FirebaseRecyclerAdapter for displaying income data
        val options: FirebaseRecyclerOptions<Data> =
            FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data::class.java)
                .build()

        val adapter: FirebaseRecyclerAdapter<Data, MyViewHolder> =
            object : FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    // Inflate layout for each item in the RecyclerView
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.income_recycler_data, parent, false)
                    return MyViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: MyViewHolder,
                    position: Int,
                    model: Data
                ) {
                    // Bind data to views in each RecyclerView item
                    holder.setType(model.type)
                    holder.setNote(model.note)
                    holder.setDate(model.date)
                    holder.setAmount(model.amount)

                    // Set click listener to handle item click
                    holder.itemView.setOnClickListener {
                        postKey = getRef(position).key ?: ""
                        type = model.type ?: ""
                        note = model.note ?: ""
                        amount = model.amount
                        updateDataItem()
                    }
                }
            }

        recyclerView.adapter = adapter
        adapter.startListening()
    }

    // MyViewHolder class for RecyclerView item
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setType(type: String?) {
            // Set income type
            val mType = itemView.findViewById<TextView>(R.id.type_txt_income)
            mType.text = type
        }

        fun setNote(note: String?) {
            // Set income note
            val mNote = itemView.findViewById<TextView>(R.id.note_txt_income)
            mNote.text = note
        }

        fun setDate(date: String?) {
            // Set income date
            val mDate = itemView.findViewById<TextView>(R.id.date_txt_income)
            mDate.text = date
        }

        fun setAmount(amount: Int) {
            // Set income amount
            val mAmmount = itemView.findViewById<TextView>(R.id.ammount_txt_income)
            mAmmount.text = amount.toString()
        }
    }

    // Method to update income data
    private fun updateDataItem() {
        val mydialog = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val myview = inflater.inflate(R.layout.update_data_item, null)
        mydialog.setView(myview)

        edtAmount = myview.findViewById(R.id.ammount_edt)
        edtType = myview.findViewById(R.id.type_edt)
        edtNote = myview.findViewById(R.id.note_edt)

        // Populate EditText fields with current data
        edtType.setText(type)
        edtType.setSelection(type.length)
        edtNote.setText(note)
        edtNote.setSelection(note.length)
        edtAmount.setText(amount.toString())
        edtAmount.setSelection(amount.toString().length)

        btnUpdate = myview.findViewById(R.id.btn_upd_Update)
        btnDelete = myview.findViewById(R.id.btnuPD_Delete)

        val dialog = mydialog.create()

        btnUpdate.setOnClickListener {
            // Update data in Firebase Database
            type = edtType.text.toString().trim()
            note = edtNote.text.toString().trim()
            val newAmount = edtAmount.text.toString().trim().toInt()
            val mDate = DateFormat.getDateInstance().format(java.util.Date())
            val data = Data(newAmount, type, note, postKey, mDate)
            mIncomeDatabase.child(postKey).setValue(data)
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            // Delete data from Firebase Database
            mIncomeDatabase.child(postKey).removeValue()
            dialog.dismiss()
        }

        dialog.show()
    }
}
