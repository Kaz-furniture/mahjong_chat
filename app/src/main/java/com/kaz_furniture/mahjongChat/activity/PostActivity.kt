package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.databinding.ActivityPostBinding
import com.kaz_furniture.mahjongChat.viewModel.PostViewModel
import com.yalantis.ucrop.UCrop
import timber.log.Timber
import java.io.File

class PostActivity: BaseActivity() {
    private val viewModel: PostViewModel by viewModels()
    private var uCropSrcUri: Uri? = null
    lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_post)
        binding.lifecycleOwner = this
        binding.explanation = viewModel.explanationInput
        binding.postButton.setOnClickListener {
            viewModel.post(this, binding)
        }
        viewModel.usersName = intent.getStringExtra("KEY_NAME")
//        viewModel.userId = intent.getStringExtra("KEY_ID")
        binding.selectImageButton.setOnClickListener {
            selectImage()
        }
        title = getString(R.string.postCreate)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

//    fun View.visibleOrGone(isVisible: Boolean) {
//        visibility = if (isVisible) View.VISIBLE else View.GONE
//    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun deleteNoImage() {
        Timber.d("uCropSrcUri = $uCropSrcUri")
        if (uCropSrcUri != null) {
            val textNoImage = binding.noImage
            textNoImage.isVisible = false
        } else {
            return
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")
        startActivityForResult(intent, RC_CHOOSE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        } else {
            when(requestCode) {
                RC_CHOOSE_IMAGE-> {
                    data.data?.also {
                        uCropSrcUri = it
                        startUCrop()
                    }
                }

                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data)
                    uCropSrcUri = resultUri
                    val cropSrc = uCropSrcUri ?:return
                    val inputStream = contentResolver.openInputStream(cropSrc)
                    val image = BitmapFactory.decodeStream(inputStream)
                    val imageView = binding.postImageView
                    imageView.setImageBitmap(image)
                    deleteNoImage()
                }

                UCrop.RESULT_ERROR -> {
                    uCropSrcUri = null
                    Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun startUCrop() {
        val file = File.createTempFile("${System.currentTimeMillis()}", ".temp", cacheDir)
        uCropSrcUri?.apply {
            UCrop.of(this, file.toUri())
                    .withAspectRatio(11f, 6f)
                    .withOptions(UCrop.Options().apply {
                        setToolbarTitle("画像トリミング")
                        setCompressionFormat(Bitmap.CompressFormat.JPEG)
//                        setCompressionQuality(75)
                        setHideBottomControls(true)
                        setCircleDimmedLayer(false)
                        setShowCropGrid(false)
                        setShowCropFrame(false)
                    })
                    .start(this@PostActivity)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PostActivity::class.java)
        }
        private const val RC_CHOOSE_IMAGE = 1000
    }
}