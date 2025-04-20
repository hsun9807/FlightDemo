package com.example.flightdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flightdemo.response.Flight
import com.example.flightdemo.R

class FlightAdapter(
    private var flightList: MutableList<Flight>
) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    var type: Int = 0

    // 點擊事件的 lambda，傳入當前的 Flight 物件
    var onItemClick: ((Flight) -> Unit)? = null

    // 更新列表資料
    fun updateData(tabType: Int, newList: List<Flight>?) {
        flightList.clear()
        if (newList != null) {
            flightList.addAll(newList)
        }
        type = tabType
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(flightList[position])
    }

    override fun getItemCount(): Int = flightList.size

    // 將 ViewHolder 改為 inner class，這樣它可以存取 adapter 的屬性
    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAirlineLogo: ImageView = itemView.findViewById(R.id.ivAirlineLogo)
        private val tvAirLine: TextView = itemView.findViewById(R.id.tvAirLine)
        private val tvExpectTime: TextView = itemView.findViewById(R.id.tvExpectTime)
        private val tvRealTime: TextView = itemView.findViewById(R.id.tvRealTime)
        private val tvFlightNumber: TextView = itemView.findViewById(R.id.tvFlightNumber)
        private val tvUpAirport: TextView = itemView.findViewById(R.id.tvUpAirport)
        private val tvDownAirport: TextView = itemView.findViewById(R.id.tvDownAirport)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvFlightCode: TextView = itemView.findViewById(R.id.tvFlightCode)

        fun bind(flight: Flight) {
            tvAirLine.text = "${flight.airLineCode ?: ""}  ${flight.airLineName ?: ""}"

            tvExpectTime.text = "預計時間\r\n${flight.expectTime ?: "HH:mm"}"
            tvRealTime.text = "實際時間\r\n${flight.realTime ?: "HH:mm"}"
            // 航機班號
            tvFlightCode.text =
                "班機編號/機型： ${flight.airLineNum ?: ""} / ${flight.airPlaneType ?: ""}"
            // 航廈 / 登機門
            tvFlightNumber.text = "登機門： ${flight.airBoardingGate ?: ""}"

            // 根據 adapter 裡的 type 來判斷目前頁籤，使用 this@FlightAdapter.type 取得
            if (this@FlightAdapter.type == 0) {
                tvUpAirport.text = "KHH\r\n高雄國際機場"
                tvDownAirport.text =
                    "${flight.goalAirportCode ?: ""}\r\n${flight.goalAirportName ?: ""}"
            } else {
                tvUpAirport.text = "${flight.upAirportCode ?: ""}\r\n${flight.upAirportName ?: ""}"
                tvDownAirport.text = "KHH\r\n高雄國際機場"
            }
            // 狀態
            tvStatus.text = "${flight.airFlyStatus ?: "未知狀態"}"

            // 使用 Glide 載入航空公司 Logo (URL 可能為 GIF)
            // 判斷 logo URL 是否有值
            if (!flight.airLineLogo.isNullOrEmpty()) {
                ivAirlineLogo.visibility = View.VISIBLE
//                Glide.with(itemView.context)
//                    .load(flight.airLineLogo)
//                    .into(ivAirlineLogo)

                Glide.with(itemView.context)
                    .asGif()
                    .load(flight.airLineLogo)
                    .fitCenter()
                    .into(ivAirlineLogo)
            } else {
                ivAirlineLogo.visibility = View.GONE
            }

            // 根據狀態變更顏色
            when {
                flight.airFlyStatus?.contains("取消", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("CANCELLED", ignoreCase = true) == true -> {
                    tvStatus.setTextColor(itemView.resources.getColor(android.R.color.holo_red_dark))
                }

                flight.airFlyStatus?.contains("更改", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("CHANGE", ignoreCase = true) == true -> {
                    tvStatus.setTextColor(itemView.resources.getColor(android.R.color.holo_orange_dark))
                }

                flight.airFlyStatus?.contains("抵達", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("準時", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("ARRIVED", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("On Time", ignoreCase = true) == true -> {
                    tvStatus.setTextColor(itemView.resources.getColor(android.R.color.holo_blue_dark))
                }

                flight.airFlyStatus?.contains("出發", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("登機", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("DEPARTED", ignoreCase = true) == true ||
                        flight.airFlyStatus?.contains("BOARDING", ignoreCase = true) == true -> {
                    tvStatus.setTextColor(itemView.resources.getColor(android.R.color.holo_green_dark))
                }

                else -> {
                    tvStatus.setTextColor(itemView.resources.getColor(android.R.color.black))
                }
            }

            // 設定點擊事件，點擊時呼叫 onItemClick lambda，並傳入當前 flight 物件
            itemView.setOnClickListener {
                onItemClick?.invoke(flight)
            }
        }
    }
}
