package com.taffan.githubuser.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taffan.githubuser.database.FavoriteUser
import com.taffan.githubuser.repository.FavoriteUserRepository
import kotlinx.coroutines.launch

class FavoriteUserAddUpdateViewModel(private val favoriteUserRepository: FavoriteUserRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getFavoritedUsers() = favoriteUserRepository.getAllUser()

    fun saveUsers(user: FavoriteUser) {
        viewModelScope.launch {
            favoriteUserRepository.insert(user)
            favoriteUserRepository.setFavoriteUsers(user, true)
        }
    }
    fun deleteUser(user: FavoriteUser) {
        viewModelScope.launch {
            favoriteUserRepository.setFavoriteUsers(user, false)
            favoriteUserRepository.delete(user)
        }
    }
}