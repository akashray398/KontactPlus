# Hilt
-keep class dagger.hilt.** { *; }
-keep class com.akash.kontactplus.**_HiltComponents* { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao

# Kotlin Serialization
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault
-keepclassmembers class com.akash.kontactplus.** {
    *** Companion;
    *** serializer(...);
}

# Telecom
-keep class com.akash.kontactplus.core.telecom.KontactInCallService { *; }
-keep class com.akash.kontactplus.feature.telecom.ActiveCallActivity { *; }
