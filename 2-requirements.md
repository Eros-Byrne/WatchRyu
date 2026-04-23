**Mobile Development 2025/26 Portfolio**
# Requirements

Student ID: `c24056128`

## Functional Requirements

- **FR1: Fetch Seasonal Anime** - The application shall retrieve a list of currently airing anime from the Jikan API.
- **FR2: Display Anime Details** - The application shall display the title, synopsis, and cover image for each anime entry.
- **FR3: Favorite Management** - Users shall be able to add or remove anime from a local "Favorites" list.
- **FR4: Local Persistence** - Favorite anime data shall be stored locally and remain available even without an internet connection.
- **FR5: Last Updated Tracking** - The application shall track and display the last time the anime list was successfully updated from the API.
- **FR6: State Preservation** - The application shall maintain its UI state (e.g., current list data) during configuration changes like screen rotation.

## Non-Functional Requirements

- **NFR1: Modern Architecture** - The app must follow the MVVM (Model-View-ViewModel) pattern as recommended by Google.
- **NFR2: Performance** - The UI shall remain responsive during data fetching, using background threads (Coroutines) to avoid blocking the main thread.
- **NFR3: Resource Efficiency** - The application shall use `RecyclerView` with `DiffUtil` for list rendering to minimize resource consumption and battery drain.
- **NFR4: Reliability** - The app shall handle network failures gracefully, displaying clear feedback or cached data where appropriate.
- **NFR5: Maintainability** - The codebase shall be organized into logical packages (data, model, ui, viewmodel) and use ViewBinding to avoid brittle `findViewById()` calls.
- **NFR6: Compatibility** - The application must run correctly on a Pixel 3a emulator with Android 15 (API 35).