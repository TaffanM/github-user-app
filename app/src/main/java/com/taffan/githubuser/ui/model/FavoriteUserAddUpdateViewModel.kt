package com.taffan.githubuser.ui.model

import android.app.Application
import androidx.lifecycle.ViewModel
import com.taffan.githubuser.database.FavoriteUser
import com.taffan.githubuser.repository.FavoriteUserRepository

class FavoriteUserAddUpdateViewModel(application: Application): ViewModel() {
    private val mFavoriteRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    fun insert(favoriteUser: FavoriteUser) {
        mFavoriteRepository.insert(favoriteUser)
    }

    fun delete(favoriteUser: FavoriteUser) {
        mFavoriteRepository.delete(favoriteUser)
    }

    fun update(favoriteUser: FavoriteUser) {
        mFavoriteRepository.update(favoriteUser)
    }
}