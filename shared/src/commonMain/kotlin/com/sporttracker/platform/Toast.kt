package com.sporttracker.platform

expect class Toast {
    fun show(message: String)
    fun showLong(message: String)
}

expect fun createToast(): Toast