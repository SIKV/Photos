plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.github.sikv.photos.compose.ui"

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common-ui"))

    implementation(libs.androidx.compose.material3)
}
