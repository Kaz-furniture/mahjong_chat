package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.google.api.Distribution
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.adapter.TileListAdapter
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Tile
import com.kaz_furniture.mahjongChat.databinding.*
import com.kaz_furniture.mahjongChat.view.TilesView
import com.kaz_furniture.mahjongChat.viewModel.PostViewModel
import com.yalantis.ucrop.UCrop
import timber.log.Timber
import java.io.File

class PostActivity: BaseActivity() {
    private val viewModel: PostViewModel by viewModels()
    private var uCropSrcUri: Uri? = null
    lateinit var binding: ActivityPostBinding
    lateinit var layoutManager: GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post)
        binding.lifecycleOwner = this
        layoutManager = GridLayoutManager(this, 4)
        binding.explanation = viewModel.explanationInput
        binding.postButton.setOnClickListener {
            viewModel.post(this, binding)
        }
        binding.selectImageButton.setOnClickListener {
            selectImage()
        }
        title = getString(R.string.postCreate)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.createChoicesButton.setOnClickListener {
            showTileSelectDialog()
        }
        viewModel.selectedChoices.observe(this, Observer {
            addAllChoiceLayout(it)
        })
    }

    private fun addAllChoiceLayout(list: List<Choice>) {
        Timber.d("addAllChoiceLayout listSize:${list.size}")
        binding.choicesParentView.apply {
            removeAllViews()
            visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }
        list.forEach { targetChoice ->
            binding.choicesParentView.addView(ListChoiceBinding.inflate(LayoutInflater.from(this), null, false).apply {
                choice = targetChoice
                deleteButton.setOnClickListener {
                    viewModel.deleteChoice(targetChoice)
                }
            }.root)
        }
    }

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

    private fun getChildView() {
//        val childBinding = ListChoiceBinding.inflate(
//                LayoutInflater.from(this), null, false)
//        childBinding.choice = viewModel.choice
//        Timber.d("selectedChoice = ${viewModel.choice.tileType.imageId}, ${Tile.M3.imageId}")
//        setContentView(childBinding.root)
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
                    showTileSelectDialog()
                }
            }
            setContentView(dialogBinding.root)
        }
    }

    private fun showTileSelectDialog() {
        MaterialDialog(this).show {
            cancelable(false)
            title(R.string.selectTile)
            val binding = DialogSelectTileBinding.inflate(LayoutInflater.from(this@PostActivity), null, false)
            binding.apply {
                tilesView.customAdapter.refresh(tileList)
                closeButton.setOnClickListener {
                    dismiss()
                }
                nextButton.setOnClickListener {
                    showWaySelectDialog()
                    dismiss()
                }
            }
            setContentView(binding.root)
        }
    }

    private fun showWaySelectDialog() {
        MaterialDialog(this).show {
            cancelable(false)
            title(R.string.selectTile)
            listItems(R.array.actions, selection = object: ItemListener {
                override fun invoke(dialog: MaterialDialog, index: Int, text: CharSequence) {
                    viewModel.setWay(index)
                    dismiss()
                }
            })
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PostActivity::class.java)
        }
        private const val RC_CHOOSE_IMAGE = 1000
        val tileList = listOf<Tile>(
                Tile.MSection, Tile.M1, Tile.M2, Tile.M3, Tile.M4, Tile.M5, Tile.M5R, Tile.M6, Tile.M7, Tile.M8, Tile.M9,
                Tile.PSection, Tile.P1, Tile.P2, Tile.P3, Tile.P4, Tile.P5, Tile.P5R, Tile.P6, Tile.P7, Tile.P8, Tile.P9,
                Tile. SSection, Tile.S1, Tile.S2, Tile.S3, Tile.S4, Tile.S5, Tile.S5R, Tile.S6, Tile.S7, Tile.S8, Tile.S9,
                Tile.ZSection, Tile.Z1, Tile.Z2, Tile.Z3, Tile.Z4, Tile.Z5, Tile.Z6, Tile.Z7)
    }
}