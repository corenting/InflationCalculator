package fr.corenting.convertisseureurofranc

import android.content.Context
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract

object YearInputTextHandler {
    fun doOnTextChanged(
        context: Context,
        converter: ConverterAbstract,
        yearInputField: EditText,
        yearInputLayout: TextInputLayout
    ) {
        val textContent = yearInputField.text.toString()
        val errorString = context.getString(
            R.string.invalid_year_error
        )
        if (textContent.isBlank()) {
            yearInputLayout.error = errorString
        }

        try {
            val year = textContent.toInt()
            when {
                year < converter.firstYear || year > converter.latestYear -> {
                    yearInputLayout.error = errorString
                }
                else -> {
                    yearInputLayout.error = null
                }
            }
        } catch (err: NumberFormatException) {
            yearInputLayout.error = errorString
        }
    }
}