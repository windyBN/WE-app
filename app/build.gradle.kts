plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.smartcity"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartcity"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.github.bumptech.glide:glide:4.13.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    //implementation ("com.taptap:lc-storage-android:8.2.24")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("cn.leancloud:storage-android:6.5.14")
    implementation ("cn.leancloud:realtime-android:6.5.14")


    implementation ("io.reactivex.rxjava3:rxjava:3.0.11")


    implementation ("com.google.android.material:material:1.1.0")
    implementation ("io.github.youth5201314:banner:2.2.3")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    implementation ("com.squareup.picasso:picasso:2.8")









}