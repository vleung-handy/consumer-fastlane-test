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

#Appsee
-keep class com.appsee.** { *; }
-dontwarn com.appsee.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keepattributes SourceFile,LineNumberTable

# Layer SDK
-keep class com.layer.** {*; }
-dontwarn com.layer.**

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
-keep class com.handybook.handybook.tool.data.** { *; }

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
-dontwarn com.facebook.**
-keepattributes Signature

#RxJava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}


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

#Stripe
-keep class com.stripe.** { *; }

#Crashlytics
-renamesourcefileattribute SourceFile
#^Appsee wants the above line removed,
#but keeping it doesn't seem to affect the functionality we care about
#and allows us to continuing obfuscating our source file names which is a better security practice
-keepattributes SourceFile,LineNumberTable

#GSON
-keepclassmembers class com.handybook.handybook.** {
    <fields>;
}
-keep class com.google.gson.** { *; }

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

#v7 support libraries
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }


# Configuration for Guava

-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static com.google.common.base.Joiner on(java.lang.String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Guava
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.concurrent.LazyInit
-dontwarn com.google.j2objc.annotations.RetainedWith
-dontwarn com.google.errorprone.annotations.ForOverride


# Button
-keepattributes Exceptions,InnerClasses,EnclosingMethod
-keep class com.usebutton.** { *; }
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient { public *; }


# Mixpanel
-dontwarn com.mixpanel.**
