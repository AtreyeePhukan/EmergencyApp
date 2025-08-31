import java.util.Properties
import java.io.FileInputStream


plugins {
    alias(libs.plugins.android.application) // Assuming you're using Kotlin DSL for plugin management
}


val localProperties = Properties()
val localFile = rootProject.file("local.properties")
if (localFile.exists()) {
    FileInputStream(localFile).use { localProperties.load(it) }
}
val openRouteKey = localProperties.getProperty("OPENROUTESERVICE_API_KEY") ?: ""


android {
    namespace = "com.example.sosapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sosapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPENROUTESERVICE_API_KEY", "\"$openRouteKey\"")
    }
    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Android dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.maps.android:android-maps-utils:2.3.0")

    // OSMDroid dependencies
    implementation("org.osmdroid:osmdroid-android:6.1.10")
    implementation("org.osmdroid:osmdroid-wms:6.1.10")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.10")

    // OkHttp dependencies
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    implementation(libs.monitor)
    implementation(libs.ext.junit)
    implementation(libs.play.services.location)



    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
}
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    }
}