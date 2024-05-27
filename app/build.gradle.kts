import org.gradle.api.JavaVersion

plugins {
  kotlin("kapt")
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
}

android {
  namespace = "com.aura"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {

    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }



  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  // TODO : J'ai été obligé de faire celà sinon çà me disait que kotlin utilisait java 17...
  // Configurer la version de la cible de la JVM pour kaptGenerateStubsDebugKotlin
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.13.1")

  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
  implementation("androidx.activity:activity-ktx:1.9.0")

  implementation("androidx.appcompat:appcompat:1.6.1")

  implementation("com.google.android.material:material:1.12.0")

  implementation("androidx.annotation:annotation:1.8.0")

  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

  // Tests
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  // Moshi JSON Library
  implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

  // Retrofit for Network Requests
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.11.0")


  // Dagger Hilt Dependency Injection
  implementation("com.google.dagger:hilt-android:2.51.1")
  implementation("com.google.android.gms:play-services-location:21.2.0")
  kapt("com.google.dagger:hilt-android-compiler:2.51.1")
}