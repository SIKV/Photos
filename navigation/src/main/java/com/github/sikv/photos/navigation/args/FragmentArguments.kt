package com.github.sikv.photos.navigation.args

import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle

interface FragmentArguments : Parcelable {
    companion object {
        const val KEY = "FragmentArguments"
    }
}

fun <T: Fragment> T.withArguments(
    args: FragmentArguments,
    key: String = FragmentArguments.KEY
): T {
    return this.apply {
        arguments = bundleOf(key to args)
    }
}

inline fun<reified T: FragmentArguments> Fragment.fragmentArguments(
    key: String = FragmentArguments.KEY
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        requireArguments().get(key) as T
    }
}

fun<T:FragmentArguments> SavedStateHandle.fragmentArguments(
    key: String = FragmentArguments.KEY
): T {
    return requireNotNull(get<T>(key))
}
