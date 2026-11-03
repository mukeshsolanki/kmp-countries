package com.mukeshsolanki.countries

import com.mukeshsolanki.countries.generated.countryChunks

/** Separates country records in the encoded data. */
private const val RECORD_SEPARATOR = '\n'

/** Separates the fields of a record. */
private const val FIELD_SEPARATOR = '\u001F'

/** Separates the items of a list field. */
private const val ITEM_SEPARATOR = '\u001E'

/** Number of items that encode one translation: a tag and its value. */
private const val TRANSLATION_ITEMS = 2

/** Offline country data. It is parsed once on first access and is safe to use from any thread. */
public object Countries {
    /** Every country, in source order. */
    public val all: List<Country> by lazy {
        countryChunks().joinToString("").split(RECORD_SEPARATOR).map(::parseCountry)
    }

    /** Returns the country with this ISO alpha-2 code, ignoring case, or null. */
    public fun byIso2(code: String): Country? = all.firstOrNull { it.iso2.equals(code, ignoreCase = true) }

    /** Returns the country with this ISO alpha-3 code, ignoring case, or null. */
    public fun byIso3(code: String): Country? = all.firstOrNull { it.iso3.equals(code, ignoreCase = true) }

    /**
     * Returns the countries with exactly this phone code, given as `+1` or `1`.
     * `+1` returns the US, Jamaica and every other `+1` country, and `+18` returns nothing.
     */
    public fun byPhoneCode(code: String): List<Country> {
        val normalized = "+${code.removePrefix("+")}"
        return all.filter { it.phoneCode == normalized }
    }

    /** Returns the countries using this ISO 4217 currency code, ignoring case. */
    public fun byCurrency(code: String): List<Country> = all.filter { it.currencyCode.equals(code, ignoreCase = true) }

    /**
     * Returns the countries whose name or a translation contains the trimmed query, ignoring case.
     * A blank query returns every country, which suits a search field that has not been typed in yet.
     */
    public fun search(query: String): List<Country> {
        val term = query.trim()
        return all.filter { country ->
            country.name.contains(term, ignoreCase = true) ||
                country.translations.values.any { it.contains(term, ignoreCase = true) }
        }
    }
}

/** Decodes one country record, reading its fields in their encoded order. */
private fun parseCountry(record: String): Country {
    val fields = record.split(FIELD_SEPARATOR).iterator()
    return Country(
        name = fields.next(),
        iso2 = fields.next(),
        iso3 = fields.next(),
        phoneCode = fields.next(),
        capital = fields.next().takeIf { it.isNotEmpty() },
        currencyCode = fields.next(),
        currencyName = fields.next(),
        currencySymbol = fields.next(),
        flagEmoji = fields.next(),
        latitude = fields.next().toDouble(),
        longitude = fields.next().toDouble(),
        translations = parseTranslations(fields.next()),
    )
}

/** Decodes a list field of alternating language tags and values, where an empty field means no translations. */
private fun parseTranslations(field: String): Map<String, String> =
    if (field.isEmpty()) {
        emptyMap()
    } else {
        field.split(ITEM_SEPARATOR).chunked(TRANSLATION_ITEMS).associate { (tag, value) -> tag to value }
    }
