package br.com.recyclerviewstateshandler.rest.service

import br.com.recyclerviewstateshandler.model.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubService {

    @GET("/users")
    fun getUsers(
        @Query("since") userId: Long,
        @Query("per_page") perPage: Int
    ): Single<List<User>>
}
