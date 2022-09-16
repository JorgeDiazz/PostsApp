package com.zemoga.posts.domain.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.base.interfaces.Cache
import com.app.base.interfaces.Logger
import com.app.base.others.READ_POSTS_FROM_REMOTE_KEY
import com.app.core.network.BaseApiResponse
import com.zemoga.posts.domain.data.Post
import com.zemoga.posts.domain.data.PostsDatabase
import com.zemoga.posts.domain.services.PostsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Represents the repository for posts.
 *
 */
@ExperimentalPagingApi
class PostsRepository @Inject constructor(
    private val logger: Logger,
    private val cache: Cache,
    private val remoteDataSource: PostsRemoteDataSource,
    private val postsDatabase: PostsDatabase,
) : BaseApiResponse(), IPostsRepository {

    override fun getPosts(pagingConfig: PagingConfig): Flow<PagingData<Post>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { postsDatabase.getPostsDao().getAllPosts() },
            remoteMediator = PostsMediator(logger, cache, remoteDataSource, postsDatabase),
        ).flow
    }

    override fun deletePost(postId: Int): Flow<Boolean> {
        return flow {
            val updatedRows = postsDatabase.getPostsDao().deletePost(postId)
            emit(updatedRows > 0)
        }
    }

    override fun deleteNonFavoritePosts(): Flow<Boolean> {
        return flow {
            cache.saveBoolean(READ_POSTS_FROM_REMOTE_KEY, false)

            val updatedRows = postsDatabase.getPostsDao().deleteNonFavoritePosts()
            emit(updatedRows >= 0)
        }
    }

    override fun updateFavoritePost(postId: Int, favorite: Boolean): Flow<Boolean> {
        return flow {
            val updatedRows = postsDatabase.getPostsDao().updateFavoritePost(postId, favorite)
            emit(updatedRows > 0)
        }
    }
}