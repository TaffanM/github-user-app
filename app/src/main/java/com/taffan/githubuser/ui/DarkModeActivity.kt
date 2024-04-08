package com.taffan.githubuser.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.taffan.githubuser.databinding.ActivityDarkModeBinding
import com.taffan.githubuser.preferences.SettingPreferences
import com.taffan.githubuser.preferences.dataStore
import com.taffan.githubuser.repository.FavoriteUserRepository
import com.taffan.githubuser.ui.model.MainViewModel
import com.taffan.githubuser.ui.model.ViewModelFactory
import com.taffan.githubuser.utils.AppExecutors

class DarkModeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDarkModeBinding
    private lateinit var favoriteUserRepository: FavoriteUserRepository

    private val mainViewModel by viewModels<MainViewModel>() {
        ViewModelFactory.getInstance(getSettingPreferences(this),favoriteUserRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDarkModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteUserRepository = FavoriteUserRepository.getInstance(application, AppExecutors())

        mainViewModel.getThemeSettings().observe(this) {isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }

        onBackPressedCallback()
        
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