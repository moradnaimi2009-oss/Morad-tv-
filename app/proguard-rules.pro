# ---------- Retrofit ----------
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep,allowobfuscation,allowshrinking interface retrofit2.http.** { *; }
-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }

# ---------- OkHttp / Okio ----------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }

# ---------- Gson ----------
-keepattributes Signature
-keep class com.lite.streamvault.data.dto.** { *; }
-keep class com.lite.streamvault.domain.model.** { *; }

# ---------- Hilt / Dagger ----------
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }

# ---------- AdMob ----------
-keep class com.google.android.gms.ads.** { *; }
-keep public class com.google.android.gms.common.** { *; }
-dontwarn com.google.android.gms.ads.**

# ---------- AppLovin ----------
-keep class com.applovin.** { *; }
-dontwarn com.applovin.**

# ---------- StartApp ----------
-keep class com.startapp.** { *; }
-dontwarn com.startapp.**

# ---------- Unity Ads ----------
-keep class com.unity3d.** { *; }
-dontwarn com.unity3d.**

# ---------- Coil ----------
-keep class coil.** { *; }
-dontwarn coil.**

# ---------- Media3 ----------
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ---------- Kotlin Coroutines ----------
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ---------- Keep names for serialization ----------
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
