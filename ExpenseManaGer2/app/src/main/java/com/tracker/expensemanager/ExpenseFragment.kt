package com.tracker.expensemanager

// Import statements
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.widget.Button
import com.tracker.expensemanager.R
import com.tracker.expensemanager.model.Data
import java.text.DateFormat
import java.util.*

// ExpenseFragment class
class ExpenseFragment : Fragment() {

    // Properties declaration
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mExpenseDatabase: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseSumResult: TextView
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
        val myview = inflater.inflate(R.layout.fragment_expense, container, false)

        // Initialize Firebase authentication and database references
        mAuth = FirebaseAuth.getInstance()
        val mUser: FirebaseUser? = mAuth.currentUser
        val uid: String = mUser?.uid ?: ""
        mExpenseDatabase =
            FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid)

        // Initialize UI elements
        expenseSumResult = myview.findViewById(R.id.expense_txt_result)
        recyclerView = myview.findViewById(R.id.recycler_id_expense)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        // Calculate and display total expense
        mExpenseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var expenseSum = 0
                for (mysanashot in dataSnapshot.children) {
                    val data = mysanashot.getValue(Data::class.java)
                    expenseSum += data?.amount ?: 0
                }
                expenseSumResult.text = expenseSum.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        return myview
    }

    // onStart method
    override fun onStart() {
        super.onStart()

        // Set up FirebaseRecyclerAdapter for expenses
        val options: FirebaseRecyclerOptions<Data> =
            FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data::class.java)
                .build()

        val adapter: FirebaseRecyclerAdapter<Data, MyViewHolder> =
            object : FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.expense_recycler_data, parent, false)
                    return MyViewHolder(view)
                }

                override fun onBindViewHolder(
                    holder: MyViewHolder,
                    position: Int,
                    model: Data
                ) {
                    holder.setType(model.type)
                    holder.setNote(model.note)
                    holder.setDate(model.date)
                    holder.setAmount(model.amount)

                    // Handle item click to update data
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

    // ViewHolder for expense items
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setType(type: String?) {
            val mType = itemView.findViewById<TextView>(R.id.type_txt_expense)
            mType.text = type
        }

        fun setNote(note: String?) {
            val mNote = itemView.findViewById<TextView>(R.id.note_txt_expense)
            mNote.text = note
        }

        fun setDate(date: String?) {
            val mDate = itemView.findViewById<TextView>(R.id.date_txt_expense)
            mDate.text = date
        }

        fun setAmount(amount: Int) {
            val mAmount = itemView.findViewById<TextView>(R.id.ammount_txt_expense)
            mAmount.text = amount.toString()
        }
    }

    // Method to update data item
    private fun updateDataItem() {
        // AlertDialog for updating or deleting data item
        val mydialog = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val myview = inflater.inflate(R.layout.update_data_item, null)
        mydialog.setView(myview)

        edtAmount = myview.findViewById(R.id.ammount_edt)
        edtNote = myview.findViewById(R.id.note_edt)
        edtType = myview.findViewById(R.id.type_edt)

        // Fill EditText fields with current data
        edtType.setText(type)
        edtType.setSelection(type.length)
        edtNote.setText(note)
        edtNote.setSelection(note.length)
        edtAmount.setText(amount.toString())
        edtAmount.setSelection(amount.toString().length)

        btnUpdate = myview.findViewById(R.id.btn_upd_Update)
        btnDelete = myview.findViewById(R.id.btnuPD_Delete)

        val dialog = mydialog.create()

        // Update button click listener
        btnUpdate.setOnClickListener {
            type = edtType.text.toString().trim()
            note = edtNote.text.toString().trim()

            val strAmount = edtAmount.text.toString().trim()
            val intAmount = strAmount.toInt()

            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(intAmount, type, note, postKey, mDate)

            mExpenseDatabase.child(postKey).setValue(data)
            dialog.dismiss()
        }

        // Delete button click listener
        btnDelete.setOnClickListener {
            mExpenseDatabase.child(postKey).removeValue()
            dialog.dismiss()
        }

        dialog.show()
    }
}
