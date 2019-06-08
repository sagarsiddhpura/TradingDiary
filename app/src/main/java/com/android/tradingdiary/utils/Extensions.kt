package com.android.tradingdiary.utils

import android.app.Activity
import android.os.Build
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.android.tradingdiary.R
import kotlinx.android.synthetic.main.dialog_title.view.*

fun Activity.setupDialogStuff(view: View, dialog: AlertDialog, titleId: Int = 0, titleText: String = "", callback: (() -> Unit)? = null) {
    if (isActivityDestroyed()) {
        return
    }

    var title: TextView? = null
    if (titleId != 0 || titleText.isNotEmpty()) {
        title = layoutInflater.inflate(R.layout.dialog_title, null) as TextView
        title.dialog_title_textview.apply {
            if (titleText.isNotEmpty()) {
                text = titleText
            } else {
                setText(titleId)
            }
        }
    }

    dialog.apply {
        setView(view)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCustomTitle(title)
        setCanceledOnTouchOutside(true)
        show()
    }
    callback?.invoke()
}

fun Activity.isActivityDestroyed() = isJellyBean1Plus() && isDestroyed

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun isJellyBean1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
