package com.zemoga.author.domain.repositories

import com.app.base.None
import com.app.base.interfaces.Logger
import com.app.core.network.BaseApiResponse
import com.app.core.network.NetworkState
import com.zemoga.author.domain.data.AuthorsDatabase
import com.zemoga.author.domain.data.AuthorsState
import com.zemoga.author.domain.data.toBaseModel
import com.zemoga.author.domain.services.AuthorsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

/**
 * Represents the repository for authors.
 *
 */
@ExperimentalSerializationApi
class AuthorsRepository @Inject constructor(
    private val logger: Logger,
    private val remoteDataSource: AuthorsRemoteDataSource,
    private val authorsDatabase: AuthorsDatabase,
) : BaseApiResponse(), IAuthorsRepository {

    override fun getAuthors(): Flow<AuthorsState> {
        return flow {
            val localAuthorsList = authorsDatabase.getAuthorsDao().getAllAuthors()

            if (localAuthorsList.isEmpty()) {
                when (val authorsNetworkState = remoteDataSource.fetchAuthors()) {
                    is NetworkState.Success -> {
                        val authors = authorsNetworkState.data?.toBaseModel() ?: emptyList()
                        authorsDatabase.getAuthorsDao().insertAll(authors)

                        emit(AuthorsState.GettingAuthorsSuccessfully(authors))
                    }
                    is NetworkState.Error -> {
                        logger.e(authorsNetworkState.message.orEmpty(), authorsNetworkState.exception)
                        emit(AuthorsState.ErrorGettingAuthors)
                    }

                    else -> None
                }
            } else {
                emit(AuthorsState.GettingAuthorsSuccessfully(localAuthorsList))
            }
        }
    }
}
