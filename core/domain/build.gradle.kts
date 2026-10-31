plugins {
    alias(libs.plugins.flipcoin.jvm.library)
}

dependencies {
    api(projects.core.model)
    implementation(libs.javax.inject)
}
