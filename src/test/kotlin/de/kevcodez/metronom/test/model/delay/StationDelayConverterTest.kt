package de.kevcodez.metronom.test.model.delay

import com.fasterxml.jackson.databind.ObjectMapper
import de.kevcodez.metronom.converter.StationDelayConverter
import de.kevcodez.metronom.model.delay.StationDelay
import de.kevcodez.metronom.provider.StationProvider
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

class StationDelayConverterTest {

    private val stationDelayConverter = StationDelayConverter()

    @Test
    fun shouldConvertJsonDepartures() {
        val json =
            "{'name':'Cuxhaven','stand':'10:10 PM','standsek':'22:10:04','abfahrt':[{'zeit':'23:15','zug':'81942','ziel':'Hamburg','prognose':'pünktlich','prognosemin':'0'},{'zeit':'00:15','zug':'81944','ziel':'Rotenburg','prognose':'pünktlich','prognosemin':'5'}]}"
                .replace("'".toRegex(), "\"")

        val station = StationProvider.findStationByName("Cuxhaven")
        val stationDelay = stationDelayConverter.convert(station!!, objectMapper.readTree(json))

        assertThat<StationDelay>(stationDelay, `is`(notNullValue()))
        assertThat(stationDelay!!.departures.size, `is`(2))

        val firstDeparture = stationDelay.departures[0]
        assertThat(firstDeparture.time.toString(), `is`("23:15"))
        assertThat<String>(firstDeparture.train, `is`("81942"))
        assertThat<String>(firstDeparture.targetStation.name, `is`("Hamburg"))
        assertThat(firstDeparture.delayInMinutes, `is`(0))

        val secondDeparture = stationDelay.departures[1]
        assertThat(secondDeparture.time.toString(), `is`("00:15"))
        assertThat<String>(secondDeparture.train, `is`("81944"))
        assertThat<String>(secondDeparture.targetStation.name, `is`("Rotenburg"))
        assertThat(secondDeparture.delayInMinutes, `is`(5))
    }

    @Test
    fun shouldParseTrack() {
        val json =
            "{'name':'Cuxhaven','stand':'10:10 PM','standsek':'22:10:04','abfahrt':[{'zeit':'22:33','zug':'82835','ziel':'Hamburg','prognose':'pünktlich, heute Gleis 8','prognosemin':'0','gleiswechsel':'8B'}]}"
                .replace("'".toRegex(), "\"")

        val station = StationProvider.findStationByName("Cuxhaven")
        val stationDelay = stationDelayConverter.convert(station!!, objectMapper.readTree(json))

        assertThat<StationDelay>(stationDelay, `is`(notNullValue()))
        assertThat(stationDelay!!.departures.size, `is`(1))

        assertThat<String>(stationDelay.departures[0].track, `is`("8"))
    }

    companion object {

        private val objectMapper = ObjectMapper()
    }

}
