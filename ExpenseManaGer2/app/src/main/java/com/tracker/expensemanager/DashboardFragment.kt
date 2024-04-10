package com.tracker.expensemanager

// Import statements
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tracker.expensemanager.R
import com.tracker.expensemanager.model.Data
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.DateFormat
import java.util.*

// DashboardFragment class
class DashboardFragment : Fragment() {

    // Properties declaration
    private lateinit var fabMainBtn: FloatingActionButton
    private lateinit var fabIncomeBtn: FloatingActionButton
    private lateinit var fabExpenseBtn: FloatingActionButton

    private lateinit var fabIncomeTxt: TextView
    private lateinit var fabExpenseTxt: TextView

    private var isOpen = false

    private lateinit var fadeOpen: Animation
    private lateinit var fadeClose: Animation

    private lateinit var totalIncomeResult: TextView
    private lateinit var totalExpenseResult: TextView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mIncomeDatabase: DatabaseReference
    private lateinit var mExpenseDatabase: DatabaseReference

    private lateinit var mRecyclerIncome: RecyclerView
    private lateinit var mRecyclerExpense: RecyclerView

    // onCreateView method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize Firebase authentication and database references
        mAuth = FirebaseAuth.getInstance()
        val mUser: FirebaseUser? = mAuth.currentUser
        val uid: String = mUser?.uid ?: ""

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference("IncomeData").child(uid)
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid)

        // Initialize UI elements
        fabMainBtn = myView.findViewById(R.id.fb_main_plus_btn)
        fabIncomeBtn = myView.findViewById(R.id.income_Ft_btn)
        fabExpenseBtn = myView.findViewById(R.id.expense_Ft_btn)

        totalIncomeResult = myView.findViewById(R.id.income_set_result)
        totalExpenseResult = myView.findViewById(R.id.expense_set_data)

        mRecyclerIncome = myView.findViewById(R.id.recycler_income)
        mRecyclerExpense = myView.findViewById(R.id.recycler_expense)

        fabIncomeTxt = myView.findViewById(R.id.income_ft_text)
        fabExpenseTxt = myView.findViewById(R.id.expense_ft_text)

        fadeOpen = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_close)

        // Set click listeners for FAB buttons
        fabMainBtn.setOnClickListener {
            addData()
        }

        fabIncomeBtn.setOnClickListener {
            incomeDataInsert()
        }

        fabExpenseBtn.setOnClickListener {
            expenseDataInsert()
        }

        // Set up listeners for income and expense data changes in Firebase database
        mIncomeDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Calculate total income
                var totalSum = 0
                for (mysnap in dataSnapshot.children) {
                    val data = mysnap.getValue(Data::class.java)
                    totalSum += data?.amount ?: 0
                    val stResult = totalSum.toString()
                    totalIncomeResult.text = "$stResult.00"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        mExpenseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Calculate total expenses
                var totalSum = 0
                for (mysnap in dataSnapshot.children) {
                    val data = mysnap.getValue(Data::class.java)
                    totalSum += data?.amount ?: 0
                    val strTotalSum = totalSum.toString()
                    totalExpenseResult.text = "$strTotalSum.00"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // Set up layout manager for income RecyclerView
        val layoutManagerIncome = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        layoutManagerIncome.stackFromEnd = true
        layoutManagerIncome.reverseLayout = true
        mRecyclerIncome.setHasFixedSize(true)
        mRecyclerIncome.layoutManager = layoutManagerIncome

        // Set up layout manager for expense RecyclerView
        val layoutManagerExpense = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        layoutManagerExpense.stackFromEnd = true
        layoutManagerExpense.reverseLayout = true
        mRecyclerExpense.setHasFixedSize(true)
        mRecyclerExpense.layoutManager = layoutManagerExpense

        return myView
    }

    // Method to handle FAB animations
    private fun ftAnimation() {
        if (isOpen) {
            fabIncomeBtn.startAnimation(fadeClose)
            fabExpenseBtn.startAnimation(fadeClose)
            fabIncomeBtn.isClickable = false
            fabExpenseBtn.isClickable = false

            fabIncomeTxt.startAnimation(fadeClose)
            fabExpenseTxt.startAnimation(fadeClose)
            fabIncomeBtn.isClickable = false
            fabExpenseBtn.isClickable = false
            isOpen = false
        } else {
            fabIncomeBtn.startAnimation(fadeOpen)
            fabExpenseBtn.startAnimation(fadeOpen)
            fabIncomeBtn.isClickable = true
            fabExpenseBtn.isClickable = true

            fabIncomeTxt.startAnimation(fadeOpen)
            fabExpenseTxt.startAnimation(fadeOpen)
            fabIncomeBtn.isClickable = true
            fabExpenseBtn.isClickable = true
            isOpen = true
        }
    }

    // Method to handle main FAB button click
    private fun addData() {
        ftAnimation()
    }

    // Method to handle income data insertion
    private fun incomeDataInsert() {
        // AlertDialog for inserting income data
        val myDialog = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())
        val myViewm = inflater.inflate(R.layout.custom_layout_for_insertdata, null)
        myDialog.setView(myViewm)
        val dialog = myDialog.create()
        dialog.setCancelable(false)

        val edtAmount = myViewm.findViewById<EditText>(R.id.ammount_edt)
        val edtType = myViewm.findViewById<EditText>(R.id.type_edt)
        val edtNote = myViewm.findViewById<EditText>(R.id.note_edt)
        val btnSave = myViewm.findViewById<Button>(R.id.btnSave)
        val btnCancel = myViewm.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            // Save income data
            val type = edtType.text.toString().trim()
            val amount = edtAmount.text.toString().trim()
            val note = edtNote.text.toString().trim()

            if (TextUtils.isEmpty(type) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(note)) {
                Toast.makeText(requireActivity(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ourAmountInt = amount.toInt()
            val id = mIncomeDatabase.push().key
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmountInt, type, note, id ?: "", mDate)
            mIncomeDatabase.child(id ?: "").setValue(data)
            Toast.makeText(requireActivity(), "Data ADDED", Toast.LENGTH_SHORT).show()
            ftAnimation()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            ftAnimation()
            dialog.dismiss()
        }

        dialog.show()
    }

    // Method to handle expense data insertion
    private fun expenseDataInsert() {
        // AlertDialog for inserting expense data
        val myDialog = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())
        val myView = inflater.inflate(R.layout.custom_layout_for_insertdata, null)
        myDialog.setView(myView)
        val dialog = myDialog.create()
        dialog.setCancelable(false)

        val edtAmount = myView.findViewById<EditText>(R.id.ammount_edt)
        val edtType = myView.findViewById<EditText>(R.id.type_edt)
        val edtNote = myView.findViewById<EditText>(R.id.note_edt)
        val btnSave = myView.findViewById<Button>(R.id.btnSave)
        val btnCancel = myView.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            // Save expense data
            val type = edtType.text.toString().trim()
            val amount = edtAmount.text.toString().trim()
            val note = edtNote.text.toString().trim()

            if (TextUtils.isEmpty(type) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(note)) {
                Toast.makeText(requireActivity(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ourAmountInt = amount.toInt()
            val id = mExpenseDatabase.push().key
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmountInt, type, note, id ?: "", mDate)
            mExpenseDatabase.child(id ?: "").setValue(data)
            Toast.makeText(requireActivity(), "Data ADDED", Toast.LENGTH_SHORT).show()
            ftAnimation()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            ftAnimation()
            dialog.dismiss()
        }

        dialog.show()
    }

    // onStart method
    override fun onStart() {
        super.onStart()

        // Set up FirebaseRecyclerAdapter for income
        val incomeQuery: Query = mIncomeDatabase.orderByChild("timestamp")
        val incomeOptions = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(incomeQuery, Data::class.java)
            .build()

        val incomeAdapter = object : FirebaseRecyclerAdapter<Data, IncomeViewHolder>(incomeOptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_income, parent, false)
                return IncomeViewHolder(view)
            }

            override fun onBindViewHolder(holder: IncomeViewHolder, position: Int, model: Data) {
                holder.setIncomeType(model.type)
                holder.setIncomeAmmount(model.amount)
                holder.setIncomeDate(model.date)
            }
        }

        mRecyclerIncome.adapter = incomeAdapter

        // Set up FirebaseRecyclerAdapter for expenses
        val expenseQuery: Query = mExpenseDatabase.orderByChild("timestamp")
        val expenseOptions = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(expenseQuery, Data::class.java)
            .build()

        val expenseAdapter = object : FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expenseOptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_expense, parent, false)
                return ExpenseViewHolder(view)
            }

            override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int, model: Data) {
                holder.setExpenseType(model.type)
                holder.setExpenseAmmount(model.amount)
                holder.setExpenseDate(model.date)
            }
        }

        mRecyclerExpense.adapter = expenseAdapter

        // Start listening for changes
        incomeAdapter.startListening()
        expenseAdapter.startListening()
    }

    // ViewHolder for income items
    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mType: TextView = itemView.findViewById(R.id.type_Income_ds)
        private val mAmount: TextView = itemView.findViewById(R.id.ammoun_income_ds)
        private val mDate: TextView = itemView.findViewById(R.id.date_income_ds)

        fun setIncomeType(type: String?) {
            mType.text = type
        }

        fun setIncomeAmmount(ammount: Int) {
            val strAmmount = ammount.toString()
            mAmount.text = strAmmount
        }

        fun setIncomeDate(date: String?) {
            mDate.text = date
        }
    }

    // ViewHolder for expense items
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mType: TextView = itemView.findViewById(R.id.type_expense_ds)
        private val mAmount: TextView = itemView.findViewById(R.id.ammoun_expense_ds)
        private val mDate: TextView = itemView.findViewById(R.id.date_expense_ds)

        fun setExpenseType(type: String?) {
            mType.text = type
        }

        fun setExpenseAmmount(amount: Int) {
            val strAmount = amount.toString()
            mAmount.text = strAmount
        }

        fun setExpenseDate(date: String?) {
            mDate.text = date
        }
    }
}
