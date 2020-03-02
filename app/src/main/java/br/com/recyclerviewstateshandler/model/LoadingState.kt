package br.com.recyclerviewstateshandler.model

@Suppress("DataClassPrivateConstructor")
data class LoadingState private constructor(
    val status: Status,
    val message: String? = null
) {
    companion object {
        val SUCCESS = LoadingState(
            Status.SUCCESS
        )
        val RUNNING = LoadingState(
            Status.RUNNING
        )

        fun error(msg: String?) =
            LoadingState(
                Status.DATA_FETCH_FAILURE,
                msg
            )

        fun networkFailure(msg: String?) =
            LoadingState(
                Status.NETWORK_FAILURE,
                msg
            )
    }

    val isError: Boolean
        get() {
            return status == Status.NETWORK_FAILURE || status == Status.DATA_FETCH_FAILURE
        }

    enum class Status {
        RUNNING,
        SUCCESS,
        DATA_FETCH_FAILURE,
        NETWORK_FAILURE
    }
}
