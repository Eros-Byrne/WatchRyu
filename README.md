WatchRyu is a full-stack anime tracking web application that allows users to manage their personal anime watchlists entirely from their own machine — no accounts, no cloud, no subscriptions required. All data is stored in a local SQLite database, giving the user full ownership of their information.
The application connects to the Jikan API (an unofficial MyAnimeList REST API) to pull live data such as currently airing shows, upcoming seasons, community ratings, and cover art. Users can also import an existing MyAnimeList account or upload a standard MAL XML export file to populate their library instantly.
This project was built progressively as part of a student assignment, with features added across multiple development sessions.

✨ Features
📑 Tab Navigation

Currently Airing — Browse live airing shows pulled from the Jikan API with search functionality
Upcoming Shows — View and search announced but not yet airing anime
Currently Watching — Your active watchlist with episode progress tracking
Completed — All finished shows with personal scores and reviews
Dropped — Shows you stopped watching
Plan to Watch — Your backlog queue
Stats — Overview of your watch history with charts and time breakdowns

📊 Stats & Data Visualisation

Total shows watched counter
Pie chart (Chart.js) showing time spent watching broken down into days, hours, and minutes
Category breakdown showing count per list

🎴 Anime Cards

Show title, episode progress, and cover art
Official MAL community score fetched from the Jikan API displayed alongside your personal rating
Edit, move between lists, or delete entries
Review indicator icon when a personal review has been written

✍️ Personal Reviews

Write and save your own review or notes for any show
Reviews stored in the local SQLite database tied to each entry
Expandable text area opened directly from the anime card

🔍 Search

Search bar on the Currently Airing tab to filter live API results
Separate search bar for the Upcoming Shows section

📥 Import Options

MAL Username Import — Enter a MyAnimeList username to fetch and import their public list via the Jikan API
XML File Upload — Upload a standard MAL XML export file and have it parsed and loaded into your local database

📤 Export Options

Full Account Export — Download your entire local library as an XML file in standard MAL format
Per-List Export — Export any individual tab (Currently Watching, Completed, Dropped, Plan to Watch) as XML or CSV

⚙️ Settings Panel

Theme Switcher — Toggle between Light, Dark, and warm Brown/Tan mode; preference saved across sessions
Contrast Settings — Adjust contrast level per theme without breaking colour schemes
Font Size Options — Choose Small, Medium, or Large; applied globally and saved locally
Settings panel accessible on every tab