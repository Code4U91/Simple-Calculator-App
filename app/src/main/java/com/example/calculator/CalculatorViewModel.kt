package com.example.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Scriptable

class CalculatorViewModel : ViewModel() {


    private val _equationText = MutableLiveData("")
    val equation : LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val result : LiveData<String> =  _resultText

    // Update the equation text from the BasicTextField
    fun updateEquation(newText: String) {
        _equationText.value = newText
        calculateResult(newText)
    }

    fun updateResult(newText: String)
    {
        _resultText.value = newText
    }

    private fun calculateResult(equation: String) {
        try {
            _resultText.value = calculate(equation)
        } catch (_: Exception) {
        }
    }

}

private fun calculate(equation : String) : String
{

    if (equation.isEmpty()) {
        return "0"  // Return default result when the equation is empty
    }

    val context: org.mozilla.javascript.Context = org.mozilla.javascript.Context.enter()

    context.optimizationLevel = -1
    val scriptable : Scriptable = context.initStandardObjects()
    var finalResult = context.evaluateString(scriptable, equation, "Javascript",1, null).toString()

    if (finalResult.endsWith(".0"))
    {
        finalResult = finalResult.replace(".0", "")
    }

    return finalResult
}
