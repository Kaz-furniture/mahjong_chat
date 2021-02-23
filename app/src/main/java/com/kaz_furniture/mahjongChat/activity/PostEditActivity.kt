package com.kaz_furniture.mahjongChat.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.data.Choice
import com.kaz_furniture.mahjongChat.data.Post
import com.kaz_furniture.mahjongChat.databinding.ActivityPostEditBinding
import com.kaz_furniture.mahjongChat.databinding.DialogSelectTilePostEditBinding
import com.kaz_furniture.mahjongChat.databinding.ListChoiceBinding
import com.kaz_furniture.mahjongChat.viewModel.PostEditViewModel
import com.yalantis.ucrop.UCrop
import timber.log.Timber
import java.io.File

class PostEditActivity: BaseActivity() {
    lateinit var binding: ActivityPostEditBinding
    private val viewModel: PostEditViewModel by viewModels()
    lateinit var post: Post
    private var uCropSrcUri: Uri? = null
        set(value) {
            viewModel.imageOK = true
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_edit)
        binding.lifecycleOwner = this
        val postGet = (intent.getSerializableExtra(KEY1) as? Post) ?: kotlin.run {
            finish()
            Toast.makeText(this, "取得失敗", Toast.LENGTH_SHORT).show()
            return@run Post()
        }
        post = postGet.also {
            binding.post = it
            viewModel.explanationInput.postValue(it.explanation)
        }
        val choicesGet = mutableListOf<Choice>().apply {
            addAll(intent.getSerializableExtra(KEY2) as? ArrayList<Choice> ?: listOf())
            viewModel.selectedChoices.postValue(this)
            viewModel.savedChoices = this
        }
        viewModel.selectedChoices.postValue(choicesGet)
        binding.selectImageButton.setOnClickListener {
            selectImage()
        }
        binding.createChoicesButton.setOnClickListener {
            showTileSelectDialog()
        }
        binding.postButton.setOnClickListener {
            binding.progressCircular.visibility = android.widget.ProgressBar.VISIBLE
            buttonClick()
        }
        binding.explanation = viewModel.explanationInput
        title = getString(R.string.edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel.selectedChoices.observe(this, Observer {
            addAllChoiceLayout(it)
        })
        viewModel.imageUploaded.observe(this, Observer {
            binding.progressCircular.visibility = android.widget.ProgressBar.GONE
            finish()
        })
    }

    private fun buttonClick() {
        viewModel.choicesUpdate(post.postId)
        viewModel.postUpdate(post)
        newImageCheck()
        if (uCropSrcUri == null) {
            finish()
        }
    }

    private fun newImageCheck() {
        if (uCropSrcUri != null) {
            val imageView = binding.postImageView
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            viewModel.postImageUpload(bitmap)
        } else return
    }

    private fun addAllChoiceLayout(list: List<Choice>) {
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

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .setType("image/*")
        startActivityForResult(intent, RC_CHOOSE_IMAGE)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showTileSelectDialog() {
        MaterialDialog(this).show {
            cancelable(false)
            title(R.string.selectTile)
            val binding = DialogSelectTilePostEditBinding.inflate(LayoutInflater.from(this@PostEditActivity), null, false)
            binding.apply {
                tilesView.customAdapter.refresh(PostActivity.tileList)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
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
                    val resultUri = UCrop.getOutput(data)
                    uCropSrcUri = resultUri
                    val cropSrc = uCropSrcUri ?:return
                    val inputStream = contentResolver.openInputStream(cropSrc)
                    val image = BitmapFactory.decodeStream(inputStream)
                    val imageView = binding.postImageView
                    imageView.setImageBitmap(image)
                }

                UCrop.RESULT_ERROR -> {
                    uCropSrcUri = null
                    Toast.makeText(this, "取得失敗", Toast.LENGTH_SHORT).show()
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
                    .start(this@PostEditActivity)
        }
    }

    companion object {
        private const val RC_CHOOSE_IMAGE =1001
        private const val KEY1 = "KEY_POST"
        private const val KEY2 = "KEY_CHOICES"
        fun newIntent(context: Context, post: Post, choices: ArrayList<Choice>): Intent {
            return Intent(context, PostEditActivity::class.java).apply {
                putExtra(KEY1, post)
                putExtra(KEY2, choices)
            }
        }
    }
}