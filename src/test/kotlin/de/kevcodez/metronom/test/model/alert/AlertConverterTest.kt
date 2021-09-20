package de.kevcodez.metronom.test.model.alert

import com.fasterxml.jackson.databind.ObjectMapper
import de.kevcodez.metronom.converter.AlertConverter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalTime

class AlertConverterTest {

    private var alertConverter: AlertConverter? = null

    @BeforeEach
    fun init() {
        alertConverter = AlertConverter()
    }

    @Test
    fun shouldConvertAlert() {
        val json = """{
                "netz": "RE4",
                "netz_orig": "Hamburg-Uelzen",
                "linie": "RE4",
                "bhfvon": "Hamburg-Harburg",
                "bhfnach": "Hamburg Hbf",
                "bhfvonid": "AHAR",
                "bhfnachid": "AH",
                "text": "82038 Bremen Hbf → Hamburg Hbf hat in Hamburg Hbf (planmäßig 21:43) +15 min.  Grund: Personen im Gleis.",
                "datum": "18.09.2021",
                "time_von": "21:43",
                "time_bis": "23:43"
                }"""

        val alert = alertConverter!!.convert(objectMapper.readTree(json))
        assertThat(alert, `is`(notNullValue()))

        assertThat<String>(
            alert.message,
            `is`("82038 Bremen Hbf → Hamburg Hbf hat in Hamburg Hbf (planmäßig 21:43) +15 min.  Grund: Personen im Gleis.")
        )
        assertThat(alert.creationDate.toString(), `is`("2021-09-18"))
        assertThat(alert.timeFrom.toString(), `is`("21:43"))
        assertThat(alert.timeTo.toString(), `is`("23:43"))
    }

    @Test
    fun shouldParsePlannedDeparture() {
        val plannedDeparture = alertConverter!!.parsePlannedDeparture("(planmäßige Abfahrt 11:09 Uhr)")

        assertThat<LocalTime>(plannedDeparture, `is`(LocalTime.of(11, 9)))
    }

    @Test
    fun shouldParsePlannedDeparture2() {
        val plannedDeparture = alertConverter!!.parsePlannedDeparture("(planmäßige Abfahrt 9:11 Uhr)")

        assertThat<LocalTime>(plannedDeparture, `is`(LocalTime.of(9, 11)))
    }

    companion object {

        private val objectMapper = ObjectMapper()
    }

}
