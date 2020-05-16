package ar.edu.unlam.practicalibs.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


fun View.show(){
    if (this.visibility == View.GONE){
        this.visibility = View.VISIBLE
    }
}

fun View.hide(){
    if (this.visibility == View.VISIBLE){
        this.visibility = View.GONE
    }
}

fun View.isVisible():Boolean{
    return this.visibility == View.VISIBLE
}


fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}