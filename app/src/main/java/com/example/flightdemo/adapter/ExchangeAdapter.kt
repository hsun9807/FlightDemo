package com.example.flightdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flightdemo.response.ExchangeItem
import com.example.flightdemo.R
import java.text.DecimalFormat

class ExchangeAdapter(
    private var exchangeList: List<ExchangeItem>
) : RecyclerView.Adapter<ExchangeAdapter.ExchangeViewHolder>() {

    // 定義點擊事件的 lambda，傳入當前 ExchangeItem
    var onItemClick: ((ExchangeItem) -> Unit)? = null
    val formatter = DecimalFormat("#,##0.00")

    // 定義長按事件的 lambda
    var onItemLongClick: ((ExchangeItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exchange, parent, false)
        return ExchangeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        holder.bind(exchangeList[position])
    }

    override fun getItemCount(): Int = exchangeList.size

    fun updateData(newList: List<ExchangeItem>) {
        exchangeList = newList
        notifyDataSetChanged()
    }

    inner class ExchangeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCurrency: TextView = itemView.findViewById(R.id.tvCurrency)
        private val tvRate: TextView = itemView.findViewById(R.id.tvRate)

        fun bind(item: ExchangeItem) {
            tvCurrency.text = item.currency
            tvRate.text = formatter.format(item.money)

            if (item.rate == 1.0){
                tvCurrency.setTextColor(itemView.resources.getColor(android.R.color.holo_red_dark))
                tvRate.setTextColor(itemView.resources.getColor(android.R.color.holo_red_dark))
            }else{
                tvCurrency.setTextColor(itemView.resources.getColor(android.R.color.black))
                tvRate.setTextColor(itemView.resources.getColor(android.R.color.black))
            }

            // 設定點擊事件
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
            // 設定長按事件，並回傳 true 代表已消耗事件
            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(item)
                true
            }
        }
    }
}
