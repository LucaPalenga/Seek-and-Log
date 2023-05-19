import org.gradle.api.JavaVersion

object AppConfig {

    const val minSdk = 26
    const val compileSdk = 33
    const val targetSdk = 33

    val javaVersion = JavaVersion.VERSION_1_8

    const val versionCode = 1
    const val versionName = "1.0"

}