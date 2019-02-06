package com.incwellventure.auth.utils

import android.app.Activity
import com.google.android.material.snackbar.Snackbar

class SnackbarUtils {
    companion object {
        fun notifyUser(context: Activity, message: String) {
            Snackbar.make(context.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
        }
    }

}