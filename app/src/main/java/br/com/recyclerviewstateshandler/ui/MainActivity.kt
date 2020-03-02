package br.com.recyclerviewstateshandler.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.recyclerviewstateshandler.R
import br.com.recyclerviewstateshandler.ui.adapter.UserAdapter
import br.com.recyclerviewstateshandler.ui.viewmodel.UsersViewModel
import br.com.recyclerviewstateshandler.view.RecyclerViewStatesHandler

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerViewStatesHandler: RecyclerViewStatesHandler
    private lateinit var userRecyclerView: RecyclerView

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UsersViewModel::class.java)
    }

    private val loadingStates by lazy { userViewModel.getLoadingState() }
    private val userAdapter by lazy {
        UserAdapter { userViewModel.retry() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewStatesHandler = findViewById(R.id.recyclerViewStateHandler)
        userRecyclerView = recyclerViewStatesHandler.recyclerView
        initAdapter()
        initStateObserver()
    }

    private fun initAdapter() {
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = userAdapter

        userViewModel.userList.observe(this, Observer { pagedList ->
            userAdapter.submitList(pagedList)
            handleStates(true)
        })
    }

    private fun initStateObserver() {
        recyclerViewStatesHandler.setDataFetchErrorState(
            recyclerViewStatesHandler.defaultDataFetchErrorState.setActionButton(
                getString(R.string.retry),
                View.OnClickListener { userViewModel.retry() })
        )

        recyclerViewStatesHandler.setNetworkFailureState(
            recyclerViewStatesHandler.deFaultNetworkErrorState.setActionButton(
                getString(R.string.retry),
                View.OnClickListener { userViewModel.retry() })
        )

        recyclerViewStatesHandler.setEmptyListState(
            recyclerViewStatesHandler.defaultEmptyListState.setActionButton(
                getString(R.string.go_back),
                View.OnClickListener { finish() })
        )

        loadingStates.observe(this, Observer { state ->
            handleStates(false)
            userAdapter.setLoadingState(state)
        })
    }

    private fun handleStates(handleEmpty: Boolean) {
        recyclerViewStatesHandler.handleLoadingStates(
            loadingStates,
            userViewModel.userList,
            handleEmpty
        )
    }
}
