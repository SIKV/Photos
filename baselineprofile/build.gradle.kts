plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.github.sikv.photos.baselineprofile"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    flavorDimensions += "version"

    productFlavors {
        create("dev") {
            dimension = "version"
        }
        create("prod") {
            dimension = "version"
        }
    }

    targetProjectPath = ":app"
}

dependencies {
    implementation(libs.benchmark.macro.junit4)
}
