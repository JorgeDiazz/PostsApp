package com.zemoga.posts

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.base.None
import com.app.base.interfaces.Cache
import com.app.base.interfaces.Logger
import com.app.base.others.READ_POSTS_FROM_REMOTE_KEY
import com.google.android.material.snackbar.Snackbar
import com.zemoga.components.utils.viewBinding
import com.zemoga.posts.databinding.FragmentPostsBinding
import com.zemoga.posts.domain.data.PostsState
import com.zemoga.posts.view.LoaderStateAdapter
import com.zemoga.posts.view.PostsAdapter
import com.zemoga.posts.view.uimodel.PostUiModel
import com.zemoga.posts.viewmodels.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the list of posts pulled in from JSON API.
 *
 */
@AndroidEntryPoint
@ExperimentalPagingApi
class PostsFragment : Fragment(R.layout.fragment_posts), PostsAdapter.OnClickListener {

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var cache: Cache

    private val viewModel by viewModels<PostsViewModel>()
    private val binding by viewBinding(FragmentPostsBinding::bind)

    lateinit var postsRecyclerView: RecyclerView
    lateinit var postsAdapter: PostsAdapter
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var lastPostSwipedIndex = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView()
        initializeViewModel()
        initializeObserver()
        initializeNewsSubscription()
    }

    private fun initializeView() {
        setUpRecyclerView()
        setUpDeleteAllPostsButton()
    }

    private fun setUpRecyclerView() {
        postsAdapter = PostsAdapter(resources, this).apply {
            addLoadStateListener { loadState ->
                val mediatorLoadState: LoadState? = loadState.mediator?.refresh

                if (mediatorLoadState is LoadState.NotLoading) {
                    swipeRefreshLayout.isRefreshing = false
                } else if (mediatorLoadState is LoadState.Error) {
                    Toast.makeText(requireContext(), getString(R.string.loading_resources_from_local_database), Toast.LENGTH_SHORT).show()
                    swipeRefreshLayout.isRefreshing = false
                }

            }
        }

        postsRecyclerView = binding.recyclerView
        postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postsAdapter.withLoadStateFooter(LoaderStateAdapter { postsAdapter.retry() })
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        swipeRefreshLayout = binding.swipeRefreshLayout

        swipeRefreshLayout.setOnRefreshListener {
            cache.saveBoolean(READ_POSTS_FROM_REMOTE_KEY, true)

            postsAdapter.refresh()
        }

        setUpRecyclerViewItemTouchHelper()
        setUpRecyclerViewScrollChangeListener()
    }

    private fun setUpRecyclerViewItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private val background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.blue_dark))

            override fun onMove(v: RecyclerView, h: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, dir: Int) {
                postsAdapter.closeMenuAt(lastPostSwipedIndex)

                lastPostSwipedIndex = viewHolder.absoluteAdapterPosition
                postsAdapter.showMenuAt(lastPostSwipedIndex)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView

                if (dX > 0) {
                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                } else if (dX < 0) {
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                } else {
                    background.setBounds(0, 0, 0, 0)
                }

                background.draw(c)
            }
        }).attachToRecyclerView(postsRecyclerView)
    }

    private fun setUpRecyclerViewScrollChangeListener() {
        postsRecyclerView.setOnScrollChangeListener { _, scrollY: Int, _, _, oldScrollY: Int ->
            postsRecyclerView.post {
                val verticalScrollChanged = scrollY != oldScrollY
                if (verticalScrollChanged) {
                    postsAdapter.closeMenuAt(lastPostSwipedIndex)
                }
            }
        }
    }

    private fun setUpDeleteAllPostsButton() {
        binding.fabDeleteAllPosts.setOnClickListener {
            viewModel.deleteNonFavoritePosts()
        }
    }

    private fun initializeViewModel() {
        viewModel.onViewActive()
    }

    private fun initializeObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postsPagingStateFlow.collect { postsPagingData ->
                    observePostsPagingData(postsPagingData)
                }
            }
        }
    }

    private fun observePostsPagingData(postsPagingData: PagingData<PostUiModel>) {
        lifecycleScope.launchWhenStarted {
            postsAdapter.submitData(postsPagingData)
        }
    }

    private fun initializeNewsSubscription() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsSharedFlow.collectLatest { postsStateNews ->
                    handleNews(postsStateNews)
                }
            }
        }
    }

    private fun handleNews(news: PostsState) {
        when (news) {
            is PostsState.PostUpdatedSuccessfully -> logger.d("Post ${news.postId} was updated successfully")
            is PostsState.PostDeletedSuccessfully -> postsAdapter.deletePost(news.postId)
            is PostsState.NonFavoritePostsDeletedSuccessfully -> Toast.makeText(requireContext(), getString(R.string.non_favorite_posts_deleted), Toast.LENGTH_SHORT).show()
            is PostsState.ErrorState -> {
                Snackbar.make(requireView(), news.errorMessage, Snackbar.LENGTH_SHORT).show()
                logger.e(news.errorMessage)
            }
            else -> None
        }
    }

    override fun onPostClick(postUiModel: PostUiModel) {
        navigateToPostDetailsFragment(postUiModel)
    }

    private fun navigateToPostDetailsFragment(postUiModel: PostUiModel) {
        val action = PostsFragmentDirections.actionPostsFragmentToPostDetailsFragment(postUiModel)
        findNavController().navigate(action)
    }

    override fun onToggleFavoriteClick(postUiModel: PostUiModel, favorite: Boolean) {
        viewModel.updateFavoritePost(postUiModel.id, favorite)
    }

    override fun onDeleteClick(postUiModel: PostUiModel) {
        viewModel.deletePost(postUiModel.id)
    }
}