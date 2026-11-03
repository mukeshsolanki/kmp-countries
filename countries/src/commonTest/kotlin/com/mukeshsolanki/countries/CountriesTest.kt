package com.mukeshsolanki.countries

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Verifies parsing, normalization and lookups against the real generated data. */
class CountriesTest {

    /** Returns the country with this ISO3 code, failing the test when it is missing. */
    private fun country(iso3: String): Country = assertNotNull(Countries.byIso3(iso3))

    /** All 250 records parse with unique codes. */
    @Test
    fun totalsMatchGenerator() {
        assertEquals(250, Countries.all.size)
        assertEquals(250, Countries.all.map { it.iso2 }.toSet().size)
        assertEquals(250, Countries.all.map { it.iso3 }.toSet().size)
    }

    /** ISO lookups ignore case. */
    @Test
    fun isoLookupsIgnoreCase() {
        assertEquals("United States", Countries.byIso2("us")?.name)
        assertEquals("United States", Countries.byIso2("US")?.name)
        assertEquals("United States", Countries.byIso3("USA")?.name)
        assertEquals("United States", Countries.byIso3("usa")?.name)
    }

    /** Unknown codes return null. */
    @Test
    fun unknownCodesReturnNull() {
        assertNull(Countries.byIso2("ZZ"))
        assertNull(Countries.byIso3("ZZZ"))
    }

    /** Phone lookup is exact, accepts an optional plus sign and returns every sharing country. */
    @Test
    fun phoneLookupIsExact() {
        val withoutPlus = Countries.byPhoneCode("1")
        assertEquals("+1", country("JAM").phoneCode)
        assertEquals(25, withoutPlus.size)
        assertTrue(country("USA") in withoutPlus)
        assertTrue(country("JAM") in withoutPlus)
        assertEquals(withoutPlus, Countries.byPhoneCode("+1"))
        assertFalse(country("BHR") in withoutPlus)
        assertEquals(listOf("IND"), Countries.byPhoneCode("91").map { it.iso3 })
        assertTrue(Countries.byPhoneCode("999999").isEmpty())
    }

    /** Currency lookup ignores case and returns every user of the currency. */
    @Test
    fun currencyLookupFindsAllUsers() {
        val users = Countries.byCurrency("usd")
        assertEquals(17, users.size)
        assertTrue(country("USA") in users)
        assertTrue(Countries.byCurrency("ZZZ").isEmpty())
    }

    /** An empty capital field becomes null only for the five known territories. */
    @Test
    fun emptyCapitalIsNull() {
        val missing = Countries.all.filter { it.capital == null }.map { it.iso3 }.toSet()
        assertEquals(setOf("ATA", "BVT", "HMD", "TKL", "UMI"), missing)
        assertNull(country("ATA").capital)
    }

    /** USA has its own position, guarding against the Vatican coordinates of the previous data source. */
    @Test
    fun coordinatesBelongToTheirCountry() {
        val usa = country("USA")
        assertEquals(38.0, usa.latitude)
        assertEquals(-97.0, usa.longitude)
        assertNotEquals(country("VAT").latitude, usa.latitude)
        assertNotEquals(country("VAT").longitude, usa.longitude)
    }

    /** Every country has coordinates in range. */
    @Test
    fun coordinatesAreInRange() {
        assertTrue(Countries.all.all { it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0 })
    }

    /** Values that older datasets had out of date are current. */
    @Test
    fun valuesAreUpToDate() {
        assertEquals("EUR", country("HRV").currencyCode)
        assertEquals("EUR", country("BGR").currencyCode)
        assertEquals("ZWG", country("ZWE").currencyCode)
        assertEquals("Eswatini", country("SWZ").name)
        assertEquals("North Macedonia", country("MKD").name)
        assertEquals("Kyiv", country("UKR").capital)
        assertEquals("Gitega", country("BDI").capital)
    }

    /** Dollar signs and emoji survive the generated string constants. */
    @Test
    fun symbolsAreIntact() {
        assertEquals("$", country("DOM").currencySymbol)
        assertEquals("J$", country("JAM").currencySymbol)
        assertEquals(69, Countries.all.count { '$' in it.currencySymbol })
        assertEquals("🇺🇸", country("USA").flagEmoji)
    }

    /** Non-Latin translations and the source's language tags are readable, with Breton kept apart from pt-BR. */
    @Test
    fun translationsAreIntact() {
        assertEquals("日本", country("JPN").translations["ja"])
        assertEquals("대한민국", country("KOR").translations["ko"])
        assertEquals("中国", country("CHN").translations["zh-CN"])
        assertEquals("Alemanha", country("DEU").translations["pt-BR"])
        assertEquals("Alamagn", country("DEU").translations["br"])
    }

    /** Localized names use the exact tag and fall back to the source name. */
    @Test
    fun localizedNameFallsBack() {
        assertEquals("Deutschland", country("DEU").localizedName("de"))
        assertEquals("Germany", country("DEU").localizedName("xx"))
    }

    /** Search ignores case and covers names and translations. */
    @Test
    fun searchMatchesNamesAndTranslations() {
        assertEquals(listOf("New Zealand"), Countries.search("ZEALAND").map { it.name })
        assertEquals(listOf("Germany"), Countries.search("allemagne").map { it.name })
        assertEquals(listOf("Japan"), Countries.search("日本").map { it.name })
        assertTrue(Countries.search("no such country").isEmpty())
    }

    /** Surrounding whitespace is ignored, and a blank query returns every country. */
    @Test
    fun searchTrimsQuery() {
        assertEquals(listOf("New Zealand"), Countries.search("  zealand ").map { it.name })
        assertEquals(Countries.all, Countries.search(""))
        assertEquals(Countries.all, Countries.search("   "))
    }
}
