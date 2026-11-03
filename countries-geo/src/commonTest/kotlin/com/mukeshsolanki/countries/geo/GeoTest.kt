package com.mukeshsolanki.countries.geo

import com.mukeshsolanki.countries.Countries
import com.mukeshsolanki.countries.Country
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Verifies state and city parsing and lookups against the real generated data. */
class GeoTest {

    /** Returns the country with this ISO3 code, failing the test when it is missing. */
    private fun country(iso3: String): Country = assertNotNull(Countries.byIso3(iso3))

    /** Every country parses and the totals match the generator. */
    @Test
    fun totalsMatchGenerator() {
        val states = Countries.all.map { it.states() }
        val citiesInStates = states.sumOf { list -> list.sumOf { it.cities.size } }
        val directCities = Countries.all.sumOf { it.directCities().size }
        assertEquals(229, states.count { it.isNotEmpty() })
        assertEquals(21, states.count { it.isEmpty() })
        assertEquals(5327, states.sumOf { it.size })
        assertEquals(149052, citiesInStates)
        assertEquals(3, directCities)
        assertEquals(149055, citiesInStates + directCities)
    }

    /** The largest country keeps every record, so no chunk boundary cut one. */
    @Test
    fun usaSurvivesChunkJoins() {
        val states = country("USA").states()
        assertEquals(60, states.size)
        assertEquals(17282, states.sumOf { it.cities.size })
        assertEquals("Alabama", states.first().name)
        assertEquals("Wyoming", states.last().name)
        assertTrue(states.all { it.name.isNotEmpty() && it.code.isNotEmpty() })
    }

    /** Spot-checked states hold their type and full, ordered city lists. */
    @Test
    fun usaStatesHaveFullCityLists() {
        val alabama = assertNotNull(country("USA").state("AL"))
        assertEquals("Alabama", alabama.name)
        assertEquals("state", alabama.type)
        assertEquals(309, alabama.cities.size)
        assertEquals("Abbeville", alabama.cities.first())
        assertEquals("York", alabama.cities.last())
        val california = assertNotNull(country("USA").state("CA"))
        assertEquals(1065, california.cities.size)
        assertEquals("Acalanes Ridge", california.cities.first())
        assertEquals("Yucca Valley", california.cities.last())
        assertTrue("San Francisco" in california.cities)
        val texas = assertNotNull(country("USA").state("TX"))
        assertEquals(1019, texas.cities.size)
        assertEquals("Zapata", texas.cities.last())
    }

    /** Duplicate state names stay separate and are told apart by code. */
    @Test
    fun duplicateStateNamesAreKept() {
        val belarus = country("BLR")
        assertEquals(setOf("MI", "HM"), belarus.states().filter { it.name == "Minsk" }.map { it.code }.toSet())
        assertEquals("oblast", belarus.state("MI")?.type)
        assertEquals(101, belarus.state("MI")?.cities?.size)
        assertEquals("city", belarus.state("HM")?.type)
        assertEquals(9, belarus.state("HM")?.cities?.size)
    }

    /** A state type can hold several words. */
    @Test
    fun stateTypeIsFreeText() {
        val aberdeenshire = assertNotNull(country("GBR").state("ABD"))
        assertEquals("Aberdeenshire", aberdeenshire.name)
        assertEquals("council area", aberdeenshire.type)
    }

    /** An empty type field becomes null while the fields after it still parse. */
    @Test
    fun emptyTypeIsNull() {
        val state = assertNotNull(country("CYM").state("01"))
        assertEquals("Grand Cayman", state.name)
        assertNull(state.type)
    }

    /** Unknown state codes return null. */
    @Test
    fun unknownStateCodeReturnsNull() {
        assertNull(country("USA").state("ZZ"))
    }

    /** A country without states or direct cities is empty, with no blank entries. */
    @Test
    fun countryWithoutDataIsEmpty() {
        assertTrue(country("ATA").states().isEmpty())
        assertTrue(country("ATA").directCities().isEmpty())
        assertNull(country("ATA").state("01"))
        assertTrue(country("ATA").searchCities("").isEmpty())
    }

    /** A country unknown to the data has no states. */
    @Test
    fun unknownCountryIsEmpty() {
        assertTrue(country("USA").copy(iso3 = "ZZZ").states().isEmpty())
    }

    /** A state without cities has an empty list, not one blank city. */
    @Test
    fun stateWithoutCitiesIsEmpty() {
        val state = assertNotNull(country("ALA").state("12"))
        assertEquals("Brändö", state.name)
        assertTrue(state.cities.isEmpty())
    }

    /** Direct cities exist outside any state and are found by search with a null state. */
    @Test
    fun directCitiesAreSearchable() {
        val westernSahara = country("ESH")
        assertTrue(westernSahara.states().isEmpty())
        assertEquals(listOf("Gueltat Zemmour", "Imlili", "Smara"), westernSahara.directCities())
        assertEquals(listOf(CityMatch(null, "Smara")), westernSahara.searchCities("Smara"))
        assertEquals(3, westernSahara.searchCities("").size)
        assertTrue(country("USA").directCities().isEmpty())
    }

    /** City search ignores case and names the state of each match. */
    @Test
    fun cityMatchesCarryTheirState() {
        val matches = country("USA").searchCities("SAN FRAN")
        assertEquals(
            setOf("City and County of San Francisco", "San Francisco", "South San Francisco"),
            matches.map { it.city }.toSet(),
        )
        assertTrue(matches.all { it.state?.code == "CA" })
        assertEquals("75C", country("FRA").searchCities("Paris").first { it.city == "Paris" }.state?.code)
        assertTrue(country("USA").searchCities("no such city").isEmpty())
    }

    /** Surrounding whitespace is ignored, and a blank query returns every city. */
    @Test
    fun citySearchTrimsQuery() {
        assertEquals(country("USA").searchCities("san fran"), country("USA").searchCities(" san fran  "))
        assertEquals(17282, country("USA").searchCities(" ").size)
    }
}
