package com.company.imetlin.fishmarker

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class ProfileActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)



        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_place_fragment, R.id.nav_profile_fragment),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.exit) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.exit_title)
            builder.setMessage(R.string.exit_text)

            builder.setPositiveButton(android.R.string.yes) { _, _ ->
                FirebaseAuth.getInstance().signOut()
                //metLoginManager.getInstance().logOut()
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton(android.R.string.no) { _, _ ->
                Toast.makeText(
                    applicationContext,
                    android.R.string.no, Toast.LENGTH_SHORT
                ).show()
            }
            builder.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("WTF")
    }
}
