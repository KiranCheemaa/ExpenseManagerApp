// Import statements
package com.tracker.expensemanager

import android.content.Intent
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
import com.tracker.expensemanager.R
import com.google.firebase.auth.FirebaseAuth

// HomeActivity class
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // Properties declaration
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout

    private lateinit var dashboardFragment: DashboardFragment
    private lateinit var incomeFragment: IncomeFragment
    private lateinit var expenseFragment: ExpenseFragment
    private lateinit var mAuth: FirebaseAuth

    // onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up toolbar
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        toolbar.title = "Expense Manager"
        setSupportActionBar(toolbar)

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance()

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationbar)
        frameLayout = findViewById(R.id.main_frame)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.naView)
        navigationView.setNavigationItemSelectedListener(this)

        // Initialize fragments
        dashboardFragment = DashboardFragment()
        incomeFragment = IncomeFragment()
        expenseFragment = ExpenseFragment()

        // Set DashboardFragment as default fragment
        setFragment(dashboardFragment)

        // Bottom navigation item click listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard -> {
                    setFragment(dashboardFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.income -> {
                    setFragment(incomeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.expense -> {
                    setFragment(expenseFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    // Sign out user and navigate to MainActivity
                    mAuth.signOut()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish() // Close the current activity after logout
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    // Method to set fragment
    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frame, fragment)
            commit()
        }
    }

    // onBackPressed method to handle drawer closing
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    // onNavigationItemSelected method to handle navigation drawer item selection
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.dashboard -> setFragment(dashboardFragment)
            R.id.income -> setFragment(incomeFragment)
            R.id.expense -> setFragment(expenseFragment)
            R.id.logout -> {
                // Sign out user and navigate to MainActivity
                mAuth.signOut()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
