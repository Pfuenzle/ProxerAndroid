@file:Suppress("NOTHING_TO_INLINE")

package me.proxer.app.util.extension

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.view.postDelayed
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.colorInt
import com.mikepenz.iconics.paddingDp
import com.mikepenz.iconics.sizeDp
import com.mikepenz.iconics.typeface.IIcon
import me.proxer.app.R
import timber.log.Timber

inline var AppCompatTextView.fastText: CharSequence
    get() = text
    set(value) {
        val textMetrics = TextViewCompat.getTextMetricsParams(this)

        setTextFuture(PrecomputedTextCompat.getTextFuture(value, textMetrics, null))
    }

inline fun ImageView.setIconicsImage(
    icon: IIcon,
    sizeDp: Int,
    paddingDp: Int = sizeDp / 4,
    @AttrRes colorAttr: Int = R.attr.colorIcon
) {
    setImageDrawable(
        IconicsDrawable(context, icon)
            .sizeDp(sizeDp)
            .paddingDp(paddingDp)
            .colorAttr(context, colorAttr)
    )
}

inline fun IconicsDrawable.colorAttr(context: Context, @AttrRes res: Int): IconicsDrawable {
    return this.colorInt(context.resolveColor(res))
}

inline fun IconicsDrawable.backgroundColorAttr(context: Context, @AttrRes res: Int): IconicsDrawable {
    return this.backgroundColor(IconicsColor.colorInt(context.resolveColor(res)))
}

inline fun IconicsDrawable.iconColor(context: Context): IconicsDrawable {
    return this.colorAttr(context, R.attr.colorIcon)
}

inline fun ViewGroup.enableLayoutAnimationsSafely() {
    this.layoutTransition = LayoutTransition().apply { setAnimateParentHierarchy(false) }
}

fun RecyclerView.isAtCompleteTop() = when (val layoutManager = this.safeLayoutManager) {
    is StaggeredGridLayoutManager -> layoutManager.findFirstCompletelyVisibleItemPositions(null).contains(0)
    is LinearLayoutManager -> layoutManager.findFirstCompletelyVisibleItemPosition() == 0
    else -> false
}

fun RecyclerView.isAtTop() = when (val layoutManager = this.safeLayoutManager) {
    is StaggeredGridLayoutManager -> layoutManager.findFirstVisibleItemPositions(null).contains(0)
    is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition() == 0
    else -> false
}

fun RecyclerView.scrollToTop() = when (val layoutManager = safeLayoutManager) {
    is StaggeredGridLayoutManager -> layoutManager.scrollToPositionWithOffset(0, 0)
    is LinearLayoutManager -> layoutManager.scrollToPositionWithOffset(0, 0)
    else -> throw IllegalStateException("Unsupported layout manager: ${layoutManager.javaClass.name}")
}

fun RecyclerView.doAfterAnimations(action: () -> Unit) {
    postDelayed(10) {
        if (isAnimating) {
            val safeItemAnimator = itemAnimator ?: throw IllegalStateException(
                "RecyclerView is reporting isAnimating as true, but no itemAnimator is set"
            )

            safeItemAnimator.isRunning { doAfterAnimations(action) }
        } else {
            action()
        }
    }
}

fun RecyclerView.enableFastScroll() {
    val thumbColor = context.resolveColor(R.attr.colorFastscrollThumb)
    val trackColor = context.resolveColor(R.attr.colorFastscrollTrack)
    val pressedColor = context.resolveColor(R.attr.colorSecondary)

    val thumbDrawableSelector = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(pressedColor))
        addState(intArrayOf(), ColorDrawable(thumbColor))
    }

    val trackDrawable = ColorDrawable(trackColor)

    try {
        val initFastScrollerMethod = RecyclerView::class.java.getDeclaredMethod(
            "initFastScroller",
            StateListDrawable::class.java, Drawable::class.java,
            StateListDrawable::class.java, Drawable::class.java
        ).apply {
            isAccessible = true
        }

        initFastScrollerMethod.invoke(
            this,
            thumbDrawableSelector, trackDrawable,
            thumbDrawableSelector, trackDrawable
        )
    } catch (error: Exception) {
        Timber.e(error, "Could not enable fast scroll")
    }
}
