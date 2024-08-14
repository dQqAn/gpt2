import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.undercouch)
    alias(libs.plugins.google.service)
    alias(libs.plugins.serialization)
}

project.ext.set("ASSET_DIR", "$projectDir/src/androidMain/assets")
//project.ext.set("TEST_ASSETS_DIR", "$projectDir/src/androidTest/assets")
apply("download_models.gradle")
//apply("download.gradle")

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_19)
        }
    }

//    jvm("desktop")

    sourceSets {
//        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(project.dependencies.platform(libs.google.bom))
            implementation(libs.google.auth)
            implementation(libs.google.database)
            implementation(libs.google.firestore)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.preview)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.viewmodel)
            implementation(libs.navigation)
            implementation(libs.koin)
            implementation(libs.gson)
            implementation(libs.room.runtime)
            implementation(libs.retrofit)
            implementation(libs.retrofit.gson)
            implementation(libs.lifecycle)
            implementation(libs.lifecycle.livedata)
            implementation(libs.tensorflow.lite)
            implementation(libs.tensorflow.text)
            implementation(libs.tensorflow.gpu.delegate)
            implementation(libs.tensorflow.gpu)
            implementation(libs.serialization)
        }
        /*desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }*/
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].java.srcDir("src/androidMain/java")
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
        }
        externalNativeBuild {
            cmake {
                // When set, builds whisper.android against the version located
                // at GGML_HOME instead of the copy bundled with whisper.cpp.
                if (
                    project.hasProperty("GGML_HOME") &&
                    project.findProperty("GGML_CLBLAST") == "ON"
                ) {
                    // Turning on CLBlast requires GGML_HOME
                    arguments.addAll(
                        listOf(
                            "-DGGML_HOME=${project.property("GGML_HOME")}",
                            "-DGGML_CLBLAST=ON",
                            "-DOPENCL_LIB=${project.property("OPENCL_LIB")}",
                            "-DCLBLAST_HOME=${project.property("CLBLAST_HOME")}",
                            "-DOPENCL_ROOT=${project.property("OPENCL_ROOT")}",
                            "-DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=BOTH",
                            "-DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=BOTH"
                        )
                    )
                } else if (project.hasProperty("GGML_HOME")) {
                    arguments.add("-DGGML_HOME=${project.property("GGML_HOME")}")
                }

            }
        }
    }
    ndkVersion = "25.2.9519653"
    externalNativeBuild {
        cmake {
            path = file("src/androidMain/jni/whisper/CMakeLists.txt")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            getDefaultProguardFile("proguard-android-optimize.txt")
            proguardFile("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
    androidResources.noCompress.add("tflite")
}

/*compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}*/

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies { ksp(libs.room.compiler) }
