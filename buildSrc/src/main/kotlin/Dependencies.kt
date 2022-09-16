object Versions {
    const val kotlin = "1.6.0"
    const val kotlinxMetadata = "0.5.0"
    const val gradle = "7.2.2"
    const val jvmTarget = "11"

    const val gradleKlint = "10.0.0"
    const val androidJUnit5 = "1.7.1.1"
    const val jacoco = "0.8.8"

    const val sonarqube = "3.1.1"

    const val dokka = "1.5.0"

    //AndroidX
    const val appCompat = "1.3.0"
    const val core = "1.5.0"
    const val jUnit = "4.13.2"
    const val jUnitExtKtx = "1.1.2"
    const val testCoreKtx = "1.3.0"

    const val material = "1.3.0"

    const val constraintLayout = "2.1.4"
    const val espressoCore = "3.3.0"
    const val androidXRunner = "1.3.0"
    const val androidXRules = "1.3.0"
    const val fragmentKtx = "1.3.3"
    const val activityKtx = "1.2.2"
    const val recyclerView = "1.2.0"
    const val desugarJdkLibs = "1.1.5"

    const val splashScreen = "1.0.0"

    const val dagger = "2.42.0"
    const val daggerHilt = "2.38.1"
    const val daggerHiltLifeCycle = "1.0.0-alpha03"

    const val retrofit = "2.9.0"

    const val jsonSerialization = "1.2.2"

    const val moshi = "1.14.0"
    const val multidex = "2.0.1"

    const val javaInject = "1"

    const val mockk = "1.11.0"

    const val okHttpBoM = "4.10.0"

    const val lifeCycle = "2.5.1"
    const val lifeCycleExtensions = "2.2.0"

    const val navigation = "2.5.2"

    const val room = "2.4.0-rc01"

    const val seeMoreTextView = "1.0.0"

    const val swipeRefreshLayout = "1.1.0"

    const val coroutines = "1.6.4"

    const val kluent = "1.67"

    const val paging = "3.1.1"

    const val leakCanary = "2.7"
    const val timber = "4.7.1"

    const val jUnit5 = "5.7.1"

    const val barista = "4.2.0"
    const val orchestrator = "1.3.0"

    const val okReplay = "1.6.0"
}

object Api {
    const val compileSDK = 32
    const val minSDK = 21
    const val targetSDK = 32
}

object Libraries {
    const val kotlinJDK = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val androidXCore = "androidx.core:core-ktx:${Versions.core}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val kotlinxMetadata = "org.jetbrains.kotlinx:kotlinx-metadata-jvm:${Versions.kotlinxMetadata}"

    const val preferences = "androidx.preference:preference-ktx:1.1.1"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
    const val activityKtx = "androidx.activity:activity-ktx:${Versions.activityKtx}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}"

    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitMoshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"

    const val jsonSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.jsonSerialization}"

    const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"

    const val multidex = "androidx.multidex:multidex:${Versions.multidex}"

    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val jUnitExtKtx = "androidx.test.ext:junit-ktx:${Versions.jUnitExtKtx}"
    const val testCoreKtx = "androidx.test:core-ktx:${Versions.testCoreKtx}"
    const val androidXRunner = "androidx.test:runner:${Versions.androidXRunner}"
    const val androidXRules = "androidx.test:rules:${Versions.androidXRules}"
    const val barista = "com.adevinta.android:barista:${Versions.barista}"
    const val orchestrator = "androidx.test:orchestrator:${Versions.orchestrator}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"

    const val lifeCycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifeCycle}"
    const val lifeCycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycle}"

    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomPaging = "androidx.room:room-paging:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    const val seeMoreTextView = "com.github.AhmMhd:SeeMoreTextView-Android:${Versions.seeMoreTextView}"

    const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"

    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"

    const val paging = "androidx.paging:paging-runtime:${Versions.paging}"

    const val lifeCycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycleExtensions}"
    const val lifeCycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycle}"
    const val lifeCycleProcess = "androidx.lifecycle:lifecycle-process:${Versions.lifeCycle}"
    const val lifeCycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifeCycle}"
    const val lifeCycleCommonJava8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifeCycle}"

    const val javaInject = "javax.inject:javax.inject:${Versions.javaInject}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    const val okHttpBoM = "com.squareup.okhttp3:okhttp-bom:${Versions.okHttpBoM}"
    const val okHttp = "com.squareup.okhttp3:okhttp"
    const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.okHttpBoM}"

    const val jUnit5 = "org.junit.jupiter:junit-jupiter:${Versions.jUnit5}"
    const val jUnit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.jUnit5}"

    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val kluent = "org.amshove.kluent:kluent-android:${Versions.kluent}"

    val suiteTest = arrayOf(mockk, jUnit5, jUnitExtKtx, testCoreKtx, kotlinTest, kotlinReflect, jUnit, coroutinesTest, kluent)

    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val okReplay = "com.airbnb.okreplay:okreplay:${Versions.okReplay}"
    const val okReplayNoop = "com.airbnb.okreplay:noop:${Versions.okReplay}"
    const val okReplayEspresso = "com.airbnb.okreplay:espresso:${Versions.okReplay}"
}

object AnnotationProcessors {
    const val daggerHilt = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val daggerHiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}"
    const val daggerHiltViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.daggerHiltLifeCycle}"

    const val moshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
}
