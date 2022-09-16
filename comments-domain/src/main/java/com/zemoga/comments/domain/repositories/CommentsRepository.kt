package com.zemoga.comments.domain.repositories

import com.app.base.None
import com.app.base.interfaces.Logger
import com.app.core.network.BaseApiResponse
import com.app.core.network.NetworkState
import com.zemoga.comments.domain.data.CommentsDatabase
import com.zemoga.comments.domain.data.CommentsState
import com.zemoga.comments.domain.data.toBaseModel
import com.zemoga.comments.domain.services.CommentsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

/**
 * Represents the repository for comments.
 *
 */
@ExperimentalSerializationApi
class CommentsRepository @Inject constructor(
    private val logger: Logger,
    private val remoteDataSource: CommentsRemoteDataSource,
    private val commentsDatabase: CommentsDatabase,
) : BaseApiResponse(), ICommentsRepository {

    override fun getComments(): Flow<CommentsState> {
        return flow {
            val localCommentsList = commentsDatabase.getCommentsDao().getAllComments()

            if (localCommentsList.isEmpty()) {
                when (val authorsNetworkState = remoteDataSource.fetchComments()) {
                    is NetworkState.Success -> {
                        val comments = authorsNetworkState.data?.toBaseModel() ?: emptyList()
                        commentsDatabase.getCommentsDao().insertAll(comments)

                        emit(CommentsState.GettingCommentsSuccessfully(comments))
                    }
                    is NetworkState.Error -> {
                        logger.e(authorsNetworkState.message.orEmpty(), authorsNetworkState.exception)
                        emit(CommentsState.ErrorGettingComments)
                    }

                    else -> None
                }
            } else {
                emit(CommentsState.GettingCommentsSuccessfully(localCommentsList))
            }
        }
    }
}
