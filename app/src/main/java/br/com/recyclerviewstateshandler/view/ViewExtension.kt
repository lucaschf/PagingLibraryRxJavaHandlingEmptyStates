package br.com.recyclerviewstateshandler.view

import android.view.View

fun View.hide(gone: Boolean = true): View {
    visibility = when {
        gone -> View.GONE
        else -> View.INVISIBLE
    }

    return this
}

fun View.show(): View {
    visibility = View.VISIBLE
    return this
}

fun View.toggleVisibility(show: Boolean) {
    visibility = when {
        show -> View.VISIBLE
        else -> View.GONE
    }
}