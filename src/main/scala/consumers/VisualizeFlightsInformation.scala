package consumers

import com.datawizards.splot.api.implicits._
import com.datawizards.splot.model.ImageFormats
import repositories.cassandra.FlightSummaryCassandraRepository

object VisualizeFlightsInformation {

  def visualizeRatioBetweenAllFlightsAndLateFlights(): Unit = {

    val flightSummaryRepo = new FlightSummaryCassandraRepository
    val flightSummaries = flightSummaryRepo.selectAll()

    //    flightSummaries.foreach { i => {
    //
    //      Seq(("lateFlights", i.totalLateFlights - i.totalLateFlightsDueToWeather),
    //        ("lateFlightsDueToWeather", i.totalLateFlightsDueToWeather.toInt),
    //        ("flightsOnTime", i.totalFlights - i.totalLateFlights))
    //        .buildPlot().pie()
    //        .title("Airport code: " + i.airportCode + "; date: " + i.date).display()
    //    }
    //    }
    //
    val flightsByAirport = flightSummaries.groupBy(_.airportCode)
    val test = flightsByAirport.mapValues(_.size)
    while (true) {
      flightsByAirport.foreach(
        f => {
          var dateFlightStatusAggregated = Map[(String, String), Integer]()
          val flightsByDateAndLateness = f._2.foreach(i => {
            dateFlightStatusAggregated += (i.date, "Total Flights") -> i.totalFlights
            dateFlightStatusAggregated += (i.date, "Flights On Time") -> (i.totalFlights - i.totalLateFlights)
            dateFlightStatusAggregated += (i.date, "Total Late Flights") -> i.totalLateFlights
            dateFlightStatusAggregated += (i.date, "Late Flights Due To Weather") -> i.totalLateFlightsDueToWeather
          })

          dateFlightStatusAggregated.buildPlot().colsBy(_._1._1 + " (" + f._1 + ")")
            .bar(x => x._1._2, x => x._2.toInt).size(1600, 300).save(s"./screenshots/image${System.currentTimeMillis()}.png", ImageFormats.PNG)
        })
      Thread.sleep(5000)
    }

  }
}
