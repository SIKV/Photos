package com.github.sikv.photos.ui.compose

import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi

// https://proandroiddev.com/how-to-animate-bottomsheet-content-using-jetpack-compose-3eab972b3bdc
@OptIn(ExperimentalMaterialApi::class)
val BottomSheetState.currentFraction: Float
    get() {
        val fraction = progress.fraction
        val targetValue = targetValue
        val currentValue = currentValue

        return when {
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed -> 0f
            currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded -> 1f
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Expanded -> fraction
            else -> 1f - fraction
        }
    }
