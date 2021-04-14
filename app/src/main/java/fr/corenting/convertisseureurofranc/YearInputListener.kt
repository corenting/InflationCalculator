package fr.corenting.convertisseureurofranc

import android.view.View
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

class YearInputListener(
    private val parentInputLayout: TextInputLayout,
    private val minYear: Int,
    private val maxYear: Int,
    private val errorString: String
) : View.OnFocusChangeListener {


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        val textContent = (v as EditText).text.toString()
        if (textContent.isBlank()) {
            this.parentInputLayout.error = errorString
            return
        }

        try {
            val year = textContent.toInt()
            when {
                year < this.minYear || year > this.maxYear -> {
                    this.parentInputLayout.error = errorString
                }
                else -> {
                    this.parentInputLayout.error = null
                }
            }
        } catch (err: NumberFormatException) {
            this.parentInputLayout.error = errorString
        }
    }
}