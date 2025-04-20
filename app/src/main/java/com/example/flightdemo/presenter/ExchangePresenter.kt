package com.example.flightdemo.presenter

import com.example.flightdemo.contract.ExchangeContract
import com.example.flightdemo.model.ExchangeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ExchangePresenter(private val repository: ExchangeModel) : ExchangeContract.Presenter {

    private var view: ExchangeContract.View? = null
    private val presenterJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + presenterJob)

    override fun attachView(view: ExchangeContract.View) {
        this.view = view
    }

    override fun setView() {
        view = null
        presenterJob.cancel() // 取消所有正在執行的協程工作
    }

    override fun fetchExchangeRates(baseCurrency: String) {
        // 每 10 秒從 API 取得資料
        scope.launch {
            var firstFetch = true
            while (isActive) {
                if (firstFetch) view?.showLoading()
                // 呼叫 repository 取得匯率資料
                val response = repository.fetchExchangeRates(baseCurrency)

                if (firstFetch) {
                    view?.hideLoading()
                    firstFetch = false
                }

                if (response != null) {
                    view?.displayExchangeRates(response)
                } else {
                    view?.displayError("無法取得匯率資訊!")
                }
                delay(10_000)
            }
        }
    }

    override fun stopFetching() {
        presenterJob.cancelChildren()
    }
}
