**Mobile Development 2025/26 Portfolio**
# API Description

Student ID: `c24056128`

The **Anime Tracker** application leverages several core components of the Android API to deliver a robust and modern user experience, adhering to the Modern Android Development (MAD) guidelines.

### UI and Layout
The application utilizes **ViewBinding** to interact with XML layouts. This choice is critical as it provides null-safety and compile-time validation, completely eliminating the need for the deprecated and brittle `findViewById()` method. For list display, **RecyclerView** is used in conjunction with **ListAdapter** and **DiffUtil**. Unlike the legacy `ListView`, `RecyclerView` efficiently recycles views, and `DiffUtil` ensures only changed items are updated, significantly reducing the performance overhead of `notifyDataSetChanged()`. Furthermore, **Vector Drawables** and theme-aware attributes are used to implement a minimalist background watermark, reducing visual clutter while maintaining a modern aesthetic.

### State Management and Lifecycle
Following the recommended MVVM architecture, the app uses **ViewModel** and **LiveData**. The `ViewModel` is designed to survive configuration changes, such as screen rotation on the Pixel 3a. By observing `LiveData` from the `MainActivity`, the UI automatically stays in sync with the underlying data state without manual intervention.

### Data Persistence and Storage
Local storage is handled by the **Room Persistence Library**, which provides an abstraction layer over SQLite. Room ensures that "Favorited" anime entries are saved safely and can be retrieved using Kotlin **Flow** for reactive updates. For simple key-value pairs, such as the 'last updated' timestamp, the app uses **Jetpack DataStore**. This modern replacement for `SharedPreferences` uses Coroutines and Flow to handle data updates asynchronously, avoiding the main-thread blocking issues associated with the older API.

### Network and Media
**Retrofit** is employed to communicate with the Jikan REST API. It simplifies network requests by converting API endpoints into Kotlin interfaces and automatically parsing JSON responses via GSON. For image loading, **Coil** (Coroutines-based Image Loader) is used to fetch and cache anime cover art efficiently, ensuring smooth scrolling in the `RecyclerView`.