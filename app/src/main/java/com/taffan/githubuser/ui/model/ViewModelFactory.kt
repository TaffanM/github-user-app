package com.taffan.githubuser.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taffan.githubuser.preferences.SettingPreferences
import com.taffan.githubuser.repository.FavoriteUserRepository

class ViewModelFactory private constructor(private val pref: SettingPreferences, private val favoriteUserRepository: FavoriteUserRepository) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(pref: SettingPreferences, favoriteUserRepository: FavoriteUserRepository): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(pref, favoriteUserRepository)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if (modelClass.isAssignableFrom(FavoriteUserAddUpdateViewModel::class.java)) {
            return FavoriteUserAddUpdateViewModel(favoriteUserRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref) as T
         }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}