plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
  id("androidx.navigation.safeargs")
  id("kotlin-parcelize")
  kotlin("plugin.serialization")
  id("jacoco")
  id("plugins.jacoco-report")
}

android {
  compileSdk = Api.compileSDK
  defaultConfig {
    minSdk = Api.minSDK
    targetSdk = Api.targetSDK
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  lint {
    abortOnError = false
  }

  buildFeatures {
    dataBinding = true
    viewBinding = true
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {

  implementation(project(":components"))
  implementation(project(":core"))
  implementation(project(":base"))

  implementation(project(":author-domain"))
  implementation(project(":author-entities"))

  implementation(Libraries.kotlinJDK)
  implementation(Libraries.appcompat)
  implementation(Libraries.androidXCore)
  implementation(Libraries.constraintLayout)
  implementation(Libraries.material)
  implementation(Libraries.fragmentKtx)

  implementation(Libraries.lifeCycleViewModel)
  implementation(Libraries.lifeCycleViewModelKtx)

  implementation(Libraries.roomRuntime)
  implementation(Libraries.roomKtx)
  kapt(Libraries.roomCompiler)

  implementation(Libraries.swipeRefreshLayout)

  implementation(AnnotationProcessors.daggerHilt)
  kapt(AnnotationProcessors.daggerHiltAndroidCompiler)
  implementation(AnnotationProcessors.daggerHiltViewModel)

  implementation(Libraries.paging)

  kapt(Libraries.kotlinxMetadata)

  implementation(Libraries.retrofit)

  implementation(Libraries.moshi)
  kapt(AnnotationProcessors.moshiCodegen)

  androidTestImplementation(Libraries.jUnitExtKtx)
  androidTestImplementation(Libraries.espressoCore)

  implementation(Libraries.jsonSerialization)
}

afterEvaluate {
  val function =
    extra.get("generateCheckCoverageTasks") as (File, String, Coverage, List<String>, List<String>) -> Unit
  function.invoke(
    buildDir,
    "testDebugUnitTest",
    Coverage(
      instructions = 100.0,
      lines = 100.0,
      complexity = 100.0,
      methods = 100.0,
      classes = 100.0
    ),
    emptyList(),
    emptyList()
  )
}
