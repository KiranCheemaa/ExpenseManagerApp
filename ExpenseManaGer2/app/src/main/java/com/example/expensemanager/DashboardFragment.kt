package com.example.expensemanager

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
import com.example.expensemanager.model.Data
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private lateinit var fab_main_btn: FloatingActionButton
    private lateinit var fab_income_btn: FloatingActionButton
    private lateinit var fab_expense_btn: FloatingActionButton

    private lateinit var fab_income_txt: TextView
    private lateinit var fab_expense_txt: TextView

    private var isOpen = false

    private lateinit var FadOpen: Animation
    private lateinit var FadeClose: Animation

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mIncomeDatabase: DatabaseReference
    private lateinit var mExpenseDatabase: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myview = inflater.inflate(R.layout.fragment_dashboard, container, false)

        mAuth = FirebaseAuth.getInstance()
        val mUser: FirebaseUser? = mAuth.currentUser
        val uid: String = mUser?.uid ?: ""

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference("IncomeData").child(uid)
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid)

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn)
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn)
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn)

        fab_income_txt = myview.findViewById(R.id.income_ft_text)
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text)

        FadOpen = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_open)
        FadeClose = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_close)

        fab_main_btn.setOnClickListener {
            addData()
        }

        fab_income_btn.setOnClickListener {
            incomeDataInsert()
        }

        fab_expense_btn.setOnClickListener {
            expenseDataInsert()
        }

        return myview
    }

    private fun ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(FadeClose)
            fab_expense_btn.startAnimation(FadeClose)
            fab_income_btn.isClickable = false
            fab_expense_btn.isClickable = false

            fab_income_txt.startAnimation(FadeClose)
            fab_expense_txt.startAnimation(FadeClose)
            fab_income_btn.isClickable = false
            fab_expense_btn.isClickable = false
            isOpen = false
        } else {
            fab_income_btn.startAnimation(FadOpen)
            fab_expense_btn.startAnimation(FadOpen)
            fab_income_btn.isClickable = true
            fab_expense_btn.isClickable = true

            fab_income_txt.startAnimation(FadOpen)
            fab_expense_txt.startAnimation(FadOpen)
            fab_income_btn.isClickable = true
            fab_expense_btn.isClickable = true
            isOpen = true
        }
    }

    private fun addData() {
        ftAnimation()
    }

    private fun incomeDataInsert() {
        val mydialog = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())
        val myviewm = inflater.inflate(R.layout.custom_layout_for_insertdata, null)
        mydialog.setView(myviewm)
        val dialog = mydialog.create()
        dialog.setCancelable(false)

        val edtAmmount = myviewm.findViewById<EditText>(R.id.ammount_edt)
        val edtType = myviewm.findViewById<EditText>(R.id.type_edt)
        val edtNote = myviewm.findViewById<EditText>(R.id.note_edt)
        val btnSave = myviewm.findViewById<Button>(R.id.btnSave)
        val btnCancel = myviewm.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            val type = edtType.text.toString().trim()
            val ammount = edtAmmount.text.toString().trim()
            val note = edtNote.text.toString().trim()

            if (TextUtils.isEmpty(type) || TextUtils.isEmpty(ammount) || TextUtils.isEmpty(note)) {
                Toast.makeText(requireActivity(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ourAmountInt = ammount.toInt()
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

    private fun expenseDataInsert() {
        val mydialog = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())
        val myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null)
        mydialog.setView(myview)
        val dialog = mydialog.create()
        dialog.setCancelable(false)

        val ammount = myview.findViewById<EditText>(R.id.ammount_edt)
        val type = myview.findViewById<EditText>(R.id.type_edt)
        val note = myview.findViewById<EditText>(R.id.note_edt)
        val btnSave = myview.findViewById<Button>(R.id.btnSave)
        val btnCancel = myview.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            val tmtype = type.text.toString().trim()
            val tmAmmount = ammount.text.toString().trim()
            val tmnote = note.text.toString().trim()

            if (TextUtils.isEmpty(tmtype) || TextUtils.isEmpty(tmAmmount) || TextUtils.isEmpty(tmnote)) {
                Toast.makeText(requireActivity(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ourAmmountInt = tmAmmount.toInt()
            val id = mExpenseDatabase.push().key
            val mDate = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmmountInt, tmtype, tmnote, id ?: "", mDate)
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
}
