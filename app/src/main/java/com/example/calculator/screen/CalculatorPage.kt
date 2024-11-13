package com.example.calculator.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.CalculatorViewModel

val buttonList = listOf(
    "C", "(", ")", "/",
    "7", "8", "9", "*",
    "4", "5", "6", "+",
    "1", "2", "3", "-",
    "AC", "0", ".", "="
)

@Composable
fun Calculator(modifier: Modifier, calculatorViewModel: CalculatorViewModel) {
    val equationText = calculatorViewModel.equation.observeAsState()
    val resultText = calculatorViewModel.result.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(equationText.value ?: "")) }
    val focusRequester = remember { FocusRequester() }
    val isDarkMode = isSystemInDarkTheme()

    // Request focus on composition
    LaunchedEffect(Unit) {

        kotlinx.coroutines.delay(100)
        focusRequester.requestFocus()

    }

    Box(
        modifier = modifier.padding(start = 5.dp, top = 20.dp, end = 5.dp, bottom = 0.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {

            // Equation

            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    calculatorViewModel.updateEquation(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            keyboardController?.hide()
                        }

                    },
                textStyle = TextStyle(
                    fontSize = 60.sp,
                    textAlign = TextAlign.End,
                    color = if (isDarkMode) Color.White else Color.Black

                ),
                maxLines = 5,
                keyboardOptions = KeyboardOptions.Default.copy(

                ),
                cursorBrush = SolidColor(Color(0xFF4662B6))
            )

            keyboardController?.hide()

            Spacer(modifier = Modifier.weight(1f))

            // Result

            Text(
                text = resultText.value ?: "0",
                fontSize = 30.sp,
                style = TextStyle(
                    textAlign = TextAlign.End
                ),
                maxLines = 2,

                )

            Spacer(modifier = Modifier.height(10.dp))
            ContentSeparator()

            LazyVerticalGrid(columns = GridCells.Fixed(4)) {

                items(buttonList)
                {

                    CalculatorButton(btn = it, onClick = {
                        val currentText = textFieldValue.text
                        val cursorPosition = textFieldValue.selection.start


                        when (it) {
                            "AC" -> {
                                // Clear the equation
                                textFieldValue = TextFieldValue("", TextRange(0))
                                calculatorViewModel.updateResult("0")

                            }

                            "C" -> {
                                // Remove the character just before the cursor position
                                if (currentText.isNotEmpty() && cursorPosition > 0) {
                                    val newText =
                                        currentText.removeRange(cursorPosition - 1, cursorPosition)
                                    textFieldValue = TextFieldValue(
                                        newText,
                                        selection = TextRange(cursorPosition - 1) // Move the cursor back by 1 position
                                    )
                                    calculatorViewModel.updateEquation(newText)
                                }
                            }

                            "=" -> {
                                // Calculate the result
                                textFieldValue = textFieldValue.copy(
                                    text = calculatorViewModel.result.value ?: "0"
                                )
                            }

                            else -> {
                                // For normal buttons, insert text at cursor position
                                val newText = currentText.substring(
                                    0,
                                    cursorPosition
                                ) + it + currentText.substring(cursorPosition)
                                textFieldValue = textFieldValue.copy(
                                    text = newText,
                                    selection = TextRange(cursorPosition + it.length) // Move cursor after inserted text
                                )
                                calculatorViewModel.updateEquation(newText)
                            }
                        }
                    })
                }
            }
        }
    }
}


@Composable
fun CalculatorButton(btn: String, onClick: () -> Unit) {

    Box(modifier = Modifier.padding(10.dp))
    {
        FloatingActionButton(
            onClick = { onClick() },
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            containerColor = getColor(btn),
            contentColor = Color.White
        ) {
            Text(
                text = btn,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun ContentSeparator() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        thickness = 1.dp,
        color = Color.Gray
    )
}

fun getColor(btn: String): Color {
    if (btn == "C" || btn == "AC") {
        return Color.Red
    }

    if (btn == "(" || btn == ")") {
        return Color.Gray
    }

    if (btn == "/" || btn == "+" || btn == "=" || btn == "-" || btn == "*") {
        return Color(0xFFFF9800)
    }

    return Color(0xFF00C8C9)
}