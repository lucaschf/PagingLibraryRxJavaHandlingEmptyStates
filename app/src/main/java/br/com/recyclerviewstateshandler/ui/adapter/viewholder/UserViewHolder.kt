package br.com.recyclerviewstateshandler.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.recyclerviewstateshandler.R
import br.com.recyclerviewstateshandler.model.User
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_user.view.*

class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(user: User?) {
        itemView.UserName.text = user?.login
        Glide.with(itemView.context)
            .load(user?.avatarUrl)
            .into(itemView.UserAvatar)
        itemView.siteAdminIcon.visibility = if (user!!.siteAdmin) View.VISIBLE else View.GONE
    }

    companion object {
        fun create(parent: ViewGroup): UserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_user, parent, false)
            return UserViewHolder(
                view
            )
        }
    }
}