plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.example.tiendavirtualapp_kotlin2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tiendavirtualapp_kotlin2"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lottie)/*Animaciones*/
    implementation(libs.firebaseAuth)/*Autenticacion con Firebase*/
    implementation(libs.firebaseDatabase)/*Firebase*/
    implementation(libs.imagePicker)/*Recortarimagenes*/
    implementation(libs.glide)/*Leer imagenes*/
    implementation(libs.storage)/*Subir archivo multimedia*/
    implementation(libs.authGoogle)/*Iniciar sesion con google */
    implementation(libs.ccp)/*Seleccionar codigo telefonico*/
    implementation(libs.photoView)
    implementation(libs.circleImage)
    implementation(libs.maps)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}