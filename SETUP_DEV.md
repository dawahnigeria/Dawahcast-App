In order to get started you must setup your development environment. This project supports the most popular Integrated Development Environment (IDE). Pick the one you are most familiar with:
* [Eclipse](https://github.com/dawahnigeria/Dawahcast-App/new/master#setting-up-netbeans-(Eclipse-NetBeans)#setting-up-eclipse)
* [Android Studio](https://github.com/dawahnigeria/Dawahcast-App/new/master#setting-up-android-studio-(Eclipse-NetBeans)#setting-up-android-studio)



### Setting up Eclipse 
Please note that Eclipse has been [deprecated for Android development by Google](https://androiddevelopers.googleblog.com/2015/06/an-update-on-eclipse-android-developer.html). 
To develop your application via Eclipse, you need to install the following pieces of software.

  * [Java Development Kit 8+ (JDK) (7 may not work!)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * [Eclipse](http://www.eclipse.org/downloads/), the "Eclipse IDE for Java Developers" is usually sufficient.
  ** [Eclipse for Android Developers](http://www.eclipse.org/downloads/packages/eclipse-android-developers-includes-incubating-components/neonr), Contains everything necessary to run described here.
  * [Android SDK](http://developer.android.com/sdk/index.html), you only need the SDK (available at the bottom of the page in the 'command line tools' section), not the whole Android Studio package which is a customized version of Intellij bundled with the Android SDK. Install the latest stable platform via the [SDK Manager](http://developer.android.com/tools/help/sdk-manager.html). If you install the ADT Plugin as described below, then you can use the Android SDK Manager that comes with the plugin instead, to simplify this process. 
  * [Android Development Tools for Eclipse](http://developer.android.com/tools/sdk/eclipse-adt.html), aka ADT Plugin. Use this update site: https://dl-ssl.google.com/android/eclipse/
  * [Eclipse Integration Gradle](https://github.com/spring-projects/eclipse-integration-gradle/), use this update site: http://dist.springsource.com/milestone/TOOLS/gradle or if you feel brave  http://dist.springsource.com/snapshot/TOOLS/gradle/nightly (for Eclipse 4.4) or http://dist.springsource.com/release/TOOLS/gradle (for Eclipse < 4.4)



### Setting up Android Studio
To run this application via Android Studio, you need to install the following pieces of software.

  * [Java Development Kit 7+ (JDK) (6 will not work!)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * [Android Studio](https://developer.android.com/sdk/index.html) already comes packaged with the Android SDK so contrary to Eclipse you do not need to install this component.



### Setting up NetBeans
To run this application via NetBeans, you need to install the following pieces of software.

  * [Java Development Kit 7+ (JDK) (6 will not work!)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * [NetBeans 7.3+](https://netbeans.org/downloads/), the "Java SE" is sufficient
  * [Android SDK](http://developer.android.com/sdk/index.html), you only need the SDK (available at the bottom of the page in the 'command line tools' section), not the whole Android Studio package which is a customized version of Intellij bundled with the Android SDK. Install the latest stable platform via the [SDK Manager](http://developer.android.com/tools/help/sdk-manager.html). You also have to create an environment variable called ANDROID_HOME, which points at your Android SDK installation directory!
  * [NBAndroid](http://www.nbandroid.org), use this update center: http://nbandroid.org/updates/updates.xml (if you have NetBeans 8.1 or higher, use http://nbandroid.org/release81/updates/updates.xml ).
  * [Gradle Support for NetBeans](https://github.com/kelemen/netbeans-gradle-project), use the NetBeans IDE Update Center.

### Import Project

**Start Android Studio

**File >> New >> Import Project and select the Gradle build java project



### P.S

To avoid Build error kindly update all packages under Android API 24 via SDK manager.
