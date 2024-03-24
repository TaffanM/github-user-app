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


/**
 * A simple [Fragment] subclass.
 * Use the [FollowerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FollowViewModel by viewModels()
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
        // Inflate the layout for this fragment
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            viewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }
            viewModel.findFollowerUser(username = username ?: "")
            viewModel.followerList.observe(viewLifecycleOwner) { followerList ->
                followAdapter.submitList(followerList)
            }
            followAdapter.setOnItemClickCallback(object : FollowAdapter.OnItemClickCallback {
                override fun onItemClicked(item: ItemsItem) {
                    showClickedUserPage(item)
                }

            })
        } else {
            viewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }
            viewModel.findFollowingUser(username = username ?: "")
            viewModel.followingList.observe(viewLifecycleOwner) { followingList ->
                followAdapter.submitList(followingList)
            }
            followAdapter.setOnItemClickCallback(object : FollowAdapter.OnItemClickCallback {
                override fun onItemClicked(item: ItemsItem) {
                    showClickedUserPage(item)
                }

            })
        }


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