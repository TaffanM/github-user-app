package com.taffan.githubuser.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.taffan.githubuser.database.FavoriteDao
import com.taffan.githubuser.database.FavoriteRoomDatabase
import com.taffan.githubuser.database.FavoriteUser
import com.taffan.githubuser.utils.AppExecutors

class FavoriteUserRepository private constructor(application: Application, private val appExecutors: AppExecutors) {
    private val favoriteUserDao: FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteUserRepository? = null

        fun getInstance(application: Application, appExecutors: AppExecutors): FavoriteUserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FavoriteUserRepository(application, appExecutors)
                INSTANCE = instance
                instance
            }
        }
    }


    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        favoriteUserDao = db.favoriteUserDao()
    }

    fun getAllUser(): LiveData<List<FavoriteUser>> = favoriteUserDao.getAllUser()

    fun setFavoriteUsers(user: FavoriteUser, favoriteState: Boolean) {
        appExecutors.diskIO.execute{
            user.isFavorited = favoriteState
            favoriteUserDao.update(user)
        }
    }

    fun insert(favoriteUser: FavoriteUser) {
        appExecutors.diskIO.execute { favoriteUserDao.insert(favoriteUser)}
    }

    fun delete(favoriteUser: FavoriteUser) {
        appExecutors.diskIO.execute { favoriteUserDao.delete(favoriteUser)}
    }





}