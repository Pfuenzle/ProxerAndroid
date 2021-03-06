package me.proxer.app.info.translatorgroup

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.gojuno.koptional.rxjava2.filterSome
import com.gojuno.koptional.toOptional
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import kotterknife.bindView
import linkClicks
import linkLongClicks
import me.proxer.app.R
import me.proxer.app.base.BaseContentFragment
import me.proxer.app.util.Utils
import me.proxer.app.util.extension.linkify
import me.proxer.app.util.extension.toAppDrawable
import me.proxer.app.util.extension.toast
import me.proxer.library.entity.info.TranslatorGroup
import me.proxer.library.enums.Country
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * @author Ruben Gees
 */
class TranslatorGroupInfoFragment : BaseContentFragment<TranslatorGroup>(R.layout.fragment_translator_group) {

    companion object {
        fun newInstance() = TranslatorGroupInfoFragment().apply {
            arguments = bundleOf()
        }
    }

    override val viewModel by viewModel<TranslatorGroupInfoViewModel> { parametersOf(id) }

    override val hostingActivity: TranslatorGroupActivity
        get() = activity as TranslatorGroupActivity

    private val id: String
        get() = hostingActivity.id

    private var name: String?
        get() = hostingActivity.name
        set(value) {
            hostingActivity.name = value
        }

    private val languageRow: ViewGroup by bindView(R.id.languageRow)
    private val language: ImageView by bindView(R.id.language)
    private val linkRow: ViewGroup by bindView(R.id.linkRow)
    private val link: TextView by bindView(R.id.link)
    private val descriptionContainer: ViewGroup by bindView(R.id.descriptionContainer)
    private val description: TextView by bindView(R.id.description)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        link.linkClicks()
            .map { Utils.parseAndFixUrl(it).toOptional() }
            .filterSome()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe { showPage(it) }

        link.linkLongClicks()
            .autoDisposable(viewLifecycleOwner.scope())
            .subscribe {
                val title = getString(R.string.clipboard_title)

                requireContext().getSystemService<ClipboardManager>()?.primaryClip =
                    ClipData.newPlainText(title, it.toString())

                requireContext().toast(R.string.clipboard_status)
            }
    }

    override fun showData(data: TranslatorGroup) {
        super.showData(data)

        name = data.name

        if (data.country == Country.NONE) {
            languageRow.isGone = true
        } else {
            languageRow.isVisible = true
            language.setImageDrawable(data.country.toAppDrawable(requireContext()))
        }

        if (data.link?.toString().isNullOrBlank()) {
            linkRow.isGone = true
        } else {
            linkRow.isVisible = true
            link.text = data.link.toString().linkify(mentions = false)
        }

        if (data.description.isBlank()) {
            descriptionContainer.isGone = true
        } else {
            descriptionContainer.isVisible = true
            description.text = data.description
        }
    }
}
