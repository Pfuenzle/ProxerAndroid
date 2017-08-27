package me.proxer.app.chat.conference

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.*
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.disposables.Disposable
import kotterknife.bindView
import me.proxer.app.GlideApp
import me.proxer.app.MainApplication.Companion.bus
import me.proxer.app.R
import me.proxer.app.base.BaseContentFragment
import me.proxer.app.chat.ChatActivity
import me.proxer.app.chat.LocalConference
import me.proxer.app.chat.new.NewChatActivity
import me.proxer.app.chat.sync.ChatNotifications
import me.proxer.app.util.DeviceUtils
import me.proxer.app.util.ErrorUtils.ErrorAction
import me.proxer.app.util.extension.unsafeLazy
import org.jetbrains.anko.bundleOf

/**
 * @author Ruben Gees
 */
class ConferenceFragment : BaseContentFragment<List<LocalConference>>() {

    companion object {
        fun newInstance() = ConferenceFragment().apply {
            arguments = bundleOf()
        }
    }

    override val viewModel: ConferenceViewModel by unsafeLazy {
        ViewModelProviders.of(this).get(ConferenceViewModel::class.java)
    }

    private lateinit var adapter: ConferenceAdapter

    private var pingDisposable: Disposable? = null

    override val contentContainer: ViewGroup
        get() = recyclerView

    private val recyclerView: RecyclerView by bindView(R.id.recyclerView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ConferenceAdapter()

        adapter.clickSubject
                .bindToLifecycle(this)
                .subscribe { ChatActivity.navigateTo(activity, it) }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_conferences, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.glide = GlideApp.with(this)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(DeviceUtils.calculateSpanAmount(activity),
                StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        ChatNotifications.cancel(context)

        pingDisposable = bus.register(ConferenceFragmentPingEvent::class.java).subscribe()
    }

    override fun onPause() {
        pingDisposable?.dispose()
        pingDisposable = null

        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        IconicsMenuInflaterUtil.inflate(inflater, context, R.menu.fragment_conferences, menu, true)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_chat -> NewChatActivity.navigateTo(activity, false)
            R.id.new_group -> NewChatActivity.navigateTo(activity, true)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showData(data: List<LocalConference>) {
        super.showData(data)

        when {
            data.isEmpty() -> {
                adapter.clearAndNotifyRemoval()

                if (adapter.isEmpty()) {
                    showError(ErrorAction(R.string.error_no_data_conferences, ErrorAction.ACTION_MESSAGE_HIDE))
                }
            }
            else -> {
                adapter.swapDataAndNotifyChange(data)
            }
        }
    }

    override fun hideData() {
        super.hideData()

        adapter.clearAndNotifyRemoval()
    }
}