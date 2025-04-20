package com.example.flightdemo.contract

import com.example.flightdemo.response.FlightResponse

interface FlightContract {
    interface View {
        fun displayFlightData(data: FlightResponse?)
        fun displayError(error: String)
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter {
        fun setView(view: View)
        fun detachView()
        fun startFetching(airFlyLine: Int, airFlyIO: Int)
        fun stopFetching()
    }
}