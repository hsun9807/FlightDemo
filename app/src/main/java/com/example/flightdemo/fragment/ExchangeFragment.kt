package com.example.flightdemo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.flightdemo.response.ExchangeItem
import com.example.flightdemo.response.ExchangeResponse
import com.example.flightdemo.R
import com.example.flightdemo.adapter.ExchangeAdapter
import com.example.flightdemo.contract.ExchangeContract
import com.example.flightdemo.model.ExchangeModel
import com.example.flightdemo.presenter.ExchangePresenter
import java.text.DecimalFormat

class ExchangeFragment : Fragment(), ExchangeContract.View {

    private lateinit var tvDisplay: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var exchangeAdapter: ExchangeAdapter
    private lateinit var presenter: ExchangeContract.Presenter
    private var originalExchangeItems: List<ExchangeItem> = emptyList()
    private var defaultResult: Double = 1000.0
    private var defaultCurrency: String = "EUR"
    val formatter = DecimalFormat("#,##0.00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_exchange, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDisplay = view.findViewById(R.id.tvDisplay)
        recyclerView = view.findViewById(R.id.rvExchange)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        exchangeAdapter = ExchangeAdapter(emptyList())
        recyclerView.adapter = exchangeAdapter

        // 設定 item 點選事件
        exchangeAdapter.onItemClick = { exchangeItem ->
            // 呼叫顯示計算機
            val calcSheet = CalcBottomFragment()
            calcSheet.onCalculatorResult = { result ->
                // 將計算結果轉成 Double，若失敗則設為 1000.0（不改變原始匯率）
                defaultResult = result.toDoubleOrNull() ?: 1000.0
                // 利用原始資料計算每個貨幣的新匯率
                val updatedList = originalExchangeItems.map { item ->
                    item.copy(money = item.rate * defaultResult)
                }
                tvDisplay.text = formatter.format(defaultResult)
                // 更新 Adapter 資料
                exchangeAdapter.updateData(updatedList)
            }
            calcSheet.show(childFragmentManager, "CalculatorBottomSheet")
        }

        exchangeAdapter.onItemLongClick = { exchangeItem ->
//            defaultCurrency = exchangeItem.currency
//            presenter.stopFetching()
//            presenter.fetchExchangeRates(defaultCurrency)
            AlertDialog.Builder(requireContext())
//                .setTitle("訊息")
                .setMessage("是否要將 ${exchangeItem.currency} 設為主要貨幣？")
                .setPositiveButton("是") { _, _ ->
                    defaultCurrency = exchangeItem.currency
                    presenter.stopFetching()
                    presenter.fetchExchangeRates(defaultCurrency)
                }
                .setNegativeButton("否", null)
                .show()
        }

        tvDisplay.text = formatter.format(defaultResult)

        // 初始化 presenter 並 attach view
        presenter = ExchangePresenter(ExchangeModel())
        presenter.attachView(this)
        // 呼叫 presenter 取得匯率資料
        presenter.fetchExchangeRates(defaultCurrency)
    }

    override fun displayExchangeRates(response: ExchangeResponse) {
        // 將回傳的 Map 轉換成列表物件
        originalExchangeItems = response.data?.map { (currency, rate) ->
            ExchangeItem(currency, rate,rate * defaultResult)
        } ?: emptyList()

        // 直接更新 Adapter 顯示原始匯率
        exchangeAdapter.updateData(originalExchangeItems)
    }

    override fun displayError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.setView()
    }


    override fun showLoading() {
        (activity?.findViewById<LottieAnimationView>(R.id.loadingView))?.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        (activity?.findViewById<LottieAnimationView>(R.id.loadingView))?.visibility = View.GONE
    }
}
