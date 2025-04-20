package com.example.flightdemo.presenter

import com.example.flightdemo.contract.FlightContract
import com.example.flightdemo.model.FlightModel
import kotlinx.coroutines.*

class FlightPresenter(
    private val repository: FlightModel
) : FlightContract.Presenter {

    private var view: FlightContract.View? = null
    private val presenterJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + presenterJob)

    override fun setView(view: FlightContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        presenterJob.cancel() // 取消所有正在執行的協程工作
    }

    override fun startFetching(airFlyLine: Int, airFlyIO: Int) {
        // 每 10 秒從 API 取得資料
        scope.launch {
            var firstFetch = true
            while (isActive) {
                if (firstFetch) view?.showLoading()

                val data =
//                    withContext(Dispatchers.IO) {
                    repository.fetchFlightData(airFlyLine, airFlyIO)
//                }

                if (firstFetch) {
                    view?.hideLoading()
                    firstFetch = false
                }

                if (data != null) {
                    view?.displayFlightData(data)
                } else {
                    view?.displayError("無法取得航班資訊!")
                }
                delay(10_000)
            }
        }
    }

    override fun stopFetching() {
        presenterJob.cancelChildren()
    }
}
