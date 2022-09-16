package com.zemoga.posts

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import com.app.base.None
import com.app.base.interfaces.Logger
import com.zemoga.authors.view.uimodel.AuthorUiModel
import com.zemoga.comments.view.uimodel.CommentUiModel
import com.zemoga.components.utils.viewBinding
import com.zemoga.posts.databinding.FragmentPostDetailsBinding
import com.zemoga.posts.domain.data.PostsState
import com.zemoga.posts.view.uimodel.PostUiModel
import com.zemoga.posts.viewmodels.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the detailed information of a given post.
 *
 */
@AndroidEntryPoint
@ExperimentalPagingApi
class PostDetailsFragment : DialogFragment(R.layout.fragment_post_details), MenuProvider {

    @Inject
    lateinit var logger: Logger

    private val postsViewModel by viewModels<PostsViewModel>()
    private val binding by viewBinding(FragmentPostDetailsBinding::bind)

    private val postUiModel: PostUiModel by lazy {
        PostDetailsFragmentArgs.fromBundle(requireArguments()).postUiModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView()
        initializeNewsSubscription()
    }

    private fun initializeView() {
        setUpPostView(postUiModel)
        setUpAuthorView(postUiModel.authorUiModel)
        setUpCommentsView(postUiModel.commentsUiModel)

        initializeToolbar()
    }

    private fun setUpPostView(postUiModel: PostUiModel) {
        binding.contentPostDetails.textViewPostTitle.text = postUiModel.title
        binding.contentPostDetails.textViewPostDescription.text = postUiModel.body
    }

    private fun setUpAuthorView(authorUiModel: AuthorUiModel) {
        binding.contentAuthorDetails.textViewAuthorName.text = authorUiModel.name
        binding.contentAuthorDetails.textViewAuthorUsername.text = authorUiModel.username
        binding.contentAuthorDetails.textViewAuthorEmail.text = authorUiModel.email
        binding.contentAuthorDetails.textViewAuthorWebsite.text = authorUiModel.website
        binding.contentAuthorDetails.textViewAuthorPhone.text = authorUiModel.phone

        val addressShort = listOf(
            authorUiModel.address.street, authorUiModel.address.suite,
            authorUiModel.address.city, authorUiModel.address.zipcode,
            authorUiModel.address.geolocation.latitude, authorUiModel.address.geolocation.longitude
        )

        val companyShort = listOf(
            authorUiModel.company.name, authorUiModel.company.catchPhrase,
            authorUiModel.company.bs
        )

        binding.contentAuthorDetails.textViewFullAuthorDescription.setTextMaxLength(10)

        binding.contentAuthorDetails.textViewFullAuthorDescription.setContent(
            addressShort.joinToString(",\n")
                .plus("\n")
                .plus(companyShort.joinToString(",\n"))
        )
    }

    private fun setUpCommentsView(commentsUiModel: List<CommentUiModel>) {
        val commentsBodyList: List<String> = commentsUiModel.map { it.body }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, commentsBodyList)

        binding.contentCommentsDetails.listViewComments.adapter = adapter
    }

    private fun initializeToolbar() {
        val menuHost: MenuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.toolbarPostDetails.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(this.toolbar)

            styleToolbarWithLightTheme()

            setTitle(getString(R.string.post_id, postUiModel.id))

            setToolbarMenu(R.menu.menu_post_details_toolbar)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_delete -> {
                        onActionDeletePressed()
                        true
                    }

                    else -> false
                }
            }

            setBackButton(true)
            setBackButtonListener { popFragment() }
        }
    }

    private fun popFragment() = requireActivity().onBackPressed()

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)

        val favoriteMenuItem = menu.findItem(R.id.action_favorite)
        val favoriteActionView = favoriteMenuItem.actionView.findViewById<ToggleButton>(R.id.toggle_favorite_dark)

        setUpOnFavoriteToggleState(favoriteActionView)
        setUpOnFavoriteCheckedChangeListener(favoriteActionView)
    }

    private fun setUpOnFavoriteToggleState(favoriteActionView: ToggleButton) {
        favoriteActionView.isChecked = postUiModel.favorite
    }

    private fun setUpOnFavoriteCheckedChangeListener(favoriteActionView: ToggleButton) {
        favoriteActionView.setOnCheckedChangeListener { _, checked ->
            onActionFavoritePressed(checked)
        }
    }

    private fun onActionDeletePressed() {
        postsViewModel.deletePost(postUiModel.id)
    }

    private fun onActionFavoritePressed(favorite: Boolean) {
        postsViewModel.updateFavoritePost(postUiModel.id, favorite)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
        menuInflater.inflate(R.menu.menu_post_details_toolbar, menu)

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = true

    private fun initializeNewsSubscription() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                postsViewModel.newsSharedFlow.collectLatest { postsStateNews ->
                    handleNews(postsStateNews)
                }
            }
        }
    }

    private fun handleNews(news: PostsState) {
        when (news) {
            is PostsState.PostDeletedSuccessfully -> popFragment()
            is PostsState.ErrorState -> {
                Toast.makeText(requireContext(), news.errorMessage, Toast.LENGTH_SHORT).show()
                logger.e(news.errorMessage)
            }
            else -> None
        }
    }
}