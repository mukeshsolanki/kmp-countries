# kmp-countries

Offline countries, states and cities for Android, iOS, Kotlin Multiplatform and Compose Multiplatform. All data ships inside the library, so there's no network access, init call, permissions or asset setup at runtime.

- **250 countries:** ISO codes, phone code, capital, currency, flag emoji, coordinates, and names in up to 19 languages.
- **5,327 states, regions, provinces and similar**, each with a type where the source gives one.
- **149,055 cities and towns.**

## Install

Two libraries are available. Use `kmp-countries` if you only need countries. Add `kmp-countries-geo` for states and cities.

### Android, KMP and Compose Multiplatform

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://mukeshsolanki.github.io/kmp-countries")
    }
}

// build.gradle.kts
implementation("com.mukeshsolanki:kmp-countries:<version>")
implementation("com.mukeshsolanki:kmp-countries-geo:<version>") // optional, includes kmp-countries
```

Requirements:

- **Android:** `minSdk` 24 and Kotlin 2.2 or newer. Tested with AGP 9.5's built-in Kotlin (2.2.10).
- **KMP and Compose Multiplatform with iOS targets:** iOS 15 and Kotlin 2.4 or newer. Kotlin 2.3 can't read the iOS artifacts.

### Swift (Swift Package Manager)

Add `https://github.com/mukeshsolanki/kmp-countries` as a package, then choose **one** product:

- `KmpCountries`: countries only.
- `KmpCountriesGeo`: countries, states and cities.

`KmpCountriesGeo` already contains everything in `KmpCountries`. Don't link both, because each would have its own incompatible `Country` type. iOS simulators on Intel Macs aren't supported.

## Usage

```kotlin
import com.mukeshsolanki.countries.Countries
import com.mukeshsolanki.countries.geo.directCities
import com.mukeshsolanki.countries.geo.searchCities
import com.mukeshsolanki.countries.geo.state
import com.mukeshsolanki.countries.geo.states

val all = Countries.all
val us = Countries.byIso2("us")!!                // case-insensitive
val japan = Countries.byIso3("JPN")
val nanp = Countries.byPhoneCode("+1")           // exact match, returns 25 countries
val euro = Countries.byCurrency("EUR")
val matches = Countries.search("german")         // name and translations

us.flagEmoji                                     // 🇺🇸
Countries.byIso2("DE")!!.localizedName("pt-BR")  // Alemanha, falls back to the English name

val states = us.states()
val alabama = us.state("AL")                     // name "Alabama", type "state"
val cities = alabama!!.cities
val found = us.searchCities("Springfield")       // each match has a state and a city
```

```swift
import KmpCountriesGeo

let us = Countries.shared.byIso2(code: "US")
let states = us?.states()
```

Calls are synchronous and thread-safe. Data is parsed on first use, and geo data is parsed per country.

## What the data does and doesn't include

- **States include regions and provinces.** There's one level below country. `CountryState.type` holds the source's label, with 105 distinct values such as `province`, `region` or `county`. It's plain text and null for a few states.
- **Cities include towns.** The source doesn't label which is which.
- **Some places have no sub-levels.** 21 countries and territories have no states, and 599 states have no cities.
- **Western Sahara** has 3 cities and no states. Use `directCities()` for these. In `searchCities`, they're returned with a null `state`.
- **Duplicate names are expected.** Different states can share a name in one country, such as two named Minsk in Belarus. Use `code` to tell them apart. Repeated city names within one state appear once.
- **Phone codes are country codes.** Jamaica is `+1`, not `+1876`.
- **Search is not accent-insensitive.** "Turkiye" won't match "Türkiye".
- **Language tags are exact.** Use the source's tags, such as `zh-CN`, `pt-BR` or `br` (Breton).
- **Flags are emoji only.**

## Size

Measured on the current data. These are artifact sizes, not the amount added to an installed app, which wasn't measured.

| Artifact | `kmp-countries` | `kmp-countries-geo` |
|---|---|---|
| Android AAR | 53 KB | 795 KB (plus `kmp-countries`) |
| XCFramework download, zipped | 1.8 MB | 3.7 MB (includes countries) |
| Framework binary, per device slice | 3.0 MB | 6.7 MB (includes countries) |

## Data freshness

The data is a pinned snapshot of [dr5hn/countries-states-cities-database](https://github.com/dr5hn/countries-states-cities-database) release `v5.2.0-export.1`, from November 2026. It doesn't change until the library is released with a new snapshot.

To update it:

1. Change `RELEASE`, `COMMIT` and the `EXPECTED_*` totals in `scripts/generate_data.py`.
2. Run `python3 -I scripts/generate_data.py`.
3. Copy that release's `LICENSE` and `.github/licenses/*` into `licenses/`, and update `NOTICE`.
4. Update the test expectations and the totals in this README.

## Releasing

Before the first release, push this repo as public and set GitHub Pages to serve the `gh-pages` branch. Pages can only be selected once the first release has created that branch.

Run the **Release** workflow in GitHub Actions with a version such as `0.1.0`. It runs the tests, publishes to the Maven repository on `gh-pages`, builds the XCFrameworks, updates `Package.swift`, tags the commit and creates the GitHub Release.

## License

- **Library code:** [MIT](LICENSE).
- **Bundled data:** [ODbL-1.0](licenses/ODbL-1.0.txt). See [NOTICE](NOTICE) for attribution and the changes made.

**If you ship an app or a product that uses this library**, the data license applies to you. Credit the data source, for example:

> Contains data from the Countries States Cities Database, © dr5hn and contributors, licensed under ODbL 1.0.

Upstream also asks that its source notices stay with copies of the data, so keep the `licenses/` files with anything you redistribute. They're attached to each GitHub Release as `licenses.zip`. This isn't legal advice. The upstream project says some of its sources' redistribution basis is still under review, so check the terms if this matters for your use.
