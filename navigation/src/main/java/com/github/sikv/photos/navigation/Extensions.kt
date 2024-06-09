package com.github.sikv.photos.navigation

import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.github.sikv.photos.navigation.args.FragmentArguments

fun NavController.navigate(@IdRes resId: Int, args: FragmentArguments) {
    navigate(resId, args = bundleOf(FragmentArguments.KEY to args))
}
