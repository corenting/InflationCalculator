package fr.corenting.convertisseureurofranc.utils

import android.content.Context
import android.os.Build
import android.view.View
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UtilsTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun hideSoftKeyboard() {
        val view = View(context)

        val ret = Utils.hideSoftKeyboard(view)

        assertTrue(ret)
    }

    @Test
    fun formatNumber_SingleDigit() {
        val ret = Utils.formatNumber(context, 2.0f)
        assertEquals(ret, "2")
    }

    @Test
    fun formatNumber_MultipleDigits() {
        val ret = Utils.formatNumber(context, 252.0f)
        assertEquals(ret, "252")
    }

    @Test
    fun formatNumber_Decimals() {
        val ret = Utils.formatNumber(context, 252.50f)
        assertEquals(ret, "252.5")
    }
}