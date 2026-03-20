plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.gms)
    alias(libs.plugins.androidx.baselineprofile)
    id("com.google.android.gms.oss-licenses-plugin")
}

fun Project.extraOrNull(propertyName: String): String? {
    return if (hasProperty(propertyName)) property(propertyName).toString() else null
}

val releaseKeyAlias: String? = extraOrNull("releaseKeyAlias")
val releaseKeyPassword: String? = extraOrNull("releaseKeyPassword")
val releaseStoreFile: String? = extraOrNull("releaseStoreFile")
val releaseStorePassword: String? = extraOrNull("releaseStorePassword")

android {
    namespace = "com.github.sikv.photos"

    defaultConfig {
        applicationId = "com.github.sikv.photos"
        versionCode = 3
        versionName = "1.1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
            storeFile = releaseStoreFile?.let { file(it) }
            storePassword = releaseStorePassword
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), file("proguard-rules.pro"))
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), file("proguard-rules.pro"))
            ndk {
                debugSymbolLevel = "FULL"
            }
            signingConfig = signingConfigs.getByName("release")
        }
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += "release"
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("dev") {
            dimension = "version"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("prod") {
            dimension = "version"
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    lint {
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = true
        disable += "InvalidPackage"
        disable += "GradleDependency"
        disable += "AndroidGradlePluginVersion"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":api"))
    implementation(project(":domain"))
    implementation(project(":config"))
    implementation(project(":data"))
    implementation(project(":common"))
    implementation(project(":common-ui"))
    implementation(project(":navigation"))
    implementation(project(":theme-manager"))
    implementation(project(":feature:curated-photos"))
    implementation(project(":feature:photo-details"))
    implementation(project(":feature:wallpaper"))
    implementation(project(":feature:search"))
    implementation(project(":feature:favorites"))
    implementation(project(":feature:recommendations"))
    implementation(project(":feature:preferences"))
    implementation(project(":feature:feedback"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.viewpager2)

    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.profileinstaller)
    baselineProfile(project(":baselineprofile"))
}
