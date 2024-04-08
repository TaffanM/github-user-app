package com.taffan.githubuser.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.taffan.githubuser.data.response.ItemsItem
import com.taffan.githubuser.databinding.FragmentFollowBinding
import com.taffan.githubuser.ui.adapter.FollowAdapter
import com.taffan.githubuser.ui.model.FollowViewModel


class FollowFragment : Fragment() {
    private lateinit var binding: FragmentFollowBinding
//    private val binding get() = _binding!!
    private val followViewModel: FollowViewModel by viewModels()
    private lateinit var followAdapter: FollowAdapter
    private var position: Int? = null
    private var username: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        followAdapter = FollowAdapter()
        binding.rvFollow.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = followAdapter
        }
        val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvFollow.addItemDecoration(itemDecoration)

        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
        if (position == 1) {
            followViewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }
            followViewModel.findFollowerUser(username = username ?: "")
            followViewModel.followerList.observe(viewLifecycleOwner) { followerList ->
                followAdapter.submitList(followerList)
            }
            followAdapter.setOnItemClickCallback(object : FollowAdapter.OnItemClickCallback {
                override fun onItemClicked(item: ItemsItem) {
                    showClickedUserPage(item)
                }

            })
        } else {
            followViewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }
            followViewModel.findFollowingUser(username = username ?: "")
            followViewModel.followingList.observe(viewLifecycleOwner) { followingList ->
                followAdapter.submitList(followingList)
            }
            followAdapter.setOnItemClickCallback(object : FollowAdapter.OnItemClickCallback {
                override fun onItemClicked(item: ItemsItem) {
                    showClickedUserPage(item)
                }

            })
        }
        return binding.root
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showClickedUserPage(item: ItemsItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("login", item.login)
            putExtra("avatarUrl", item.avatarUrl)
        }
        startActivity(intent)
    }

    companion object {
        const val ARG_POSITION = "arg_position"
        const val ARG_USERNAME = "arg_username"
    }
}