package com.proxerme.app.activity.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.proxerme.app.R
import com.proxerme.app.activity.MainActivity
import com.proxerme.app.activity.ProfileActivity
import com.proxerme.app.entitiy.LocalConference
import com.proxerme.app.fragment.chat.ChatFragment
import com.proxerme.app.util.bindView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity

class ChatActivity : MainActivity() {

    companion object {
        private const val EXTRA_CONFERENCE = "extra_conference"

        fun navigateTo(context: Activity, conference: LocalConference) {
            context.startActivity<ChatActivity>(EXTRA_CONFERENCE to conference)
        }

        fun getIntent(context: Context, conference: LocalConference): Intent {
            return context.intentFor<ChatActivity>(EXTRA_CONFERENCE to conference)
        }
    }

    val conference: LocalConference
        get() = intent.getParcelableExtra(EXTRA_CONFERENCE)

    private val toolbar: Toolbar by bindView(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_default)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = conference.topic

        toolbar.setOnClickListener {
            if (conference.isGroup) {
                ConferenceInfoActivity.navigateTo(this, conference)
            } else {
                ProfileActivity.navigateTo(this, null, conference.topic, conference.imageId)
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .setAllowOptimization(true)
                    .replace(R.id.container, ChatFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
