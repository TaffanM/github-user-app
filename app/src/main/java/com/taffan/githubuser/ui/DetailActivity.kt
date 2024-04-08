package com.taffan.githubuser.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.taffan.githubuser.R
import com.taffan.githubuser.database.FavoriteUser
import com.taffan.githubuser.databinding.ActivityDetailBinding
import com.taffan.githubuser.preferences.SettingPreferences
import com.taffan.githubuser.preferences.dataStore
import com.taffan.githubuser.repository.FavoriteUserRepository
import com.taffan.githubuser.ui.adapter.SectionsPagerAdapter
import com.taffan.githubuser.ui.model.DetailViewModel
import com.taffan.githubuser.ui.model.FavoriteUserAddUpdateViewModel
import com.taffan.githubuser.ui.model.ViewModelFactory
import com.taffan.githubuser.utils.AppExecutors

class DetailActivity : AppCompatActivity() {
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var favoriteUserRepository: FavoriteUserRepository
    private var isFavorite = false

    private val favoriteUserAddUpdateViewModel by viewModels<FavoriteUserAddUpdateViewModel> {
        ViewModelFactory.getInstance(getSettingPreferences(this), favoriteUserRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailViewModel = DetailViewModel()
        favoriteUserRepository = FavoriteUserRepository.getInstance(application, AppExecutors())

        val login = intent.getStringExtra("login")
        val avatarUrl = intent.getStringExtra("avatarUrl")



        Glide.with(this)
            .load(avatarUrl)
            .circleCrop()
            .into(binding.imgDetailPhoto)

        binding.detailUsername.text = login

        detailViewModel.followers.observe(this) { followers ->
            binding.followersNumber.text  = followers?.toString() ?: "0"
        }

        detailViewModel.following.observe(this) { following ->
            binding.followingNumber.text  = following?.toString() ?: "0"
        }

        detailViewModel.name.observe(this) { name ->
            binding.detailName.text = name?.toString() ?: ""
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.htmlUrl.observe(this) {htmlUrl ->
            binding.btnShare.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, htmlUrl)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }

        detailViewModel.findDetailedInfo(login!!)

        favoriteUserRepository.getAllUser().observe(this) { favoriteUsers ->
            isFavorite = favoriteUsers.any { it.username == login }
            updateFavButton(isFavorite)
        }


        binding.btnFav.setOnClickListener {
            if (isFavorite) {
                val favoriteUser = FavoriteUser(login, avatarUrl, true)
                favoriteUserAddUpdateViewModel.deleteUser(favoriteUser)
                Toast.makeText(this@DetailActivity, "User has been removed from favorites", Toast.LENGTH_SHORT).show()
                isFavorite = false
            } else {
                val favoriteUser = FavoriteUser(login, avatarUrl, false)
                favoriteUserAddUpdateViewModel.saveUsers(favoriteUser)
                Toast.makeText(this@DetailActivity, "User has been added to favorites!", Toast.LENGTH_SHORT).show()
                isFavorite = true
            }
            updateFavButton(isFavorite)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this, login)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) {tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.hide()

        onBackPressedCallback()

    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun updateFavButton(isFavorite: Boolean) {
        val drawable = if (isFavorite)  {
            R.drawable.baseline_favorite_24
        } else {
            R.drawable.baseline_favorite_border_24
        }
        binding.btnFav.setImageResource(drawable)
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