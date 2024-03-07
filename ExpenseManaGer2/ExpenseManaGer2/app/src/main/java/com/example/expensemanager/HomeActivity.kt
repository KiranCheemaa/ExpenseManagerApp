package com.example.expensemanager

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.FrameLayout
 

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    private lateinit var dashboardFragment: DashboardFragment
    private lateinit var incomeFragment: IncomeFragment
    private lateinit var expenseFragment: ExpenseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        toolbar.title = "Expense Manager"
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottomNavigationbar)
        frameLayout = findViewById(R.id.main_frame)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.naView)
        navigationView.setNavigationItemSelectedListener(this)

        dashboardFragment = DashboardFragment() // Define your fragments
        incomeFragment = IncomeFragment()
        expenseFragment = ExpenseFragment()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard -> {
                    setFragment(dashboardFragment)
                    bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.income -> {
                    setFragment(incomeFragment)
                    bottomNavigationView.setItemBackgroundResource(R.color.income_color)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.expense -> {
                    setFragment(expenseFragment)
                    bottomNavigationView.setItemBackgroundResource(R.color.expense_color)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frame, fragment)
            commit()
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedListener(item.itemId)
        return true
    }

    private fun displaySelectedListener(itemId: Int) {
        var fragment: Fragment? = null
        when (itemId) {
            R.id.dashboard -> {
                // Initialize Dashboard Fragment
                fragment = DashboardFragment()
            }
            R.id.income -> {
                // Initialize Income Fragment
                fragment = IncomeFragment()
            }
            R.id.expense -> {
                // Initialize Expense Fragment
                fragment = ExpenseFragment()
            }
        }

        fragment?.let {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_frame, it)
                commit()
            }
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}
