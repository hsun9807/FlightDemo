package com.example.flightdemo.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.flightdemo.response.FlightResponse
import com.example.flightdemo.R
import com.example.flightdemo.adapter.FlightAdapter
import com.example.flightdemo.contract.FlightContract
import com.example.flightdemo.model.FlightModel
import com.example.flightdemo.presenter.FlightPresenter
import com.google.android.material.tabs.TabLayout

class FlightFragment : Fragment(), FlightContract.View {

    private lateinit var flightAdapter: FlightAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var presenter: FlightContract.Presenter
    private lateinit var spinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // 載入 HomeFragment 的佈局，例如 fragment_home.xml
        return inflater.inflate(R.layout.activity_airline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 RecyclerView 與 Adapter
        recyclerView = view.findViewById(R.id.rvFlights)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        flightAdapter = FlightAdapter(mutableListOf())
        recyclerView.adapter = flightAdapter

        spinner = view.findViewById(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.flight_type_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                var selected = parent?.getItemAtPosition(position).toString()
                presenter.stopFetching()
                presenter.startFetching(spinner.selectedItemPosition,tabLayout.selectedTabPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        // 設定 item 點選事件
        flightAdapter.onItemClick = { flight ->
            if (!flight.airLineUrl.isNullOrEmpty()) {
                // 使用 Fragment 的 context 呼叫自訂對話框函式
                requireContext().showCustomDialog(
                    icon = R.drawable.ico_info,
                    title = "提示",
                    message = "請問是否要前往網頁取得航空公司資訊?",
                    url = flight.airLineUrl
                ) { flightUrl ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(flightUrl))
                    startActivity(intent)
                }
            } else {
                Toast.makeText(requireContext(), "無法取得航班資訊網址", Toast.LENGTH_SHORT).show()
            }
        }

        // 初始化 TabLayout
        tabLayout = view.findViewById(R.id.tabLayoutFlight)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                presenter.stopFetching()
                presenter.startFetching(spinner.selectedItemPosition,tabLayout.selectedTabPosition)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 初始化 Presenter，並 attach view
        presenter = FlightPresenter(FlightModel())
        presenter.setView(this)
        // 初次載入時可以啟動抓取資料，例如預設 國際線、起飛航班
        presenter.startFetching(0,0)
    }

    override fun displayFlightData(data: FlightResponse?) {
        // 更新 adapter 資料
        data?.instantSchedule?.let { flightAdapter.updateData(tabLayout.selectedTabPosition, it) }
    }

    override fun displayError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun showLoading() {
        (activity?.findViewById<LottieAnimationView>(R.id.loadingView))?.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        (activity?.findViewById<LottieAnimationView>(R.id.loadingView))?.visibility = View.GONE
    }

    fun Context.showCustomDialog(
        icon: Int,
        title: String,
        message: String,
        url: String,
        onPositiveAction: (String) -> Unit = { flightUrl ->
            // 預設動作為使用 Intent 開啟 URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(flightUrl))
            startActivity(intent)
        }
    ) {
        AlertDialog.Builder(this)
            .setIcon(icon)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("是") { dialog, which ->
                onPositiveAction(url)
            }
            .setNegativeButton("否") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}
