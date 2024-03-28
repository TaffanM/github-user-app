package com.taffan.githubuser.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.taffan.githubuser.database.FavoriteDao
import com.taffan.githubuser.database.FavoriteRoomDatabase
import com.taffan.githubuser.database.FavoriteUser
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteUserRepository(application: Application) {
    private val favoriteUserDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        favoriteUserDao = db.favoriteUserDao()
    }

    fun getAllUser(): LiveData<List<FavoriteUser>> = favoriteUserDao.getAllUser()

    fun insert(favoriteUser: FavoriteUser) {
        executorService.execute { favoriteUserDao.insert(favoriteUser)}
    }

    fun delete(favoriteUser: FavoriteUser) {
        executorService.execute { favoriteUserDao.delete(favoriteUser)}
    }

    fun update(favoriteUser: FavoriteUser) {
        executorService.execute { favoriteUserDao.update(favoriteUser)}
    }
}