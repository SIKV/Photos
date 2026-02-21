plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.github.sikv.photo.usecase"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
    implementation(project(":common-ui"))
    implementation(project(":data"))
    implementation(project(":navigation"))

    implementation(libs.inject)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime)
}
