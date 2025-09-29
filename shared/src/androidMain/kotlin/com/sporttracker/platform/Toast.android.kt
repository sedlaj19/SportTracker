package com.sporttracker.platform

import android.content.Context
import android.widget.Toast as AndroidToast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class Toast : KoinComponent {
    private val context: Context by inject()

    actual fun show(message: String) {
        AndroidToast.makeText(context, message, AndroidToast.LENGTH_SHORT).show()
    }

    actual fun showLong(message: String) {
        AndroidToast.makeText(context, message, AndroidToast.LENGTH_LONG).show()
    }
}

actual fun createToast(): Toast = Toast()