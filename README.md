# MLBB Draft AI

An intelligent draft assistant Android app for Mobile Legends: Bang Bang that recommends the Top 5 counter-picks based on enemy draft, allied synergy, role fit, and ranked performance.

## Features (V0.1 Prototype)
- **Role Selection**: Filter recommendations strictly by role (EXP Lane, Jungle, Mid Lane, Gold Lane, Roam).
- **Interactive Team & Ban Slots**: Assign and clear allied picks (5), enemy picks (5), and bans (10).
- **Searchable Hero Picker**: Filter by name and role, preventing selection of unavailable heroes.
- **Dynamic Recommendation Engine**:
  - Enemy counter weighting (60%)
  - Role suitability (20%)
  - Allied synergy (10%)
  - Meta strength (10%)
- **Deterministic Explanations**: Provides machine-generated rationale for each recommendation (e.g., counters, weaknesses, team synergy).
- **Offline First**: Bundled local dataset requires no external APIs or network connectivity.

---

## Building with GitHub & Locally

### Prerequisites
- **JDK 17** or **JDK 21**
- **Android SDK** with API level 36 (`compileSdk = 36`)
- **Git**

### Setup & Build Commands

1. **Clone the repository**:
   ```bash
   git clone <your-repo-url>
   cd mlbb-draft-ai
   ```

2. **Prepare the environment**:
   ```bash
   # Create .env from template
   cp .env.example .env

   # Restore debug keystore (if base64 template is present)
   if [ -f debug.keystore.base64 ]; then
     base64 -d debug.keystore.base64 > debug.keystore
   fi
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

4. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   The generated APK will be at:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## CI / GitHub Actions
The repository includes a ready-to-use GitHub Actions workflow in `.github/workflows/build.yml` that:
- Automatically runs unit tests on every push and pull request.
- Builds the debug APK on Ubuntu runners.
- Uploads the APK as a downloadable workflow artifact.
