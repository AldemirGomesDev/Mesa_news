package com.aldemir.mesanews.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aldemir.mesanews.R
import com.aldemir.mesanews.ui.login.LoginActivity
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.fixedRateTimer


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var userNameHeader: TextView
    private lateinit var userEmailHeader: TextView
    private var mUserEmail: String? = null

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val logout: LinearLayout = findViewById(R.id.sign_out)
        val navController = findNavController(R.id.nav_host_fragment)

        logout.setOnClickListener {
            dialogLogout()
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val headerView: View = navView.getHeaderView(0)

        userNameHeader = headerView.user_name_drawer
        userEmailHeader = headerView.user_email_drawer
        observers()
        mainViewModel.getUserNameSharedPreference()
    }

    private fun observers() {
        mainViewModel.userEmail.observe(this@MainActivity, Observer { userEmail ->
            Log.d("RegisterViewModel: ", "userEmail ==>: $userEmail")
            if (userEmail != null) {
                mUserEmail = userEmail
                userEmailHeader.text = userEmail
                mainViewModel.getUserLogged(userEmail)
            }
        })

        mainViewModel.userLogged.observe(this@MainActivity, Observer { userLogged ->
            Log.d("RegisterViewModel: ", "userLogged ==>: ${userLogged}")
            if (userLogged != null) {
                userNameHeader.text = userLogged.name
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                dialogLogout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOut() {
        mainViewModel.logout(mUserEmail!!)
        LoginManager.getInstance().logOut()
        finish()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("key", "value")
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun dialogLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.alert_title))
            .setMessage(resources.getString(R.string.alert_message))
            .setNegativeButton(resources.getString(R.string.alert_button_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.alert_button_confirm)) { dialog, which ->
                dialog.dismiss()
                logOut()
            }
            .setCancelable(false)
            .show()
    }
}