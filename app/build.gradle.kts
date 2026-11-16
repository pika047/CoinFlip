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
        // The release workflow passes a new code for every upload, since Play rejects repeats.
        versionCode = System.getenv("FLIPCOIN_VERSION_CODE")?.toInt() ?: 1
        versionName = "1.0.0"
    }

    // The upload key is supplied by the release workflow from GitHub secrets and is never
    // committed. Without it, release builds are signed with the debug key so CI can still
    // install and test the minified build; Play rejects debug-signed bundles.
    val uploadKeystore = System.getenv("FLIPCOIN_KEYSTORE_PATH")
    signingConfigs {
        if (uploadKeystore != null) {
            create("upload") {
                storeFile = file(uploadKeystore)
                storePassword = System.getenv("FLIPCOIN_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("FLIPCOIN_KEY_ALIAS")
                keyPassword = System.getenv("FLIPCOIN_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName(if (uploadKeystore != null) "upload" else "debug")
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
    implementation(projects.core.data)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.flip)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
