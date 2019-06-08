package com.android.tradingdiary.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.android.tradingdiary.R
import kotlinx.android.synthetic.main.dialog_message.view.*

class ConfirmationDialog(activity: Activity, message: String = "", messageId: Int = R.string.ok, positive: Int = R.string.yes,
                         negative: Int = R.string.no, val callback: () -> Unit) {
    var dialog: AlertDialog

    init {
        val view = activity.layoutInflater.inflate(R.layout.dialog_message, null)
        view.message.text = if (message.isEmpty()) activity.resources.getString(messageId) else message

        val builder = AlertDialog.Builder(activity)
            .setPositiveButton(positive) { dialog, which -> dialogConfirmed() }

        if (negative != 0)
            builder.setNegativeButton(negative, null)

        dialog = builder.create().apply {
            activity.setupDialogStuff(view, this)
        }
    }

    private fun dialogConfirmed() {
        dialog.dismiss()
        callback()
    }
}
