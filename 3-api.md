**Mobile Development 2025/26 Portfolio**
# API Description

Student ID: `c24056128`

**WatchRyu** leverages a suite of modern Android APIs to provide a stable, accessible, and high-performance experience.

### UI and Navigation
The application uses **ViewBinding** to interact with layouts, ensuring null-safety and compile-time validation while strictly avoiding the deprecated `findViewById()`. For navigation, I implemented **ViewPager2** and **TabLayoutMediator** to manage seven distinct fragments. The list rendering utilizes **RecyclerView** with **ListAdapter** and **DiffUtil**. This architecture ensures that only changed data is updated, avoiding the performance-heavy `notifyDataSetChanged()` and providing a smooth user experience.

### State and Accessibility
Following the MVVM pattern, **ViewModel** and **LiveData** are used to maintain state across configuration changes, such as screen rotation. I implemented a sophisticated **Theme Overlay** system. In `MainActivity`, the app layers accessibility preferences (High Contrast and Font Scaling) on top of the base theme. By defining custom attributes in `attrs.xml`, I ensured that all text, including captions, scales proportionally—a significant improvement over basic SP scaling.

### Data and Storage
Local persistence is managed via the **Room Persistence Library (v4)**. I designed a schema to support tracking status, personal reviews, and rewatch counts. I implemented a **Repository pattern** to abstract data sources, providing the UI with a unified stream of data including a `lastUpdated` timestamp managed via **Jetpack DataStore**. This ensures that the app provides a reactive, non-blocking alternative to the deprecated `SharedPreferences` while maintaining efficient cache-awareness. To handle data portability, I integrated the **Storage Access Framework (SAF)**, allowing secure XML/CSV exports using standard Android system pickers.

### Networking and Analytics
**Retrofit** with **GSON** handles communication with the Jikan API. I implemented robust error handling to manage cases where MAL blocks automated scrapers, providing users with informative feedback. Finally, the **MPAndroidChart** API is used to generate dynamic Pie Charts, programmatically resolving theme-aware colors to ensure the UI remains cohesive and professional in both Light and Dark modes. (325 words)
