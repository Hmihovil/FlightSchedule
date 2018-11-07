package io.capsella.flightschedule.model

class FlightSchedule(id: Int, duration: String, airportCodeDeparture: String, localTimeDeparture: String, airportCodeArrival: String, localTimeArrival: String, flightNumber: Int) {

    var id: Int = id

    var duration: String = duration

    var airportCodeDeparture: String = airportCodeDeparture

    var localTimeDeparture: String = localTimeDeparture

    var airportCodeArrival: String = airportCodeArrival

    var localTimeArrival: String = localTimeArrival

    var flightNumber: Int = flightNumber
}