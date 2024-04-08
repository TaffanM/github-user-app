package com.taffan.githubuser.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.taffan.githubuser.database.FavoriteUser
import com.taffan.githubuser.databinding.ActivityFavoriteUserBinding
import com.taffan.githubuser.preferences.SettingPreferences
import com.taffan.githubuser.preferences.dataStore
import com.taffan.githubuser.repository.FavoriteUserRepository
import com.taffan.githubuser.ui.adapter.FavoriteAdapter
import com.taffan.githubuser.ui.model.FavoriteUserAddUpdateViewModel
import com.taffan.githubuser.ui.model.ViewModelFactory
import com.taffan.githubuser.utils.AppExecutors

class FavoriteUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteUserBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteUserRepository: FavoriteUserRepository

    private val favoriteUserAddUpdateViewModel by viewModels<FavoriteUserAddUpdateViewModel> {
        ViewModelFactory.getInstance(getSettingPreferences(this), favoriteUserRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteUserRepository = FavoriteUserRepository.getInstance(application, AppExecutors())
        favoriteAdapter = FavoriteAdapter()

        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = layoutManager

        favoriteUserAddUpdateViewModel.getFavoritedUsers().observe(this) {favoriteUsers ->
            setUserData(favoriteUsers)
            binding.progressBar.visibility = View.GONE
        }

        favoriteUserAddUpdateViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        favoriteAdapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback{
            override fun onItemClicked(user: FavoriteUser) {
                showClickedUserPage(user)
            }
        })

        onBackPressedCallback()

    }

    private fun setUserData(user: List<FavoriteUser>) {
        favoriteAdapter.submitList(user)
        binding.rvFavorite.adapter = favoriteAdapter
    }

    private fun getSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showClickedUserPage(user: FavoriteUser) {
        val intent = Intent(this@FavoriteUserActivity, DetailActivity::class.java).apply {
            putExtra("login", user.username)
            putExtra("avatarUrl", user.avatarUrl)
        }
        startActivity(intent)
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