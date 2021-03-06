package me.proxer.app.ucp

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper
import kotterknife.bindView
import me.proxer.app.R
import me.proxer.app.base.DrawerActivity
import me.proxer.app.ucp.history.UcpHistoryFragment
import me.proxer.app.ucp.media.UcpMediaListFragment
import me.proxer.app.ucp.overview.UcpOverviewFragment
import me.proxer.app.ucp.topten.UcpTopTenFragment
import me.proxer.app.util.extension.startActivity
import me.proxer.app.util.extension.unsafeLazy
import me.proxer.library.enums.Category

/**
 * @author Ruben Gees
 */
class UcpActivity : DrawerActivity() {

    companion object {
        fun navigateTo(context: Activity) = context.startActivity<UcpActivity>()
    }

    override val contentView: Int
        get() = R.layout.activity_ucp

    private val sectionsPagerAdapter by unsafeLazy { SectionsPagerAdapter(supportFragmentManager) }

    private val viewPager: ViewPager by bindView(R.id.viewPager)
    private val tabs: TabLayout by bindView(R.id.tabs)

    private var tabLayoutHelper: TabLayoutHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()

        viewPager.adapter = sectionsPagerAdapter

        tabLayoutHelper = TabLayoutHelper(tabs, viewPager).apply { isAutoAdjustTabModeEnabled = true }
    }

    override fun onDestroy() {
        tabLayoutHelper?.release()
        tabLayoutHelper = null
        viewPager.adapter = null

        super.onDestroy()
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.section_ucp)
    }

    inner class SectionsPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(
        fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        override fun getItem(position: Int) = when (position) {
            0 -> UcpOverviewFragment.newInstance()
            1 -> UcpTopTenFragment.newInstance()
            2 -> UcpMediaListFragment.newInstance(Category.ANIME)
            3 -> UcpMediaListFragment.newInstance(Category.MANGA)
            4 -> UcpHistoryFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown index passed: $position")
        }

        override fun getCount() = 5

        override fun getPageTitle(position: Int): String = when (position) {
            0 -> getString(R.string.section_ucp_overview)
            1 -> getString(R.string.section_ucp_top_ten)
            2 -> getString(R.string.section_user_media_list_anime)
            3 -> getString(R.string.section_user_media_list_manga)
            4 -> getString(R.string.section_ucp_history)
            else -> throw IllegalArgumentException("Unknown index passed: $position")
        }
    }
}
