package com.example.flightdemo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.flightdemo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DecimalFormat

class CalcBottomFragment : BottomSheetDialogFragment() {

    private lateinit var tvDisplay: TextView
    private var currentInput = ""
    private var operator: String = ""
    private var previousValue: Double? = null
    // 這裡可以加入回呼以將計算結果回傳給調用端
    var onCalculatorResult: ((String) -> Unit)? = null
    private var isResultShown = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 載入計算機的佈局
        return inflater.inflate(R.layout.layout_calculator_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDisplay = view.findViewById(R.id.tvDisplay)

        // 數字與點按鈕
        val buttonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in buttonIds) {
            view.findViewById<Button>(id).setOnClickListener { numberClick(it as Button) }
        }

        // 運算子
        view.findViewById<Button>(R.id.btnPlus).setOnClickListener { operatorClick("+") }
        view.findViewById<Button>(R.id.btnMinus).setOnClickListener { operatorClick("-") }
        view.findViewById<Button>(R.id.btnMultiply).setOnClickListener { operatorClick("*") }
        view.findViewById<Button>(R.id.btnDivide).setOnClickListener { operatorClick("/") }

        // 清除與刪除
        view.findViewById<Button>(R.id.btnCE).setOnClickListener { clearAll() }
        view.findViewById<Button>(R.id.btnDelete).setOnClickListener { deleteLast() }

        // 等於
        view.findViewById<Button>(R.id.btnEqual).setOnClickListener { calculateResult() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun numberClick(btn: Button) {
        if (tvDisplay.text == "Error" || isResultShown) {
            currentInput = ""
            isResultShown = false
        }

        val inputChar = btn.text.toString()

        if (inputChar == "." && currentInput.contains("."))
            return
        if (inputChar == "." && currentInput.isEmpty())
            currentInput = "0"

        currentInput += inputChar
        tvDisplay.text = currentInput
    }

    private fun operatorClick(op: String) {
        if (currentInput.isEmpty() && operator.isNotEmpty()) {
            // 若前一個是運算子就提示錯誤（防止重複點擊）
            if (operator.isNotEmpty()) {
                tvDisplay.text = "請先輸入數字"
                isResultShown = true
            }
            // 若有先前的 operator 且未輸入數字就按第二個，直接忽略
            return
        }

        if (previousValue != null && currentInput.isNotEmpty()) {
            // 自動先結算一次
            calculateResult()
            previousValue = currentInput.toDoubleOrNull()
            currentInput = ""
        } else {
            previousValue = currentInput.toDoubleOrNull()
            currentInput = ""
        }

        operator = op
        tvDisplay.text = op
        isResultShown = false
    }

    private fun calculateResult() {
        val currentValue = currentInput.toDoubleOrNull()
        if (previousValue == null || currentValue == null || operator.isEmpty()) {
            tvDisplay.text = "Error"
            isResultShown = true
            return
        }

        val result = when (operator) {
            "+" -> previousValue!! + currentValue
            "-" -> previousValue!! - currentValue
            "*" -> previousValue!! * currentValue
            "/" -> if (currentValue != 0.0) previousValue!! / currentValue else {
                tvDisplay.text = "Error"
                isResultShown = true
                return
            }
            else -> 0.0
        }
        val formattedResult = DecimalFormat("#,###.########").format(result)
        tvDisplay.text = formattedResult
        currentInput = result.toString()  // 保留未格式化的結果供下一次運算用
        operator = ""
        previousValue = null
        isResultShown = true
        onCalculatorResult?.invoke(result.toString())
    }

    private fun clearAll() {
        currentInput = ""
        operator = ""
        previousValue = null
        isResultShown = false
        tvDisplay.text = "0"
    }

    private fun deleteLast() {
        if (tvDisplay.text == "Error" || isResultShown) {
            clearAll()
            return
        }

        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            tvDisplay.text = if (currentInput.isEmpty()) "0" else currentInput
        }
    }
}
