package com.zemoga.posts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.app.base.interfaces.FlowUseCase
import com.app.core.interfaces.AppResources
import com.zemoga.author.domain.data.Author
import com.zemoga.author.domain.data.AuthorsState
import com.zemoga.author.qualifiers.GetAuthors
import com.zemoga.authors.view.uimodel.AddressUiModel
import com.zemoga.authors.view.uimodel.AuthorUiModel
import com.zemoga.authors.view.uimodel.CompanyUiModel
import com.zemoga.authors.view.uimodel.GeolocationUiModel
import com.zemoga.comments.domain.data.Comment
import com.zemoga.comments.domain.data.CommentsState
import com.zemoga.comments.qualifiers.GetComments
import com.zemoga.comments.view.uimodel.CommentUiModel
import com.zemoga.posts.R
import com.zemoga.posts.domain.data.Post
import com.zemoga.posts.domain.data.PostsState
import com.zemoga.posts.qualifiers.DeleteNonFavoritePosts
import com.zemoga.posts.qualifiers.DeletePost
import com.zemoga.posts.qualifiers.GetPosts
import com.zemoga.posts.qualifiers.UpdateFavoritePost
import com.zemoga.posts.view.uimodel.PostUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the ViewModel layer of PostFragment & PostDetailsFragment.
 *
 */
@HiltViewModel
@ExperimentalPagingApi
class PostsViewModel @Inject constructor(
    private val resources: AppResources,
    @GetPosts private val getPostsUseCase: FlowUseCase<Unit, PagingData<Post>>,
    @DeletePost private val deletePostUseCase: FlowUseCase<Int, Boolean>,
    @DeleteNonFavoritePosts private val deleteNonFavoritePostsUseCase: FlowUseCase<Unit, Boolean>,
    @UpdateFavoritePost private val updateFavoritePostUseCase: FlowUseCase<Pair<Int, Boolean>, Boolean>,
    @GetAuthors private val getAuthorsUseCase: FlowUseCase<Unit, AuthorsState>,
    @GetComments private val getCommentsUseCase: FlowUseCase<Unit, CommentsState>,
) : ViewModel() {

    private val _postsPagingStateFlow = MutableStateFlow<PagingData<PostUiModel>>(PagingData.empty())
    val postsPagingStateFlow: StateFlow<PagingData<PostUiModel>> = _postsPagingStateFlow

    private val _newsSharedFlow = MutableSharedFlow<PostsState>()
    val newsSharedFlow: SharedFlow<PostsState> = _newsSharedFlow

    fun onViewActive() {
        loadPosts()
    }

    fun deletePost(postId: Int) = viewModelScope.launch {
        deletePostUseCase.execute(postId)
            .flowOn(Dispatchers.IO)
            .collect { deleted ->
                if (deleted) {
                    _newsSharedFlow.emit(PostsState.PostDeletedSuccessfully(postId))
                } else {
                    _newsSharedFlow.emit(PostsState.ErrorState(resources.getString(R.string.error_occurred_deleting_post)))
                }
            }
    }

    fun deleteNonFavoritePosts() = viewModelScope.launch {
        deleteNonFavoritePostsUseCase.execute(Unit)
            .flowOn(Dispatchers.IO)
            .collect { deleted ->
                if (deleted) {
                    _newsSharedFlow.emit(PostsState.NonFavoritePostsDeletedSuccessfully)
                } else {
                    _newsSharedFlow.emit(PostsState.ErrorState(resources.getString(R.string.error_occurred_deleting_non_favorite_posts)))
                }
            }
    }

    fun updateFavoritePost(postId: Int, favorite: Boolean) = viewModelScope.launch {
        updateFavoritePostUseCase.execute(postId to favorite)
            .flowOn(Dispatchers.IO)
            .collect { updated ->
                if (updated) {
                    _newsSharedFlow.emit(PostsState.PostUpdatedSuccessfully(postId))
                } else {
                    _newsSharedFlow.emit(PostsState.ErrorState(resources.getString(R.string.error_occurred_updating_post)))
                }
            }
    }

    private fun loadPosts() = viewModelScope.launch {
        combine(
            getPostsUseCase.execute(Unit).cachedIn(viewModelScope).distinctUntilChanged(),
            getAuthorsUseCase.execute(Unit),
            getCommentsUseCase.execute(Unit)
        ) { postPagingData: PagingData<Post>, authorsState: AuthorsState, commentsState: CommentsState ->
            Triple(postPagingData, authorsState, commentsState)
        }
            .flowOn(Dispatchers.IO)
            .collectLatest {
                val (postPagingData, authorsState, commentsState) = it

                if (authorsState is AuthorsState.GettingAuthorsSuccessfully && commentsState is CommentsState.GettingCommentsSuccessfully) {
                    val authors = authorsState.authorsList
                    val comments = commentsState.commentsList

                    _postsPagingStateFlow.value = postPagingData.toUiModel(authors, comments)
                } else {
                    _newsSharedFlow.emit(PostsState.ErrorState(resources.getString(R.string.error_occurred_getting_posts)))
                }
            }
    }

    private fun PagingData<Post>.toUiModel(authors: List<Author>, comments: List<Comment>): PagingData<PostUiModel> = map { post ->
        post.toUiModel(
            authorUiModel = authors.first { author -> author.id == post.userId }.toUiModel(),
            commentsUiModel = comments.filter { comment -> comment.postId == post.id }.toUiModel()
        )
    }

    private fun Post.toUiModel(authorUiModel: AuthorUiModel, commentsUiModel: List<CommentUiModel>): PostUiModel =
        PostUiModel(
            id, userId, title, body, menuVisible = false, favorite = favorite,
            deleted = false, authorUiModel = authorUiModel, commentsUiModel = commentsUiModel,
        )

    private fun Author.toUiModel(): AuthorUiModel = AuthorUiModel(
        id, name, username, email,
        AddressUiModel(
            address.street,
            address.suite,
            address.city,
            address.zipcode,
            GeolocationUiModel(address.geolocation.latitude, address.geolocation.longitude),
        ),
        phone,
        website,
        CompanyUiModel(company.name, company.catchPhrase, company.bs),
    )

    private fun List<Comment>.toUiModel(): List<CommentUiModel> = map { it.toUiModel() }
    private fun Comment.toUiModel(): CommentUiModel = CommentUiModel(postId, id, name, email, body)
}