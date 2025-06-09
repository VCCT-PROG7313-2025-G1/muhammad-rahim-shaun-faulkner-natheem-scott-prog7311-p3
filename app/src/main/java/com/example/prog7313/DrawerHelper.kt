package com.example.prog7313

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

object DrawerHelper {

    fun setupDrawer(activity: Activity, drawerLayout: DrawerLayout, toolbar: Toolbar, navView: NavigationView) {
        val toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //val user = FirebaseAuth.getInstance().currentUser
        //usernameText.text = user?.displayName ?: "Guest"

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    activity.startActivity(Intent(activity, HomepageActivity::class.java))
                }
                R.id.nav_timeline -> {
                    activity.startActivity(Intent(activity, Timeline::class.java))
                }
                R.id.nav_analytics -> {
                    activity.startActivity(Intent(activity, Analytics::class.java))
                }
                R.id.nav_profile -> {
                    activity.startActivity(Intent(activity, Account::class.java))
                }
                R.id.nav_accounts -> {
                    activity.startActivity(Intent(activity, AccountListActivity::class.java))
                }
                R.id.nav_categories -> {
                    activity.startActivity(Intent(activity, CreateCategory::class.java))
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(activity, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    activity.finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}