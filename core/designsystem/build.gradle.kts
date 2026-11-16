plugins {
    alias(libs.plugins.flipcoin.android.library)
    alias(libs.plugins.flipcoin.android.compose)
}

android {
    namespace = "com.flipcoin.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
}
