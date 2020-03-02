package br.com.recyclerviewstateshandler.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.recyclerviewstateshandler.R
import br.com.recyclerviewstateshandler.model.LoadingState
import br.com.recyclerviewstateshandler.model.LoadingState.Status
import br.com.recyclerviewstateshandler.view.toggleVisibility
import com.google.android.material.button.MaterialButton

class LoadingStateViewHolder(
    view: View,
    private val retryCallBack: () -> Unit
) : RecyclerView.ViewHolder(view) {
    private val btnRetry = itemView.findViewById<MaterialButton>(R.id.retryLoadingButton)
    private val progressBar = itemView.findViewById<ProgressBar>(R.id.loadingProgressBar)
    private val tvErrorMessage = itemView.findViewById<TextView>(R.id.errorMessageTextView)

    init {
        btnRetry.setOnClickListener { retryCallBack() }
    }

    fun bind(loadingState: LoadingState?) {
        loadingState?.let {
            //error message
            tvErrorMessage.toggleVisibility(loadingState.message != null)

            if (loadingState.message != null) {
                tvErrorMessage.text = loadingState.message
            }

            //loading and retry
            btnRetry.toggleVisibility(loadingState.isError)
            progressBar.toggleVisibility(loadingState.status == Status.RUNNING)
        }
    }

    companion object {
        fun create(parent: ViewGroup, retryCallBack: () -> Unit): LoadingStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading_state, parent, false)
            return LoadingStateViewHolder(
                view,
                retryCallBack
            )
        }
    }
}