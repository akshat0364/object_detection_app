package com.objdetector.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

class ErrorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity: Activity = activity
        return AlertDialog.Builder(activity)
            .setMessage(arguments?.getString(ARG_MESSAGE))
            .setPositiveButton(android.R.string.ok) { _, _ -> activity.finish() }
            .create()
    }

    companion object {
        private const val ARG_MESSAGE = "message"

        fun newInstance(message: String): ErrorDialog {
            val dialog = ErrorDialog()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }
}
