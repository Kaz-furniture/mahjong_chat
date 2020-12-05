package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.base.BaseActivity
import com.kaz_furniture.mahjongChat.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        setSupportActionBar(binding.toolBar)
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//        ), binding.drawerLayout)

        binding.bottomNuv.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
//        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        //return navController.navigateUp()
        return NavigationUI.navigateUp(navController,appBarConfiguration) || super.onSupportNavigateUp()

    }


    companion object {
        fun start(activity: Activity) =
            activity.apply {
                finishAffinity()
                startActivity(Intent(activity, MainActivity::class.java))
            }
    }
}