package com.example.flightdemo.contract

import com.example.flightdemo.response.ExchangeResponse

interface ExchangeContract {
    interface View {
        fun displayExchangeRates(response: ExchangeResponse)
        fun displayError(error: String)
        fun showLoading()
        fun hideLoading()
    }
    interface Presenter {
        fun attachView(view: View)
        fun setView()
        fun fetchExchangeRates(baseCurrency: String)
        fun stopFetching()
    }
}
