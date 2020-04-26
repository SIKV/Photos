package com.github.sikv.photos.model

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

data class FragmentInfo(
        val fragment: Fragment,
        @IdRes val itemId: Int
)