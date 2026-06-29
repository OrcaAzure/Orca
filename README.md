# Orca

A modern Android network & developer toolkit built with Kotlin and Jetpack Compose.

## Tech Stack

- **Kotlin** — primary language
- **Jetpack Compose** — declarative UI
- **Material 3** — design system (dark mode first)
- **MVVM** — architecture (ViewModels added per feature)
- **Hilt** — dependency injection
- **Navigation Compose** — in-app navigation

## Sprint 1 — Foundation

This release establishes the app shell:

- Home screen with category cards (Network, Developer, Security, Device)
- Search, Favorites, and Settings screens
- Bottom navigation with animated transitions
- Dark theme inspired by Raycast and Arc Browser

## Project Structure

```
app/src/main/java/com/orca/app/
├── MainActivity.kt
├── OrcaApplication.kt
├── navigation/
│   ├── NavGraph.kt
│   └── Routes.kt
└── ui/
    ├── components/
    ├── screens/
    └── theme/
```

## Build

Requires Android SDK 35 and JDK 17.

```bash
./gradlew assembleDebug
```

## License

Private project.
