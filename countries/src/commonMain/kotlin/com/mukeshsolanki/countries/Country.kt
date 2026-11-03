package com.mukeshsolanki.countries

/**
 * A country with its codes, currency, flag, position and translated names.
 * The phone code is a plus sign followed by digits, and translation keys are language tags as the source writes them.
 */
public data class Country(
    /** English name. */
    val name: String,
    /** ISO 3166-1 alpha-2 code. */
    val iso2: String,
    /** ISO 3166-1 alpha-3 code. */
    val iso3: String,
    /** Dialing code, such as `+1`. Countries can share one. */
    val phoneCode: String,
    /** Capital city, null when the source has none. */
    val capital: String?,
    /** ISO 4217 currency code. */
    val currencyCode: String,
    /** English currency name. */
    val currencyName: String,
    /** Currency symbol. */
    val currencySymbol: String,
    /** Flag as an emoji. */
    val flagEmoji: String,
    /** Latitude in decimal degrees. */
    val latitude: Double,
    /** Longitude in decimal degrees. */
    val longitude: Double,
    /** Translated names keyed by language tag, such as `ko`, `zh-CN` or `pt-BR`. */
    val translations: Map<String, String>,
) {
    /** Returns the name for this exact language tag, or [name] when there is no such translation. */
    public fun localizedName(languageTag: String): String = translations[languageTag] ?: name
}
