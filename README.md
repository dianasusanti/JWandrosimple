# NWT Reader (Indonesian) — a lightweight alternative to JW Library

A minimal Android app: pick a book, pick a chapter, read. No account, no media library,
no videos cached on disk, no bloat — just text. This is why it'll take a fraction of
JW Library's storage footprint.

## What's here vs. what you add

This project ships with:
- A working app shell (Kotlin, plain Android Views — no Compose, no Room, tiny APK)
- The full 66-book index with correct Indonesian names and chapter counts (`generate_index.py`)
- A converter (`convert_txt_to_json.py`) that turns plain text you provide into the app's data format

It does **not** ship with any NWT verse text. That text is copyrighted by the Watch Tower
Bible and Tract Society, and I'm not going to scrape it or extract it from JW Library's
data for you — that's the one thing I won't automate here. You'll need to type or paste
in text you already have the right to use for personal reading. Once it's in the right
format, the app does the rest.

## Adding a book's text

1. Create a plain text file, one verse per line, format `chapter:verse text`:

   ```
   1:1 Pada mulanya Allah menciptakan langit dan bumi.
   1:2 Bumi belum berbentuk dan kosong...
   2:1 Demikianlah langit dan bumi...
   ```

   Save it under `raw_text/`, e.g. `raw_text/kejadian.txt`.

2. Run the converter:

   ```
   python3 scripts/convert_txt_to_json.py raw_text/kejadian.txt kejadian
   ```

   This writes `app/src/main/assets/bible/kejadian.json`. The second argument must match
   the `code` field for that book in `app/src/main/assets/bible/index.json` (already
   generated for all 66 books — see that file for every code, e.g. `matius`, `yohanes`,
   `wahyu`).

3. Rebuild the app (see below). Any book without a matching `.json` file just shows
   "Teks belum tersedia" (text not available yet) instead of crashing — so you can fill
   books in gradually, most-used first.

## Building the app

This sandbox has Java and Gradle but **not the Android SDK**, so I can't compile an
.apk here. Open the project in Android Studio on your own machine instead:

1. Install [Android Studio](https://developer.android.com/studio) if you don't have it.
2. File → Open → select the `nwt-reader` folder.
3. Let Gradle sync (first sync downloads the Android SDK pieces it needs).
4. Click Run, or Build → Build Bundle(s)/APK(s) → Build APK(s) to get an installable `.apk`.
5. Build → Generate Signed Bundle/APK if you want a signed release build.

If you'd rather not install Android Studio, `cd` into this project on any machine with
the Android command-line SDK tools and run `./gradlew assembleRelease` (you'll need to
generate the Gradle wrapper jar once with `gradle wrapper` if it's not already present).

## Project layout

```
app/src/main/java/.../MainActivity.kt   -- UI logic: book/chapter spinners, prev/next
app/src/main/java/.../BibleData.kt      -- loads index.json + per-book JSON from assets
app/src/main/assets/bible/index.json    -- 66 books: code, Indonesian name, chapter count
app/src/main/assets/bible/<code>.json   -- per-book verse text (you add these)
scripts/generate_index.py               -- (re)generates index.json
scripts/convert_txt_to_json.py          -- converts your raw_text/*.txt into the app's format
```

## Extending it later

Kept deliberately bare-bones per your spec (navigation only, no search/bookmarks). If
you want them later, the natural next additions are:
- A `SharedPreferences` entry to remember last book/chapter on launch
- A `SearchView` that linear-scans the cached `JSONObject`s for a query string
- A font-size slider feeding into `textBody.textSize`

All straightforward additions to `MainActivity.kt` whenever you want them — just ask.
