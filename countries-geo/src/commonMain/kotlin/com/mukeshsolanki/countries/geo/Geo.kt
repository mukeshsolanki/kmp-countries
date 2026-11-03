package com.mukeshsolanki.countries.geo

import com.mukeshsolanki.countries.Countries
import com.mukeshsolanki.countries.Country
import com.mukeshsolanki.countries.geo.generated.directCityChunks
import com.mukeshsolanki.countries.geo.generated.geoChunks

/** Separates state records in the encoded data. */
private const val RECORD_SEPARATOR = '\n'

/** Separates the fields of a record. */
private const val FIELD_SEPARATOR = '\u001F'

/** Separates the items of a list field. */
private const val ITEM_SEPARATOR = '\u001E'

/** States by ISO3 code. The map is built once, and each country is parsed once when first used. */
private val statesByIso3: Map<String, Lazy<List<CountryState>>> by lazy {
    Countries.all.associate { country -> country.iso3 to lazy { parseStates(country.iso3) } }
}

/** Returns this country's states, empty when the source has none. Parsed on first use, then cached. */
public fun Country.states(): List<CountryState> = statesByIso3[iso3]?.value.orEmpty()

/** Returns the state with exactly this code, or null. Codes are unique within a country. */
public fun Country.state(code: String): CountryState? = states().firstOrNull { it.code == code }

/** Returns the cities listed directly under this country, outside any state. */
public fun Country.directCities(): List<String> = parseCities(directCityChunks(iso3).joinToString(""))

/**
 * Returns this country's cities whose name contains the trimmed query, ignoring case, including direct cities.
 * A match has a null state for a direct city. A blank query returns every city in the country.
 */
public fun Country.searchCities(query: String): List<CityMatch> {
    val term = query.trim()
    val inStates = states().flatMap { state ->
        state.cities.filter { it.contains(term, ignoreCase = true) }.map { CityMatch(state, it) }
    }
    return inStates + directCities().filter { it.contains(term, ignoreCase = true) }.map { CityMatch(null, it) }
}

/** Decodes the states of one country, assembling only that country's text. */
private fun parseStates(iso3: String): List<CountryState> {
    val encoded = geoChunks(iso3).joinToString("")
    return if (encoded.isEmpty()) emptyList() else encoded.split(RECORD_SEPARATOR).map(::parseState)
}

/** Decodes one state record, reading its fields in their encoded order. */
private fun parseState(record: String): CountryState {
    val fields = record.split(FIELD_SEPARATOR).iterator()
    return CountryState(
        name = fields.next(),
        code = fields.next(),
        type = fields.next().takeIf { it.isNotEmpty() },
        cities = parseCities(fields.next()),
    )
}

/** Splits a list of city names, treating an empty field as no cities. */
private fun parseCities(field: String): List<String> =
    if (field.isEmpty()) emptyList() else field.split(ITEM_SEPARATOR)
