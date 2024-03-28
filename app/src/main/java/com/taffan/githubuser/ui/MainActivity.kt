package com.taffan.githubuser.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.taffan.githubuser.data.response.ItemsItem
import com.taffan.githubuser.databinding.ActivityMainBinding
import com.taffan.githubuser.ui.adapter.UserAdapter
import com.taffan.githubuser.ui.model.DetailViewModel
import com.taffan.githubuser.ui.model.MainViewModel
import com.taffan.githubuser.ui.model.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter

    private val detailViewModel by viewModels<DetailViewModel>() {
        ViewModelFactory.getInstance(application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        userAdapter = UserAdapter()
        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(item: ItemsItem) {
                showDetailedUserPage(item)
            }

        })

        val mainViewModel = obtainMainViewModel(this@MainActivity)
        mainViewModel.listItems.observe(this) { listItems ->
            setUserData(listItems)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager


        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchText = searchView.text.toString().trim()
                    mainViewModel.findUserGitHub(searchText)
                    searchView.hide()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        onBackPressedCallback()

    }

    private fun setUserData(user: List<ItemsItem>) {
        userAdapter.submitList(user)
        binding.rvUser.adapter = userAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showDetailedUserPage(item: ItemsItem) {
        val intentDetail = Intent(this@MainActivity, DetailActivity::class.java)
        intentDetail.putExtra("login", item.login)
        intentDetail.putExtra("avatarUrl", item.avatarUrl)
        detailViewModel.findDetailedInfo(item.login)
        startActivity(intentDetail)
    }


    private fun onBackPressedCallback() {
        val dispatcher = onBackPressedDispatcher

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        dispatcher.addCallback(this, onBackPressedCallback)

    }

    private fun obtainDetailViewModel(activity: AppCompatActivity): DetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[DetailViewModel::class.java]
    }

    private fun obtainMainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

}