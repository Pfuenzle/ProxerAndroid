@file:Suppress("NOTHING_TO_INLINE")

package me.proxer.app.util.extension

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

inline val RecyclerView.safeLayoutManager: RecyclerView.LayoutManager
    get() = layoutManager ?: throw IllegalStateException("layoutManager is null")

inline val EditText.safeText: Editable
    get() = text ?: throw IllegalStateException("text is null")

inline fun Intent.getSafeStringExtra(key: String) = getStringExtra(key)
    ?: throw IllegalAccessError("No value found for key $key")

inline fun Intent.getSafeStringArrayExtra(key: String): Array<out String> = getStringArrayExtra(key)
    ?: throw IllegalAccessError("No value found for key $key")

inline fun <reified T : Parcelable> Intent.getSafeParcelableExtra(key: String) = getParcelableExtra<T>(key)
    ?: throw IllegalAccessError("No value found for key $key")

inline fun <T : Parcelable> Bundle.getSafeParcelable(key: String) = getParcelable<T>(key)
    ?: throw IllegalAccessError("No value found for key $key")

inline fun Bundle.getSafeCharSequence(key: String) = getCharSequence(key)
    ?: throw IllegalAccessError("No value found for key $key")

inline fun Bundle.getSafeString(key: String) = getString(key)
    ?: throw IllegalAccessError("No value found for key $key")

inline fun Parcel.readStringSafely() = readString()
    ?: throw IllegalAccessError("No value available at this position")

inline fun Parcel.readSerializableSafely() = readSerializable()
    ?: throw IllegalAccessError("No value available at this position")

inline fun SharedPreferences.getSafeString(key: String, default: String? = null) = getString(key, default)
    ?: throw IllegalAccessError("No value found for key $key")
