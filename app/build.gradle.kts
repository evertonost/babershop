plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt") // Adicionado para processar anotações do Robolectric se necessário
}

android {
    namespace = "everton.ost.pi_barbershop"
    compileSdk = 34

    defaultConfig {
        applicationId = "everton.ost.pi_barbershop"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Usar o AndroidJUnitRunner
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Dependências do Firebase e outras bibliotecas
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")

    implementation(libs.androidx.espresso.intents)
    implementation(libs.core)
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    // Dependências para testes unitários
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("junit:junit:4.13.2")





    // Dependências para testes Android
    androidTestImplementation("org.mockito:mockito-android:5.5.0")

    

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Dependências para rodar o Firebase no ambiente de testes
    androidTestImplementation("com.google.firebase:firebase-auth:21.1.0")
    androidTestImplementation("com.google.firebase:firebase-firestore:24.5.1")

    // Dependências para navegação
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")
}
