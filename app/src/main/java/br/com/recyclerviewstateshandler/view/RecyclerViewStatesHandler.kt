package br.com.recyclerviewstateshandler.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import br.com.recyclerviewstateshandler.R
import br.com.recyclerviewstateshandler.model.LoadingState
import br.com.recyclerviewstateshandler.model.LoadingStateViewData
import com.google.android.material.button.MaterialButton

class RecyclerViewStatesHandler(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    //region Views
    private val ivState: ImageView
    private val tvTitle: TextView
    private val tvMessage: TextView
    private val btnAction: MaterialButton
    private val loadingProgress: ContentLoadingProgressBar
    private val mRecyclerView: RecyclerView
    private val emptyStateView: View
    //endregion

    //region Default empty states
    val defaultDataFetchErrorState =
        LoadingStateViewData.Builder(
            context.getString(R.string.we_re_having_some_difficulties_please_try_again)
        )
            .setActionButton(null, null)
            .setTitle(context.getString(R.string.whoops))
            .setImageDrawable(R.drawable.img_undraw_winter_magic)
            .build()

    val defaultEmptyListState =
        LoadingStateViewData.Builder(
            ""
        )
            .setActionButton(null, null)
            .setTitle(context.getString(R.string.nothing_was_found))
            .setImageDrawable(R.drawable.img_undraw_empty)
            .build()

    val deFaultNetworkErrorState =
        LoadingStateViewData.Builder(
            context.getString(R.string.please_check_your_internet_connection_and_try_again)
        )
            .setActionButton(null, null)
            .setTitle(context.getString(R.string.network_failure))
            .setImageDrawable(R.drawable.img_undraw_server_down)
            .build()
    //endregion

    // states Data
    private var networkFailureState = deFaultNetworkErrorState
    private var dataFetchErrorState = defaultDataFetchErrorState
    private var emptyListState = defaultEmptyListState

    init {
        View.inflate(
            context,
            R.layout.content_recycler_states_handler, this
        )

        loadingProgress = findViewById(R.id.stateHandlerLoadingProgress)
        btnAction = findViewById(R.id.emptyStateAction)
        tvTitle = findViewById(R.id.emptyStateTitle)
        ivState = findViewById(R.id.emptyStateImage)
        tvMessage = findViewById(R.id.emptyStateDescription)
        mRecyclerView = findViewById(R.id.stateHandlerRecyclerView)
        emptyStateView = findViewById(R.id.emptyStateView)
    }

    val recyclerView: RecyclerView
        get() {
            return mRecyclerView
        }

    fun <T> handleLoadingStates(
        loadingState: LiveData<LoadingState>,
        data: LiveData<PagedList<T>>,
        handleEmptyStates: Boolean
    ) {
        if (data.value == null || data.value!!.isEmpty()) {
            if (loadingState.value == LoadingState.RUNNING)
                showProgress()
            else {
                hideProgress()

                loadingState.value?.let {
                    when (it.status) {
                        LoadingState.Status.DATA_FETCH_FAILURE -> showEmptyState(
                            dataFetchErrorState.updateMessage(
                                it.message!!
                            )
                        )
                        LoadingState.Status.NETWORK_FAILURE -> showEmptyState(
                            networkFailureState.updateMessage(
                                it.message!!
                            )
                        )
                        else -> if (handleEmptyStates) showEmptyState(emptyListState) else showRecyclerView()
                    }
                }
            }
        } else
            showRecyclerView()
    }

    // region Progress handling
    private fun showProgress() {
        mRecyclerView.hide()
        emptyStateView.hide()
        loadingProgress.show()
    }

    private fun hideProgress() {
        loadingProgress.hide()
    }
    // endregion

    // region Empty state show
    private fun showEmptyState(
        title: String?,
        message: String?,
        @DrawableRes imageResource: Int,
        actionName: String?,
        listener: OnClickListener? = null
    ) {
        resetEmptyStateFieldsVisibility()

        setTitle(title)
        setMessage(message)
        setImage(imageResource)
        setupActionButton(actionName, listener)

        hideProgress()
        mRecyclerView.hide()
        emptyStateView.show()
    }

    private fun resetEmptyStateFieldsVisibility() {
        tvMessage.show()
        tvTitle.show()
        btnAction.show()
        ivState.show()
    }

    private fun showEmptyState(loadingStateViewData: LoadingStateViewData) {
        showEmptyState(
            title = loadingStateViewData.getTitle(),
            message = loadingStateViewData.getMessage(),
            imageResource = loadingStateViewData.getImageResource(),
            actionName = loadingStateViewData.getButtonText(),
            listener = loadingStateViewData.getButtonListener()
        )
    }
    // endregion

    private fun showRecyclerView() {
        hideProgress()
        emptyStateView.hide()
        mRecyclerView.show()
    }

    // region Data binding
    private fun setImage(@DrawableRes imageResource: Int) {
        if (imageResource != 0) {
            ivState.setImageResource(imageResource)
            ivState.show()
        } else
            ivState.hide()
    }

    private fun setTitle(title: String?) {
        tvTitle.text = title

        if (TextUtils.isEmpty(title))
            tvTitle.hide()
        else
            tvTitle.show()
    }

    private fun setMessage(message: String?) {
        tvMessage.text = message

        if (TextUtils.isEmpty(message))
            tvMessage.hide()
        else
            tvMessage.show()
    }

    private fun setupActionButton(text: String?, listener: OnClickListener?) {
        if (TextUtils.isEmpty(text) || listener == null) {
            btnAction.hide()
        } else {
            btnAction.text = text
            btnAction.setOnClickListener(listener)
            btnAction.show()
        }
    }
    // endregion

    // setters
    fun setNetworkFailureState(networkFailureState: LoadingStateViewData) {
        this.networkFailureState = networkFailureState
    }

    fun setDataFetchErrorState(dataFetchErrorState: LoadingStateViewData) {
        this.dataFetchErrorState = dataFetchErrorState
    }

    fun setEmptyListState(emptyListState: LoadingStateViewData) {
        this.emptyListState = emptyListState
    }
}


