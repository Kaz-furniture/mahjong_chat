package com.kaz_furniture.mahjongChat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.kaz_furniture.mahjongChat.MahjongChatApplication.Companion.myUser
import com.kaz_furniture.mahjongChat.R
import com.kaz_furniture.mahjongChat.activity.DMDetailActivity
import com.kaz_furniture.mahjongChat.data.DMRoom
import com.kaz_furniture.mahjongChat.databinding.DialogRoomCreateBinding
import com.kaz_furniture.mahjongChat.databinding.FragmentFourthBinding
import com.kaz_furniture.mahjongChat.viewModel.MainViewModel

class FourthFragment : Fragment(R.layout.fragment_fourth) {

    private var binding: FragmentFourthBinding? = null
    private lateinit var layoutManager: LinearLayoutManager
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDMRooms()
        val bindingData: FragmentFourthBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?:return
        layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
                )
        binding?.fab?.setOnClickListener {
            showCreateRoomDialog()
        }
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.getDMRooms()
            binding?.swipeRefresh?.isRefreshing = false
        }
        viewModel.dMRoomList.observe(viewLifecycleOwner, Observer {
            binding?.dMRoomView?.customAdapter?.refresh(it)
        })
        viewModel.selectedDMRoom.observe(viewLifecycleOwner, Observer {
            it?.also {
                launchDMDetailActivity(it)
                viewModel.clearSelect()
            } ?:return@Observer
        })
        viewModel.userSelected.observe(viewLifecycleOwner, Observer {
            viewModel.createDMRoom(it)
        })
    }

    private fun showCreateRoomDialog() {
        if (FirebaseAuth.getInstance().currentUser?.uid.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "ログインしてください", Toast.LENGTH_SHORT).show()
            return
        }
        MaterialDialog(requireContext()).show {
            cancelable(false)
            val binding = DialogRoomCreateBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            binding.apply {
                followingUsersView.customAdapter.refresh(myUser.followingUserIds)
                closeButton.setOnClickListener {
                    dismiss()
                }
            }
            viewModel.userSelected.observe(viewLifecycleOwner, Observer {
                dismiss()
            })
            setContentView(binding.root)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DM_DETAIL) {
            viewModel.getDMRooms()
        }
    }

    private fun launchDMDetailActivity(data: DMRoom) {
        val intent = DMDetailActivity.newIntent(requireContext(), data)
        startActivityForResult(intent, REQUEST_CODE_DM_DETAIL)
    }

    companion object {
        private const val REQUEST_CODE_DM_DETAIL = 4000
    }
}