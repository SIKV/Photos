import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.gms.oss.licenses.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.protobuf) apply false
}

fun BaseExtension.baseConfig() {
    compileSdkVersion(34)

    defaultConfig.apply {
        minSdk = 23
        targetSdk = 34
    }

    compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

fun PluginContainer.applyBaseConfig(project: Project) {
    whenPluginAdded {
        when (this) {
            is AppPlugin -> {
                project.extensions.getByType<AppExtension>().apply {
                    baseConfig()
                }
            }
            is LibraryPlugin -> {
                project.extensions.getByType<LibraryExtension>().apply {
                    baseConfig()
                }
            }
        }
    }
}

subprojects {
    project.plugins.applyBaseConfig(project)
}
