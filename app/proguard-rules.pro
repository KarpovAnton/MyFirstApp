# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\U_M0SMV\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

# Proguard rules that are applied to your test apk/code.

-ignorewarnings
-keepattributes EnclosingMethod

#ButterKnife
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(...); }
# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

#picasso
-dontwarn com.squareup.okhttp.**

#SearchView
 -keep class android.support.v7.widget.SearchView { *; }

#FireBase
 -keep class com.firebase.** { *; }
 -keep class org.apache.** { *; }
 -keepnames class com.fasterxml.jackson.** { *; }
 -keepnames class javax.servlet.** { *; }
 -keepnames class org.ietf.jgss.** { *; }
 -dontwarn org.w3c.dom.**
 -dontwarn org.joda.time.**
 -dontwarn org.shaded.apache.**
 -dontwarn org.ietf.jgss.**

 #warnings
 -dontwarn butterknife.internal.**
 -dontwarn okio.**
 -dontwarn org.spongycastle.**
 -dontwarn ru.alfabank.**

 #app
 -keep public class org.simpleframework.** { *; }
 -keep class org.simpleframework.xml.** { *; }
 -keep class org.simpleframework.xml.core.** { *; }
 -keep class org.simpleframework.xml.util.** { *; }
 
-keepclassmembers class com.karpov.vacuum.network.data.dto.** { <fields>; }