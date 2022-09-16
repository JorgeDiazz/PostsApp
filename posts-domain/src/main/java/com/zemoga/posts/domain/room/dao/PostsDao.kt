package com.zemoga.posts.domain.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zemoga.posts.domain.data.Post

/**
 * Represents Room dao for posts entities.
 *
 */
@Dao
interface PostsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(posts: List<Post>)

    @Query("SELECT * FROM post ORDER BY favorite DESC")
    fun getAllPosts(): PagingSource<Int, Post>

    @Query("UPDATE post SET favorite = :favorite WHERE id = :id")
    suspend fun updateFavoritePost(id: Int, favorite: Boolean): Int

    @Query("DELETE FROM post WHERE id = :id")
    suspend fun deletePost(id: Int): Int

    @Query("DELETE FROM post WHERE favorite = 0")
    suspend fun deleteNonFavoritePosts(): Int

    @Query("DELETE FROM post")
    suspend fun clearAllPosts()
}