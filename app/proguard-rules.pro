# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends androidx.hilt.work.HiltWorker

# Keep Room generated classes
-keep class androidx.room.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
