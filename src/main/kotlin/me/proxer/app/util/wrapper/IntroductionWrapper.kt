package me.proxer.app.util.wrapper

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.colorRes
import com.mikepenz.iconics.paddingDp
import com.mikepenz.iconics.sizeDp
import com.mikepenz.iconics.typeface.library.communitymaterial.CommunityMaterial
import com.rubengees.introduction.IntroductionBuilder
import com.rubengees.introduction.Option
import com.rubengees.introduction.Slide
import com.rubengees.introduction.interfaces.OnSlideListener
import me.proxer.app.R
import me.proxer.app.util.DeviceUtils

/**
 * @author Ruben Gees
 */
object IntroductionWrapper {

    fun introduce(activity: Activity) = IntroductionBuilder(activity)
        .withSlides(generateSlides(activity))
        .withSkipEnabled(R.string.introduction_skip)
        .withOnSlideListener(object : OnSlideListener() {
            override fun onSlideInit(position: Int, title: TextView?, image: ImageView, description: TextView?) {
                val padding = DeviceUtils.getScreenHeight(image.context) / 16

                when (position) {
                    0 -> {
                        image.setPadding(padding)
                        image.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.ic_proxer))
                    }
                    1 -> image.setImageDrawable(
                        IconicsDrawable(image.context, CommunityMaterial.Icon.cmd_bell_outline)
                            .sizeDp(256)
                            .paddingDp(padding / 2)
                            .colorRes(R.color.on_primary)
                    )
                    2 -> image.setImageDrawable(
                        IconicsDrawable(image.context, CommunityMaterial.Icon2.cmd_theme_light_dark)
                            .sizeDp(256)
                            .paddingDp(padding / 2)
                            .colorRes(R.color.on_primary)
                    )
                }
            }
        })
        .introduceMyself()

    private fun generateSlides(context: Context) = arrayListOf(
        Slide().withTitle(R.string.introduction_welcome_title)
            .withColorResource(R.color.primary)
            .withDescription(R.string.introduction_welcome_description),
        Slide().withTitle(R.string.introduction_notifications_title)
            .withColorResource(R.color.primary)
            .withOption(Option(context.getString(R.string.introduction_notifications_description), true)),
        Slide().withTitle(context.getString(R.string.introduction_theme_title))
            .withColorResource(R.color.primary)
            .withOption(Option(context.getString(R.string.introduction_theme_description), false))
    )
}
