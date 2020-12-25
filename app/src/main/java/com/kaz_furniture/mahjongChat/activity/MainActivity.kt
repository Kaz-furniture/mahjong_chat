package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.MahjongChatApplication
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.PostListAdapter
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityMainBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel
import timber.log.Timber

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolBar)

//        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
//        binding.drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        binding.bottomNuv.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_second, R.id.navigation_third, R.id.navigation_fourth), binding.drawerLayout)
        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.fab.setOnClickListener{
            launchPostActivity()
        }
        binding.navView.setNavigationItemSelectedListener(this)
//        viewModel.loadDMUsers(viewModel.dMList)
        viewModel.makeLogout.observe(this, Observer {
            launchLoginActivity()
        })
    }

    override fun onResume() {
        super.onResume()
        binding.navView.getHeaderView(0)?.also { headerView ->
            headerView.findViewById<TextView>(R.id.headerUserName)?.text = myUser.name
            val storageRef = FirebaseStorage.getInstance().reference
            val iconImageRef = storageRef.child("${myUser.userId}/profileImage.jpg")
            val userIconImage = headerView.findViewById<com.makeramen.roundedimageview.RoundedImageView>(R.id.headerUserIconImage)
            Glide.with(MahjongChatApplication.applicationContext)
                    .load(iconImageRef)
                    .into(userIconImage)
        }
    }

    private fun launchPostActivity() {
        val intent = PostActivity.newIntent(this)
        startActivityForResult(intent, REQUEST_CODE_POST)
    }

    private fun launchProfileEditActivity() {
        val intent = ProfileEditActivity.newIntent(this)
        startActivityForResult(intent, REQUEST_CODE_PROFILE_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_POST) {
            viewModel.updateData.postValue(true)
        }
        if (requestCode == REQUEST_CODE_PROFILE_EDIT) {
            viewModel.updateData.postValue(true)
        }
    }

    //bottom nav
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                finishAffinity()
                LoginActivity.start(this)
                Toast.makeText(this, "ログアウトしました", Toast.LENGTH_LONG).show()
            }

            R.id.menu_profile_edit -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                launchProfileEditActivity()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        binding.navView.getHeaderView(0)?.also { headerView ->
            headerView.findViewById<TextView>(R.id.headerUserName)?.text = myUser.name
            val storageRef = FirebaseStorage.getInstance().reference
            val iconImageRef = storageRef.child("${myUser.userId}/profileImage.jpg")
            val userIconImage = headerView.findViewById<com.makeramen.roundedimageview.RoundedImageView>(R.id.headerUserIconImage)
            Glide.with(MahjongChatApplication.applicationContext)
                .load(iconImageRef)
                .into(userIconImage)
        }
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        fun start(activity: Activity) =
            activity.apply {
                val intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        private const val REQUEST_CODE_POST = 1000
        private const val REQUEST_CODE_PROFILE_EDIT = 1001
    }
}