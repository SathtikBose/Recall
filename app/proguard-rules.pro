# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Hilt rules
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.AndroidEntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.HiltAndroidApp class *

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Compose rules
-keep class androidx.compose.ui.** { *; }

# Models
-keep class com.buildstack.recall.data.local.entity.** { *; }
-keep class com.buildstack.recall.domain.model.** { *; }
