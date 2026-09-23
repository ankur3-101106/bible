# Release Signing Configuration & Setup

## Keystore Security
Release keystores and passwords must **never** be committed to Git or public repositories.

## Local & CI Setup
In `app/build.gradle.kts`, release signing reads from environment variables or a local `keystore.properties` file:

```properties
KEYSTORE_FILE=path/to/release.keystore
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

If `keystore.properties` is absent, the release build falls back safely to debug signing for local testing.
