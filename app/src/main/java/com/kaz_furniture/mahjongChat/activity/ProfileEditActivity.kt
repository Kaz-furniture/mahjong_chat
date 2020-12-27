package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityProfileEditBinding
import com.kaz_furniture.mahjongChat.viewModel.ProfileEditViewModel
import com.yalantis.ucrop.UCrop
import timber.log.Timber
import java.io.File

class ProfileEditActivity: BaseActivity() {

    lateinit var binding: ActivityProfileEditBinding
    private val viewModel: ProfileEditViewModel by viewModels()
    private var uCropSrcUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        binding.lifecycleOwner = this
        binding.name = viewModel.editedName
        binding.introduction = viewModel.editedIntroduction
        viewModel.makeLogout.observe(this, Observer {
            launchLoginActivity()
        })
        viewModel.canSubmit.observe(this, Observer {
            binding.canSubmit = viewModel.canSubmit.value
        })
        viewModel.uCropSrcUriLive.observe(this, Observer {
            uCropSrcUri = it
            binding.noImageTextView.isVisible = false
        })
        binding.saveButton.setOnClickListener {
            viewModel.editUpload(this)
        }
        binding.profileImageSelect.setOnClickListener {
            selectImage()
        }
        binding.presentName.text = myUser.name
        binding.presentIntroduction.text = myUser.introduction
        title = getString(R.string.profileEdit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel.showProfileImage(binding)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode!= RESULT_OK || data == null) {
            return
        } else {
            when(requestCode) {
                RC_CHOOSE_IMAGE -> {
                    data.data?.also {
                        uCropSrcUri = it
                        startUCrop()
                    }
                }
                UCrop.REQUEST_CROP -> {
                    viewModel.uCropStart(data, binding)
                }

                UCrop.RESULT_ERROR -> {
                    viewModel.showProfileImage(binding)
                    Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")
        startActivityForResult(intent, RC_CHOOSE_IMAGE)
    }

    private fun startUCrop() {
        val file = File.createTempFile("${System.currentTimeMillis()}", ".temp", cacheDir)
        uCropSrcUri?.apply {
            UCrop.of(this, file.toUri())
                    .withAspectRatio(1f, 1f)
                    .withOptions(UCrop.Options().apply {
                        setToolbarTitle("画像トリミング")
                        setCompressionFormat(Bitmap.CompressFormat.JPEG)
//                        setCompressionQuality(75)
                        setHideBottomControls(true)
                        setCircleDimmedLayer(true)
                        setShowCropGrid(false)
                        setShowCropFrame(false)
                    })
                    .start(this@ProfileEditActivity)
        }
        viewModel.uCropSrcUriLive.postValue(uCropSrcUri)

    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileEditActivity::class.java)
        }
        private const val RC_CHOOSE_IMAGE = 2000
    }
}