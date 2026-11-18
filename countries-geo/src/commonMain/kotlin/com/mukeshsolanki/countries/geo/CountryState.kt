package com.mukeshsolanki.countries.geo

/** A city or town with its position and IANA time zone. */
public data class City(
    /** City name. */
    val name: String,
    /** Latitude in degrees, rounded to 4 decimals. */
    val latitude: Double,
    /** Longitude in degrees, rounded to 4 decimals. */
    val longitude: Double,
    /** IANA time zone such as Asia/Kolkata. */
    val timeZone: String,
)

/** A first-level subdivision of a country, with its cities. */
public data class CountryState(
    /** State name. Names can repeat within a country, so use [code] to tell them apart. */
    val name: String,
    /** State code, unique within its country. */
    val code: String,
    /** Kind of subdivision in the source's own words, such as province, region or county, or null when unknown. */
    val type: String?,
    /** Cities, empty when the source has none. Includes towns, because the source doesn't distinguish them. */
    val cities: List<City>,
)

/** A city found by a search. */
public data class CityMatch(
    /** State containing the city, or null for a city listed directly under the country. */
    val state: CountryState?,
    /** The matched city. */
    val city: City,
)
