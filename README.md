# Itinero
Collaborative Trip Planning App

**Itinero** is a modern travel planning app built with Jetpack Compose. It enables travel groups to collaboratively organize, track, and manage all aspects of a trip — including itineraries, accommodations, shared expenses, packing lists, and AI-assisted travel suggestions.

## Features

- **Group Itinerary Management**  
  Plan and share day-by-day travel activities with your group.

- **AI-Powered Assistant**  
  Integrated GPT-based chat to help with itinerary suggestions, smart summaries, and location-specific queries.

- **Shared Packing Lists**  
  Collaboratively manage what to bring, assign responsibilities, and avoid duplicates.

- **Expense Tracking**  
  Record shared expenses, split costs among group members, and keep track of individual balances.

- **Accommodation Management**  
  Store check-in/check-out times, phone numbers, and relevant lodging details accessible to the entire group.

- **Smart Notifications**  
  Automatically notify users of important times such as hotel check-ins or transportation departures.

- **Group Invitation by Code or QR**  
  Users can join a trip group by manually entering a group code or by scanning a QR code that deep-links directly into the app.

## QR Code and Deep Link Integration

Itinero supports joining a group via:

- **Manual Entry**  
  Each group has a unique invitation code in the format `ITN-xxxxx`

- **QR Code Scanning**  
  QR codes can encode a deep link that opens the app via standard Android intents, enabling quick and seamless access to the correct trip group.

## Status

Currently in active development. New features are being added and refined regularly.


### Product Flavors System

This project uses Android product flavors to automatically build different variants based on the Git
branch:

## Flavor Mapping

| Branch | Flavor | App ID | App Name | Environment |
|--------|--------|---------|----------|-------------|
| `master`/`main` | `release` | `com.serranoie.app.itinero` | Itinero | production |
| `develop` | `beta` | `com.serranoie.app.itinero.beta` | Itinero Beta | beta |
| Any other branch | `alpha` | `com.serranoie.app.itinero.alpha` | Itinero Alpha | alpha |

## Manual Build Commands

### Local Development

```bash
# Build alpha debug (feature branches)
./gradlew assembleAlphaDebug

# Build beta debug (develop branch)
./gradlew assembleBetaDebug

# Build release debug (master/main branch)
./gradlew assembleReleaseDebug

# Build production release
./gradlew assembleReleaseRelease
```

### Running Tests

```bash
# Run tests for specific flavor
./gradlew testAlphaDebugUnitTest
./gradlew testBetaDebugUnitTest
./gradlew testReleaseDebugUnitTest
```

### Running Lint

```bash
# Run lint for specific flavor
./gradlew lintAlphaDebug
./gradlew lintBetaDebug
./gradlew lintReleaseDebug
```

## CI/CD Integration

### GitHub Actions

The CI/CD pipeline automatically detects the branch and builds the appropriate flavor:

- **Pull Requests to `develop`**: Builds `beta` flavor
- **Pushes to `develop`**: Builds `beta` flavor
- **Pushes to `master`/`main`**: Builds `release` flavor
- **Feature branches**: Builds `alpha` flavor

### Build Artifacts

Each flavor produces uniquely named artifacts:

- `debug-apk-alpha` for alpha builds
- `debug-apk-beta` for beta builds
- `debug-apk-release` for release builds

## Configuration

### Build Configuration Fields

Each flavor has access to these BuildConfig fields:

- `ENVIRONMENT`: "alpha", "beta", or "production"
- `BASE_URL`: API endpoint for the respective environment

### App Names and IDs

- **Alpha**: `com.serranoie.app.itinero.alpha` - "Itinero Alpha"
- **Beta**: `com.serranoie.app.itinero.beta` - "Itinero Beta"
- **Release**: `com.serranoie.app.itinero` - "Itinero"

This allows installing multiple versions side-by-side on the same device.

## Usage in Code

```kotlin
// Access build configuration
val environment = BuildConfig.ENVIRONMENT
val baseUrl = BuildConfig.BASE_URL
val isDebug = BuildConfig.DEBUG

// Check environment
when (BuildConfig.ENVIRONMENT) {
    "alpha" -> // Alpha-specific code
    "beta" -> // Beta-specific code
    "production" -> // Production-specific code
}
```

## Future Enhancements

- [ ] Add proper release signing configuration
- [ ] Create different app icons for each flavor
- [ ] Add flavor-specific Firebase configurations
- [ ] Implement automatic version bumping
- [ ] Add flavor-specific notification channels
- [ ] Create different themes for each flavor