package br.com.recyclerviewstateshandler.data.datasource.factory

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import br.com.recyclerviewstateshandler.data.datasource.UsersDataSource
import br.com.recyclerviewstateshandler.model.User
import io.reactivex.disposables.CompositeDisposable

class UsersDataSourceFactory(
    private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Long, User>() {

    val usersDataSourceLiveData = MutableLiveData<UsersDataSource>()

    override fun create(): DataSource<Long, User> {
        val usersDataSource = UsersDataSource(compositeDisposable)
        usersDataSourceLiveData.postValue(usersDataSource)
        return usersDataSource
    }
}
