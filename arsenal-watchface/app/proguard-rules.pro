# Arsenal Watch Face ProGuard Rules

# Keep Watch Face components
-keep class com.arsenal.watchface.** { *; }

# Keep Wear OS classes
-keep class androidx.wear.watchface.** { *; }
-keep class androidx.wear.complications.** { *; }

# Keep data classes for serialization
-keepclassmembers class com.arsenal.watchface.data.** {
    <fields>;
    <init>(...);
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Play Services
-keep class com.google.android.gms.** { *; }
