package com.taffan.githubuser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.taffan.githubuser.R
import com.taffan.githubuser.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var imgDetail: ImageView
    private lateinit var detailUsername: TextView
    private lateinit var detailName: TextView
    private lateinit var binding: ActivityDetailBinding
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgDetail = findViewById(R.id.img_detail_photo)
        detailUsername = findViewById(R.id.detail_username)
        detailName = findViewById(R.id.detail_name)

        val login = intent.getStringExtra("login")
        val avatarUrl = intent.getStringExtra("avatarUrl")


        Glide.with(this)
            .load(avatarUrl)
            .circleCrop()
            .into(imgDetail)

        detailUsername.text = login


        val detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]
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

        detailViewModel.findDetailedInfo(login!!)




        val sectionsPagerAdapter = SectionsPagerAdapter(this, login ?: "")
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