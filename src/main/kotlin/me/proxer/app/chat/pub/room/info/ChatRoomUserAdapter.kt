package me.proxer.app.chat.pub.room.info

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gojuno.koptional.rxjava2.filterSome
import com.gojuno.koptional.toOptional
import com.jakewharton.rxbinding3.view.clicks
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.paddingDp
import com.mikepenz.iconics.sizeDp
import com.mikepenz.iconics.typeface.library.communitymaterial.CommunityMaterial
import com.uber.autodispose.autoDisposable
import io.reactivex.subjects.PublishSubject
import kotterknife.bindView
import linkClicks
import me.proxer.app.GlideRequests
import me.proxer.app.R
import me.proxer.app.base.AutoDisposeViewHolder
import me.proxer.app.base.BaseAdapter
import me.proxer.app.chat.pub.room.info.ChatRoomUserAdapter.ViewHolder
import me.proxer.app.util.Utils
import me.proxer.app.util.extension.colorAttr
import me.proxer.app.util.extension.linkify
import me.proxer.app.util.extension.logErrors
import me.proxer.app.util.extension.mapAdapterPosition
import me.proxer.app.util.extension.setIconicsImage
import me.proxer.library.entity.chat.ChatRoomUser
import me.proxer.library.util.ProxerUrls
import okhttp3.HttpUrl

/**
 * @author Ruben Gees
 */
class ChatRoomUserAdapter : BaseAdapter<ChatRoomUser, ViewHolder>() {

    var glide: GlideRequests? = null
    val participantClickSubject: PublishSubject<Pair<ImageView, ChatRoomUser>> = PublishSubject.create()
    val statusLinkClickSubject: PublishSubject<HttpUrl> = PublishSubject.create()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room_participant, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun onViewRecycled(holder: ViewHolder) {
        glide?.clear(holder.image)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        glide = null
    }

    inner class ViewHolder(itemView: View) : AutoDisposeViewHolder(itemView) {

        internal val image: ImageView by bindView(R.id.image)
        internal val username: TextView by bindView(R.id.username)
        internal val status: TextView by bindView(R.id.status)

        fun bind(item: ChatRoomUser) {
            itemView.clicks()
                .mapAdapterPosition({ adapterPosition }) { image to data[it] }
                .autoDisposable(this)
                .subscribe(participantClickSubject)

            status.linkClicks()
                .map { Utils.parseAndFixUrl(it).toOptional() }
                .filterSome()
                .autoDisposable(this)
                .subscribe(statusLinkClickSubject)

            ViewCompat.setTransitionName(image, "chat_room_user_${item.id}")

            username.text = item.name

            if (item.isModerator) {
                username.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, generateModeratorDrawable(username.context), null
                )
            } else {
                username.setCompoundDrawables(null, null, null, null)
            }

            if (item.status.isBlank()) {
                status.isGone = true
            } else {
                status.isVisible = true
                status.text = item.status.trim().linkify(mentions = false)
            }

            if (item.image.isBlank()) {
                image.setIconicsImage(CommunityMaterial.Icon.cmd_account, 96, 16, R.attr.colorSecondary)
            } else {
                glide?.load(ProxerUrls.userImage(item.image).toString())
                    ?.transition(DrawableTransitionOptions.withCrossFade())
                    ?.circleCrop()
                    ?.logErrors()
                    ?.into(image)
            }
        }

        private fun generateModeratorDrawable(context: Context) = IconicsDrawable(context)
            .icon(CommunityMaterial.Icon2.cmd_star)
            .sizeDp(32)
            .paddingDp(8)
            .colorAttr(username.context, R.attr.colorSecondary)
    }
}
