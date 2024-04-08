package com.taffan.githubuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taffan.githubuser.database.FavoriteUser
import com.taffan.githubuser.databinding.UserItemRowBinding


class FavoriteAdapter : ListAdapter<FavoriteUser, FavoriteAdapter.UserViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteUser> =
            object : DiffUtil.ItemCallback<FavoriteUser>() {
                override fun areItemsTheSame(oldItem: FavoriteUser, newItem: FavoriteUser): Boolean {
                    return oldItem.username == newItem.username
                }

                override fun areContentsTheSame(oldItem: FavoriteUser, newItem: FavoriteUser): Boolean {
                    return oldItem == newItem
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(user)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: FavoriteUser)
    }

    inner class UserViewHolder(private val binding: UserItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: FavoriteUser) {
            binding.tvItemName.text = user.username
            Glide.with(binding.root.context)
                .load(user.avatarUrl)
                .circleCrop()
                .into(binding.imgItemPhoto)
        }
    }
}

