package com.github.sikv.photos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.github.sikv.photos.R
import com.github.sikv.photos.model.Photo
import com.google.android.material.composethemeadapter.MdcTheme
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage

class PhotoDetailsFragment : BaseFragment() {

    companion object {
        fun newInstance(photo: Photo): PhotoDetailsFragment = PhotoDetailsFragment()
            .apply {
                arguments = bundleOf(
                    Photo.KEY to photo
                )
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        val photo = arguments?.getParcelable<Photo>(Photo.KEY)
        val photoUrl: String? = photo?.getPhotoFullPreviewUrl()

        setContent {
            MdcTheme {
                Surface {
                    Box {
                        if (photoUrl != null) {
                            NetworkImage(imageUrl = photoUrl)
                        }
                        TransparentTopAppBar {
                            navigation?.backPressed()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkImage(imageUrl: String) {
    GlideImage(
        modifier = Modifier.fillMaxSize(),
        imageModel = imageUrl,
        contentScale = ContentScale.Fit,
        circularReveal = CircularReveal(duration = 1000),
    )
}

@Composable
fun TransparentTopAppBar(onBackClicked: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(
                modifier = Modifier
                    .background(
                        MaterialTheme.colors.surface.copy(alpha = 0.5F),
                        shape = CircleShape
                    ),
                onClick = onBackClicked
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back_24dp),
                    contentDescription = stringResource(id = R.string.cd_back_button)
                )
            }
        },
        elevation = 0.dp,
        backgroundColor = Color.Transparent
    )
}
