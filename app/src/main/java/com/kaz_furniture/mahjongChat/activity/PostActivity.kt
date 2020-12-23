package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getRecyclerView
import com.afollestad.materialdialogs.list.listItems
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.DMListAdapter
import com.kaz_furniture.mahjongChat.adapter.TileListAdapter
import com.kaz_furniture.mahjongChat.data.Tile
import com.kaz_furniture.mahjongChat.databinding.ActivityPostBinding
import com.kaz_furniture.mahjongChat.databinding.DialogFragmentCreateChoiceBinding
import com.kaz_furniture.mahjongChat.viewModel.PostViewModel
import com.yalantis.ucrop.UCrop
import timber.log.Timber
import java.io.File

class PostActivity: BaseActivity() {
    private val viewModel: PostViewModel by viewModels()
    private var uCropSrcUri: Uri? = null
    lateinit var binding: ActivityPostBinding
    private lateinit var adapter: TileListAdapter
    lateinit var layoutManager: GridLayoutManager
    private val tileList = listOf<Tile>(
            Tile.M1, Tile.M2, Tile.M3, Tile.M4, Tile.M5, Tile.M5R, Tile.M6, Tile.M7, Tile.M8, Tile.M9,
            Tile.P1, Tile.P2, Tile.P3, Tile.P4, Tile.P5, Tile.P5R, Tile.P6, Tile.P7, Tile.P8, Tile.P9,
            Tile.S1, Tile.S2, Tile.S3, Tile.S4, Tile.S5, Tile.S5R, Tile.S6, Tile.S7, Tile.S8, Tile.S9,
            Tile.Z1, Tile.Z2, Tile.Z3, Tile.Z4, Tile.Z5, Tile.Z6, Tile.Z7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_post)
        binding.lifecycleOwner = this
        adapter = TileListAdapter(layoutInflater, tileList)
        layoutManager = GridLayoutManager(this, 4)
        binding.explanation = viewModel.explanationInput
        binding.postButton.setOnClickListener {
            viewModel.post(this, binding)
        }
//        viewModel.userId = intent.getStringExtra("KEY_ID")
        binding.selectImageButton.setOnClickListener {
            selectImage()
        }
        title = getString(R.string.postCreate)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.createChoicesButton.setOnClickListener {
            showCreateChoicesDialog()
        }
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

    private fun showCreateChoicesDialog() {
        MaterialDialog(this).show {
            cancelable(false)
            val dialogBinding = DialogFragmentCreateChoiceBinding.inflate(
                    LayoutInflater.from(this@PostActivity), null, false)
            dialogBinding.apply {
                this.choiceData = viewModel.choiceData
                this.doneButton.setOnClickListener {
                    dismiss()
                }
                this.selectTileButton.setOnClickListener {
                    showTileDialog()
                }
            }
            setContentView(dialogBinding.root)
        }
    }

    private fun showTileDialog() {
        MaterialDialog(this).show {
//            val items = mutableListOf<String>()
//            for (i in 0 until 10) {
//                items.add("アイテム$i")
//            }
//            getRecyclerView()
            customListAdapter(adapter, layoutManager)
//            listItems(items = items, selection = object: ItemListener {
//                override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
//                    viewModel.selectChoice(text.toString())
//                    dismiss()
//                }
//            })
        }

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PostActivity::class.java)
        }
        private const val RC_CHOOSE_IMAGE = 1000
    }
}