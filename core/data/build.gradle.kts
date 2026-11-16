plugins {
    alias(libs.plugins.flipcoin.android.library)
    alias(libs.plugins.flipcoin.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.flipcoin.core.data"
}

dependencies {
    api(projects.core.domain)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.kotlinx.coroutines.test)
}
