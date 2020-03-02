package br.com.recyclerviewstateshandler.model

import android.view.View.OnClickListener
import androidx.annotation.DrawableRes

class LoadingStateViewData private constructor(
    @DrawableRes private val image: Int,
    private val title: String?,
    private var message: String,
    private var buttonText: String?,
    private var buttonListener: OnClickListener?
) {
    fun getTitle() = title
    fun getMessage() = message
    fun getButtonText() = buttonText
    fun getButtonListener() = buttonListener
    fun getImageResource() = image

    fun updateMessage(message: String): LoadingStateViewData {
        this.message = message
        return this
    }

    fun setActionButton(text: String, listener: OnClickListener?): LoadingStateViewData {
        buttonText = text
        buttonListener = listener

        return this
    }

    class Builder(
        private var message: String
    ) {
        @DrawableRes
        private var image: Int = 0
        private var title: String? = null
        private var buttonText: String? = null
        private var buttonListener: OnClickListener? = null

        fun setImageDrawable(@DrawableRes image: Int): Builder {
            this.image = image
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setActionButton(text: String?, listener: OnClickListener?): Builder {
            buttonText = text
            buttonListener = listener
            return this
        }

        fun build() =
            LoadingStateViewData(
                image,
                title,
                message,
                buttonText,
                buttonListener
            )
    }
}