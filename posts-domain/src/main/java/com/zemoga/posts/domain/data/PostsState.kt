package com.zemoga.posts.domain.data

sealed class PostsState {

    data class PostUpdatedSuccessfully(val postId: Int) : PostsState()

    data class PostDeletedSuccessfully(val postId: Int) : PostsState()

    object NonFavoritePostsDeletedSuccessfully : PostsState()

    data class ErrorState(val errorMessage: String) : PostsState()

}
