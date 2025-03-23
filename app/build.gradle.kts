plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.visionary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.visionary"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }


    buildFeatures{
        viewBinding=true
    }

    buildFeatures{
        dataBinding=true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //// sdp dependencies
    implementation (libs.sdp.android)
    //glide
    implementation (libs.glide)

    /// curve bottom navigation
    implementation (libs.curvedbottomnavigation)

    implementation (libs.exoplayer)
    implementation ("com.google.android.exoplayer:exoplayer-dash:2.19.1")

    ///double click listner
    
    implementation ("com.github.vkay94:DoubleTapPlayerView:1.0.4")
    implementation ("im.zego:express-audio:3.12.4")
    implementation ("de.hdodenhof:circleimageview:3.1.0")


    // Room Database
    implementation ("androidx.room:room-runtime:2.5.2")
    implementation ("androidx.room:room-ktx:2.5.2")
///rozor pay
    implementation ("com.razorpay:checkout:1.6.40")

}