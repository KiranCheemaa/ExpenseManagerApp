package com.example.expensemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.model.Data
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class IncomeFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mIncomeDatabase: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var incomeTotalSum: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myview = inflater.inflate(R.layout.fragment_income, container, false)

        mAuth = FirebaseAuth.getInstance()
        val mUser: FirebaseUser? = mAuth.currentUser
        val uid: String = mUser?.uid ?: ""
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference("IncomeData").child(uid)

        incomeTotalSum = myview.findViewById(R.id.income_txt_result)
        recyclerView = myview.findViewById(R.id.recycler_id_income)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        mIncomeDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalValue = 0
                for (mysnapshot in dataSnapshot.children) {
                    val data: Data? = mysnapshot.getValue(Data::class.java)
                    data?.let {
                        totalValue += it.amount
                    }
                }
                val stTotalValue = totalValue.toString()
                incomeTotalSum.text = stTotalValue
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled
            }
        })

        return myview
    }

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(mIncomeDatabase, Data::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.income_recycler_data, parent, false)
                return MyViewHolder(view)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Data) {
                holder.setType(model.type)
                holder.setNote(model.note)
                holder.setDate(model.date)
                holder.setAmount(model.amount)
            }
        }

        recyclerView.adapter = adapter
        adapter.startListening()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mView: View = itemView

        fun setType(type: String) {
            val mType: TextView = mView.findViewById(R.id.type_txt_income)
            mType.text = type
        }

        fun setNote(note: String) {
            val mNote: TextView = mView.findViewById(R.id.note_txt_income)
            mNote.text = note
        }

        fun setDate(date: String) {
            val mDate: TextView = mView.findViewById(R.id.date_txt_income)
            mDate.text = date
        }

        fun setAmount(amount: Int) {
            val mAmmount: TextView = mView.findViewById(R.id.ammount_txt_income)
            mAmmount.text = amount.toString()
        }
    }
}
