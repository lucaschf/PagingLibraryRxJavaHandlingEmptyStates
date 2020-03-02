package br.com.recyclerviewstateshandler.data.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import br.com.recyclerviewstateshandler.model.LoadingState
import br.com.recyclerviewstateshandler.model.User
import br.com.recyclerviewstateshandler.rest.RetrofitClient
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UsersDataSource(
    private val compositeDisposable: CompositeDisposable
) : ItemKeyedDataSource<Long, User>() {

    val loadingState = MutableLiveData<LoadingState>()
    private val firstUser: Long = 1
    private val service = RetrofitClient().githubService

    private var retryCompletable: Completable? = null

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { },
                        { throwable ->
                            Log.d(
                                this::class.java.canonicalName,
                                throwable?.message.toString()
                            )
                        })
            )
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<User>
    ) {
        updateState(LoadingState.RUNNING)

        compositeDisposable.add(
            service.getUsers(firstUser, params.requestedLoadSize).subscribe({ users ->
                setRetry(null)
                updateState(LoadingState.SUCCESS)
                callback.onResult(users)
            }, { throwable ->
                setRetry(Action { loadInitial(params, callback) })
                updateState(throwable)
            })
        )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<User>) {
        updateState(LoadingState.RUNNING)

        compositeDisposable.add(
            service.getUsers(params.key, params.requestedLoadSize).subscribe({ users ->
                setRetry(null)
                updateState(LoadingState.SUCCESS)
                callback.onResult(users)
            }, { throwable ->
                setRetry(Action { loadAfter(params, callback) })
                updateState(throwable)
            })
        )
    }

    private fun updateState(throwable: Throwable) {
        if (throwable.isNetworkException())
            updateState(LoadingState.networkFailure(throwable.localizedMessage))
        else
            updateState(LoadingState.error(throwable.localizedMessage))
    }

    private fun updateState(state: LoadingState) {
        loadingState.postValue(state)
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<User>) {
        // ignored
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

    override fun getKey(item: User): Long {
        return item.id
    }

    private fun Throwable.isNetworkException(): Boolean {
        return (this is SocketTimeoutException || this is UnknownHostException)
    }
}