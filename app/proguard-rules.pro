# Add project specific ProGuard rules here.

# kotlinx.serialization: keep the generated serializer and fields for the one
# @Serializable class used for recipe sharing. The library ships its own
# consumer rules for the general case; this is a defensive, narrow pin for
# our specific type since it can't be verified with a local release build.
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class com.alf452.recipeapp.data.SharedRecipe {
    <fields>;
}
-keepclasseswithmembers class com.alf452.recipeapp.data.SharedRecipe {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class com.alf452.recipeapp.data.SharedRecipe$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
