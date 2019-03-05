package me.proxer.app.profile.media

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gojuno.koptional.toOptional
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import me.proxer.app.GlideApp
import me.proxer.app.R
import me.proxer.app.base.PagedContentFragment
import me.proxer.app.media.MediaActivity
import me.proxer.app.profile.ProfileActivity
import me.proxer.app.util.DeviceUtils
import me.proxer.app.util.extension.toCategory
import me.proxer.app.util.extension.unsafeLazy
import me.proxer.library.entity.user.UserMediaListEntry
import me.proxer.library.enums.Category
import me.proxer.library.enums.UserMediaListFilterType
import org.koin.androidx.viewmodel.ext.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.properties.Delegates

/**
 * @author Ruben Gees
 */
class ProfileMediaListFragment : PagedContentFragment<UserMediaListEntry>() {

    companion object {
        private const val CATEGORY_ARGUMENT = "category"
        private const val FILTER_ARGUMENT = "filter"

        fun newInstance(category: Category) = ProfileMediaListFragment().apply {
            arguments = bundleOf(CATEGORY_ARGUMENT to category)
        }
    }

    override val emptyDataMessage = R.string.error_no_data_user_media_list
    override val isSwipeToRefreshEnabled = false

    override val viewModel by viewModel<ProfileMediaListViewModel> {
        parametersOf(userId.toOptional(), username.toOptional(), category, filter.toOptional())
    }

    override val layoutManager by unsafeLazy {
        StaggeredGridLayoutManager(
            DeviceUtils.calculateSpanAmount(requireActivity()) + 1,
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    override val hostingActivity: ProfileActivity
        get() = activity as ProfileActivity

    private val userId: String?
        get() = hostingActivity.userId

    private val username: String?
        get() = hostingActivity.username

    private val category: Category
        get() = requireArguments().getSerializable(CATEGORY_ARGUMENT) as Category

    private var filter: UserMediaListFilterType?
        get() = requireArguments().getSerializable(FILTER_ARGUMENT) as? UserMediaListFilterType
        set(value) {
            requireArguments().putSerializable(FILTER_ARGUMENT, value)

            viewModel.filter = value
        }

    override var innerAdapter by Delegates.notNull<ProfileMediaAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        innerAdapter = ProfileMediaAdapter()

        innerAdapter.clickSubject
            .autoDisposable(this.scope())
            .subscribe { (view, item) ->
                MediaActivity.navigateTo(requireActivity(), item.id, item.name, item.medium.toCategory(), view)
            }

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        innerAdapter.glide = GlideApp.with(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuResource = when (category) {
            Category.ANIME -> R.menu.fragment_user_media_list_anime
            Category.MANGA -> R.menu.fragment_user_media_list_manga
        }

        IconicsMenuInflaterUtil.inflate(inflater, requireContext(), menuResource, menu, true)

        when (filter) {
            UserMediaListFilterType.WATCHING -> menu.findItem(R.id.watching).isChecked = true
            UserMediaListFilterType.WATCHED -> menu.findItem(R.id.watched).isChecked = true
            UserMediaListFilterType.WILL_WATCH -> menu.findItem(R.id.will_watch).isChecked = true
            UserMediaListFilterType.CANCELLED -> menu.findItem(R.id.cancelled).isChecked = true
            null -> menu.findItem(R.id.all).isChecked = true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.watching -> filter = UserMediaListFilterType.WATCHING
            R.id.watched -> filter = UserMediaListFilterType.WATCHED
            R.id.will_watch -> filter = UserMediaListFilterType.WILL_WATCH
            R.id.cancelled -> filter = UserMediaListFilterType.CANCELLED
            R.id.all -> filter = null
        }

        item.isChecked = true

        return true
    }
}
