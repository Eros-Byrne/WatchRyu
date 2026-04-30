**Mobile Development 2025/26 Portfolio**
# Requirements

Student ID: `c24056128`

## Functional Requirements

- **FR1: Jikan API Discovery** - The application shall fetch and display anime from Top, Seasonal, and Upcoming categories using the Jikan REST API.
- **FR2: Multi-Category Tracking** - Users shall be able to track anime across seven statuses: Airing, Watching, Completed, On-Hold, Dropped, and Plan to Watch.
- **FR3: Granular Progress Management** - The app shall allow users to update episodes watched, set a personal score (1-10), and write personal reviews for each show.
- **FR4: Data Analytics** - The application shall provide a dashboard with a Pie Chart breakdown of the library and calculate total time spent watching in Days, Hours, and Minutes.
- **FR5: Account Portability** - Users shall be able to import and export their library using the standard MyAnimeList XML format and local CSV files.
- **FR6: Global Accessibility** - The app shall provide a settings panel to toggle High Contrast mode and adjust global Font Size (Small, Medium, Large).
- **FR7: Real-Time Discovery Filters** - Users shall be able to filter discovery lists by official MAL genres and search show titles in real-time.

## Non-Functional Requirements

- **NFR1: MAD Compliance** - The application shall use ViewBinding, Room, and DataStore, strictly avoiding deprecated methods like `findViewById` or `SharedPreferences`.
- **NFR2: Performance** - The UI shall remain responsive during heavy API fetches, using Kotlin Coroutines for asynchronous processing.
- **NFR3: Resource Efficiency** - The app shall use `RecyclerView` with `DiffUtil` to ensure smooth scrolling and low battery consumption.
- **NFR4: Reliability** - The application shall handle API instability (e.g., MAL blocking scrapers) by providing clear error feedback and manual XML import fallbacks.
- **NFR5: Branded UX** - The application shall feature a custom brand identity (WatchRyu) with a vector adaptive icon and themed backgrounds.
- **NFR6: Device Target** - The application must be fully compatible with a Pixel 3a emulator running API 35.
