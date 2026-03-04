# Przelewy24 Android Example Project

This is an example Android project demonstrating the integration of the Przelewy24 payment library (p24lib) into an Android application.

## Project Description

The project showcases how to implement Przelewy24 payment processing in an Android app, including:
- Payment transactions via Przelewy24
- Google Pay integration
- Card registration functionality
- Payment settings management

## Prerequisites

Before building this project, ensure you have the following:

- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **JDK**: Version 17 or higher
- **Android SDK**: API level 34 (Android 14)
- **Gradle**: Version 8.2 (included in wrapper)
- **Przelewy24 Merchant Account**: Valid credentials for payment processing

## Setup Instructions

1. **Clone or download** this project
2. **Open in Android Studio**: File → Open → Select the project directory
3. **Sync Gradle**: Allow Android Studio to sync the project with Gradle files
4. **Configure Przelewy24**: Set up your merchant credentials in the payment settings

## Build Instructions

### Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean and rebuild
./gradlew clean build
```

### Android Studio
1. Select **Build** → **Make Project** from the menu
2. Or use keyboard shortcut: `Cmd+F9` (macOS) / `Ctrl+F9` (Windows/Linux)

## Configuration Requirements for Przelewy24

To use the Przelewy24 payment library, you need to configure the following:

### Merchant Credentials
- **Merchant ID (posId)**: Your unique merchant identifier
- **API Key**: Secret key for API authentication
- **Shop URL**: Your e-commerce website URL
- **Contact Email**: Merchant contact email

### Payment Settings
Configure payment options in the PaymentSettingsActivity:
- Enable/disable specific payment methods
- Set default currency (PLN recommended)
- Configure Google Pay if needed

### Google Pay Setup (Optional)
If using Google Pay integration:
1. Register your application in the [Google Pay Console](https://pay.google.com/business/console)
2. Configure your merchant ID
3. Update the `AndroidManifest.xml` with required metadata
4. Ensure `play-services-wallet` dependency is included

### ProGuard Rules
For release builds with minification enabled, the default ProGuard rules are used. If you encounter issues, add custom rules to [`proguard-rules.txt`](app/proguard-rules.txt).

## Project Structure

```
p24-mobile-lib-android-example/
├── app/
│   ├── src/main/
│   │   ├── java/pl/przelewy24/p24example/
│   │   │   └── P24ExampleActivity.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable-*/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/
│   └── wrapper/
├── build.gradle (root)
├── settings.gradle
├── gradle.properties
└── README.md
```

## Dependencies

| Dependency | Version |
|------------|---------|
| Android Gradle Plugin | 8.2.0 |
| Kotlin | 1.9.21 |
| Compile SDK | 34 |
| Min SDK | 21 |
| Target SDK | 34 |
| p24lib | 1.0.0 |
| Google Play Services Wallet | 19.3.0 |
| AndroidX AppCompat | 1.6.1 |
| Material Components | 1.11.0 |

## Important Notes

- **Minimum Android Version**: Android 5.0 (API level 21)
- **Target Android Version**: Android 14 (API level 34)
- **Java Version**: Java 17 compatibility required
- **Permissions**: The app requires INTERNET, ACCESS_NETWORK_STATE, and RECEIVE_SMS permissions

## Documentation

For detailed Przelewy24 library documentation, visit:
[Przelewy24 Android Library Documentation](https://github.com/przelewy24/p24-mobile-lib-android)

## Support

For technical support and questions regarding the Przelewy24 payment integration, contact the Przelewy24 technical team.

## License

This example project is provided for demonstration purposes. Refer to the Przelewy24 library license for usage terms.
