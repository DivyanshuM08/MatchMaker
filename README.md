# MatchMate

A small Android app that shows match cards (matrimonial-style). You can accept or decline profiles; everything is stored locally and works offline.

## How to run

Clone the repo, open the project in Android Studio, sync Gradle, and run on an emulator or device (min SDK 24).

## Libraries

- **Hilt** – Dependency injection for Application, Database, API, Repository, and ViewModel; no manual factories or singletons.
- **Retrofit + OkHttp** – Fetch users from the API; OkHttp for logging and timeouts.
- **Gson** – Parse the JSON response (with a custom deserializer for the API’s mixed postcode type).
- **Coroutines** – Background work and Flow for the profile list.
- **Room** – Local DB for profiles and accept/decline state; offline-first.
- **Glide** – Load profile images in the list (with circle crop).
- **Lifecycle / ViewModel** – Hold UI state and talk to the repository without leaking the activity.

## Architecture

We use **MVVM** with a **Repository** and **Hilt** for DI. The Application is annotated with `@HiltAndroidApp`; a single `AppModule` provides `AppDatabase`, `MatchProfileDao`, Gson, OkHttp, Retrofit, `RandomUserApi`, and `MatchRepository`. The screen is an `@AndroidEntryPoint` Activity that gets a `@HiltViewModel` with `MatchRepository` and `Application` injected. The screen gets data from the ViewModel, which talks only to the repository. The repository is the single source of truth: it reads from Room and, on refresh, fetches from the API and writes to Room. The UI observes a Flow from the repository, so it stays in sync and works offline.

## Extra profile fields

The assignment asked for at least two extra fields that matter for a matrimonial app. We added **education**, **religion**, and **occupation**. The API doesn’t provide these, so we fill them from small mock lists when mapping the API response. They’re useful filters and display on the card.

## Match score

We use a simple formula: up to 50 points for age (closer age = higher score, using `max(0, 50 - 2 * |myAge - cardAge|)`), and 50 points if the city matches (case-insensitive). The total is capped at 100. “My” age and city are fixed in `MyProfile` for now; you could later plug in a real profile screen or prefs.

## Offline and errors

The list always comes from Room. **On first launch**, if the device has no internet we do not call the API and we show a Snackbar (“No Internet available. Please try again.”). When internet is available at launch we trigger a refresh. A **refresh icon** in the toolbar is shown only when the device is offline; the user can tap it once back online to load data (we only call the API when network is available). When you tap refresh (or the toolbar icon when online), we fetch from the API and merge into the DB. If the request fails (or our 30% flaky simulation kicks in), we retry a few times with backoff; on success we persist whatever we got. Errors are shown in a Snackbar with a Retry action. We listen for network changes and show “You’re offline” / “Back online” only when connectivity actually changes during the session (not on first app open), so the user knows when data might be stale. All user-facing strings are in `res/values/strings.xml`.

## If we couldn’t show profile images

If a legal or product rule said we can’t show profile photos, we’d hide the image view (or replace it with a placeholder / initials avatar) and tweak the card layout so name, age, city, and actions still look fine. The rest of the app (API, DB, accept/decline, match score) would stay the same.

## If I had more time

I’d add a proper “my profile” screen so the user can set their own age and city (and maybe education/religion) and have the match score reflect that. Right now it’s hardcoded; making it configurable would make the score feel more meaningful.
