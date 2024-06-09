import org.gradle.api.Project

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.gms)
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
        versionCode = 1
        versionName = "1.0"
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
        create("releaseDebuggable") {
            initWith(getByName("release"))
            matchingFallbacks += "release"
            isDebuggable = true
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
    }

    lint {
        checkAllWarnings = true
        warningsAsErrors = true
        abortOnError = true
        baseline = file("lint-baseline.xml")
        disable += "InvalidPackage"
    }
}

kapt {
    generateStubs = false
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
    implementation(project(":photo-list-ui"))
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
    kapt(libs.hilt.compiler)

    // https://stackoverflow.com/a/60492942/7064179
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.3")
}
