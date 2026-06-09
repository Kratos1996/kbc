# Add project specific ProGuard rules here.

# Keep generic signatures for Retrofit
-keepattributes Signature, Exceptions, *Annotation*, EnclosingMethod, InnerClasses
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retrofit
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper

# Keep all DTOs used by Retrofit (Moshi @JsonClass(generateAdapter = true))
-keep class com.ishan.kbc.data.remote.dto.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Billing
-keep class com.android.billingclient.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
