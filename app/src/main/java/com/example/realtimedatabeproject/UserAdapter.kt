package com.example.realtimedatabeproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realtimedatabeproject.databinding.ItemUserBinding

class UserAdapter(private val userList: List<User>,private val onEditClick:(User) -> Unit, private val  onDeleteClick:(User) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val binding:ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.userName.text = user.name
        holder.binding.userEmail.text = user.email
        holder.binding.editBtn.setOnClickListener {
            onEditClick(user)
        }
        holder.binding.deleteBtn.setOnClickListener {
            onDeleteClick(user)
        }
    }

    override fun getItemCount(): Int = userList.size
}