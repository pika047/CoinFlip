plugins {
    alias(libs.plugins.flipcoin.android.application)
    alias(libs.plugins.flipcoin.android.compose)
    alias(libs.plugins.flipcoin.hilt)
}

android {
    namespace = "com.flipcoin"

    defaultConfig {
        // Permanent once published on Google Play.
        applicationId = "io.github.pika047.flipcoin"
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
