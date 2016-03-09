# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android Studio.app/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#card.io
-keep class io.card.**
-keepclassmembers class io.card.** {
    *;
}

#DeepLinkDispatch library
-keep class com.airbnb.deeplinkdispatch.** { *; }
-keepclasseswithmembers class * {
     @com.airbnb.deeplinkdispatch.DeepLink <methods>;
}

-keepattributes EnclosingMethod, InnerClasses

-dontshrink  #currently having compile issues
-dontoptimize #currently having compile issues

#Some warnings we think are safe to ignore
-dontwarn sun.misc.Unsafe

#typed handy retrofitcallback
-keep class com.handybook.handybook.data.** { *; }

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

#Butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.Bind *; }

#Dagger
-dontwarn dagger.internal.codegen.**
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter

#Otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#Facebook
-keep class com.facebook.** { *; }
-keepattributes Signature

#Retrofit
-dontwarn rx.**
-dontwarn okio.**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Annotation
-keep class retrofit.** { *; }
-keepclasseswithmembers class * { @retrofit.http.* <methods>; }
-keepattributes Signature

#OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

#Newrelic
-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses

#Stripe
-keep class com.stripe.** { *; }

#Crashlytics
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#GSON
-keepclassmembers class com.handybook.handybook.** {
    <fields>;
}

#Urban Airship
-dontwarn com.urbanairship.**
-keepnames class * implements android.os.Parcelable {
  public static final ** CREATOR;
}
-dontwarn com.amazon.device.messaging.**
-keepclassmembers class com.urbanairship.js.UAJavascriptInterface {
   public *;
}
-keep public class * extends com.urbanairship.Autopilot

#Yozio
-keep public class com.handybook.handybook.yozio.YozioMetaDataCallback

#Android 23 workaround
-dontwarn com.viewpagerindicator.LinePageIndicator
-dontwarn com.mixpanel.android.mpmetrics.GCMReceiver

#v7 support libraries
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
