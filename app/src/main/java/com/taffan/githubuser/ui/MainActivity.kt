package com.taffan.githubuser.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.taffan.githubuser.R
import com.taffan.githubuser.data.response.ItemsItem
import com.taffan.githubuser.databinding.ActivityMainBinding
import com.taffan.githubuser.preferences.SettingPreferences
import com.taffan.githubuser.preferences.dataStore
import com.taffan.githubuser.repository.FavoriteUserRepository
import com.taffan.githubuser.ui.adapter.UserAdapter
import com.taffan.githubuser.ui.model.DetailViewModel
import com.taffan.githubuser.ui.model.MainViewModel
import com.taffan.githubuser.ui.model.ViewModelFactory
import com.taffan.githubuser.utils.AppExecutors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var favoriteUserRepository: FavoriteUserRepository


    private val mainViewModel by viewModels<MainViewModel>() {
        ViewModelFactory.getInstance(getSettingPreferences(this), favoriteUserRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailViewModel = DetailViewModel()

        favoriteUserRepository = FavoriteUserRepository.getInstance(application, AppExecutors())
        userAdapter = UserAdapter()
        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(item: ItemsItem) {
                showDetailedUserPage(item)
            }

        })

        findViewById<View>(R.id.favorite).setOnClickListener {
            val intent = Intent(this, FavoriteUserActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.settings).setOnClickListener {
            val intent = Intent(this, DarkModeActivity::class.java)
            startActivity(intent)
        }

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

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        onBackPressedCallback()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_searchbar, menu)
        return super.onCreateOptionsMenu(menu)
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

    private fun getSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
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

}