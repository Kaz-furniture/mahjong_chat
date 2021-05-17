package com.kaz_furniture.mahjongChat.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.kaz_furniture.mahjongChat.GlideApp
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityMainBinding
import com.kaz_furniture.mahjongChat.extensions.setIconOnImageId
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        binding.bottomNuv.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home, R.id.navigation_second, R.id.navigation_third, R.id.navigation_fourth), binding.drawerLayout)
        binding.navView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.navView.getHeaderView(0)?.also { headerView ->
            headerView.findViewById<TextView>(R.id.headerUserName)?.apply {
                text = myUser.name
                setOnClickListener {
                    launchProfileActivity()
                }
            }
            val userIconImage = headerView.findViewById<ImageView>(R.id.headerUserIconImage)
            userIconImage.setIconOnImageId(myUser.imageUrl)
            headerView.findViewById<ImageView>(R.id.headerUserIconImage)?.setOnClickListener {
                launchProfileActivity()
            }
        }
    }

    private fun launchProfileActivity() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        if (FirebaseAuth.getInstance().currentUser?.uid.isNullOrEmpty()) {
            launchLoginActivity()
            return
        }
        val intent = ProfileActivity.newIntent(this, myUser.userId)
        startActivityForResult(intent, REQUEST_CODE_PROFILE)
    }

    private fun launchFavoritePostsActivity() {
        val intent = FavoritePostsActivity.newIntent(this)
        startActivityForResult(intent, REQUEST_CODE_FAVORITE_POSTS)
    }

    private fun launchProfileEditActivity() {
        if (FirebaseAuth.getInstance().currentUser?.uid.isNullOrEmpty()) {
            launchLoginActivity()
            return
        }
        val intent = ProfileEditActivity.newIntent(this)
        startActivityForResult(intent, REQUEST_CODE_PROFILE_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

            R.id.menu_favorite -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                launchFavoritePostsActivity()
            }

            R.id.menu_profile_edit -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                launchProfileEditActivity()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
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
        private const val REQUEST_CODE_DETAIL = 1002
        private const val REQUEST_CODE_PROFILE = 1003
        private const val REQUEST_CODE_FAVORITE_POSTS = 1004
    }
}