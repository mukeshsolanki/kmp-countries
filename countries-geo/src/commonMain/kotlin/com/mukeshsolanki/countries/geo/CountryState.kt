package com.mukeshsolanki.countries.geo

/** A first-level subdivision of a country, with its city names. */
public data class CountryState(
    /** State name. Names can repeat within a country, so use [code] to tell them apart. */
    val name: String,
    /** State code, unique within its country. */
    val code: String,
    /** Kind of subdivision in the source's own words, such as province, region or county, or null when unknown. */
    val type: String?,
    /** City names, empty when the source has none. Includes towns, because the source doesn't distinguish them. */
    val cities: List<String>,
)

/** A city found by a search. */
public data class CityMatch(
    /** State containing the city, or null for a city listed directly under the country. */
    val state: CountryState?,
    /** City name. */
    val city: String,
)
