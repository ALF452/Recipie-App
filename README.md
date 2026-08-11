# Recipe Box

A simple Android app for saving your own cooking recipes, built with Kotlin, Jetpack Compose, and Room (local on-device storage — no account or internet needed).

## Features

- List all saved recipes
- Add a new recipe (title, category, ingredients, instructions, notes)
- View a recipe's full details
- Edit or delete an existing recipe

## Tech stack

- Kotlin
- Jetpack Compose + Material 3
- Room (local SQLite database)
- Navigation Compose
- MVVM (`RecipeViewModel` + `RecipeRepository`)

## Requirements

- Android Studio (Koala or newer recommended)
- JDK 17
- Android SDK with `compileSdk 34` installed (Android Studio will prompt to install this automatically)

## Running the app

1. Open this project folder in Android Studio.
2. Let Gradle sync (this downloads dependencies from Google's Maven repo, so you'll need normal internet access — the sandbox this project was scaffolded in blocks that host, so the build was not verified there).
3. Run on an emulator or a physical device (minSdk 26 / Android 8.0+).

Or from the command line once you have the Android SDK set up locally:

```
./gradlew assembleDebug
```

## Project structure

```
app/src/main/java/com/alf452/recipeapp/
  data/        Room entity, DAO, database, repository
  ui/          ViewModel and Compose screens (list, detail, add/edit)
  navigation/  Navigation graph
  MainActivity.kt
```
