-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @com.google.dagger.* <methods>;
}
-keep class com.mdesigns.khata.** { *; }
