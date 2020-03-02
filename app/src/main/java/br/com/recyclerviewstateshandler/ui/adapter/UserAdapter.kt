package br.com.recyclerviewstateshandler.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.recyclerviewstateshandler.R
import br.com.recyclerviewstateshandler.model.LoadingState
import br.com.recyclerviewstateshandler.model.User
import br.com.recyclerviewstateshandler.ui.adapter.viewholder.LoadingStateViewHolder
import br.com.recyclerviewstateshandler.ui.adapter.viewholder.UserViewHolder

class UserAdapter(private val retryCallback: () -> Unit) :
    PagedListAdapter<User, RecyclerView.ViewHolder>(UserDiffCallback) {

    private var loadingState: LoadingState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_user -> UserViewHolder.create(parent)
            R.layout.item_loading_state -> LoadingStateViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_user -> (holder as UserViewHolder).bind(getItem(position))
            R.layout.item_loading_state -> (holder as LoadingStateViewHolder).bind(loadingState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return loadingState != null && loadingState != LoadingState.SUCCESS
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_loading_state
        } else {
            R.layout.item_user
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount().plus(if (hasExtraRow()) 1 else 0)
    }

    /**
     * Set the current network state to the adapter
     * but this work only after the initial load
     * and the adapter already have list to add new loading raw to it
     * so the initial loading state the activity responsible for handle it
     *
     * @param newLoadingState the new network state
     */
    fun setLoadingState(newLoadingState: LoadingState?) {
        if (currentList != null) {
            if (currentList!!.size != 0) {
                val previousState = this.loadingState
                val hadExtraRow = hasExtraRow()
                this.loadingState = newLoadingState
                val hasExtraRow = hasExtraRow()
                if (hadExtraRow != hasExtraRow) {
                    if (hadExtraRow) {
                        notifyItemRemoved(super.getItemCount())
                    } else {
                        notifyItemInserted(super.getItemCount())
                    }
                } else if (hasExtraRow && previousState !== newLoadingState) {
                    notifyItemChanged(itemCount - 1)
                }
            }
        }
    }

    companion object {
        val UserDiffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}