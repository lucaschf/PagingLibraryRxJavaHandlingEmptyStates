package br.com.recyclerviewstateshandler.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import br.com.recyclerviewstateshandler.data.datasource.UsersDataSource
import br.com.recyclerviewstateshandler.data.datasource.factory.UsersDataSourceFactory
import br.com.recyclerviewstateshandler.model.LoadingState
import br.com.recyclerviewstateshandler.model.User
import io.reactivex.disposables.CompositeDisposable

class UsersViewModel : ViewModel() {
    var userList: LiveData<PagedList<User>>

    private val compositeDisposable = CompositeDisposable()
    private val pageSize = 15
    private val sourceFactory: UsersDataSourceFactory

    init {
        sourceFactory = UsersDataSourceFactory(compositeDisposable)

        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()

        userList = LivePagedListBuilder<Long, User>(sourceFactory, config).build()
    }

    fun retry() {
        sourceFactory.usersDataSourceLiveData.value?.retry()
    }

    fun getLoadingState(): LiveData<LoadingState> =
        Transformations.switchMap<UsersDataSource, LoadingState>(
            sourceFactory.usersDataSourceLiveData
        ) { it.loadingState }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}