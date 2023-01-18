package io.github.svejnohatomas.swansea.newsaggregator.helper

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IConfirmationObserver

/**
 * A helper class to show refresh confirmation dialog.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class RefreshHelper {

    companion object {

        /**
         * Shows a refresh confirmation dialog.
         *
         * @param context The context of the dialog.
         * @param confirmationObserver A IConfirmationObserver to notify if the user confirms the refresh.
         * */
        fun showConfirmationDialog(context: Context, confirmationObserver: IConfirmationObserver) {
            MaterialAlertDialogBuilder(context)
                .setMessage(context.getString(R.string.new_articles_available))
                .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
                }
                .setPositiveButton(context.getString(R.string.refresh)) { _, _ ->
                    confirmationObserver.onConfirm()
                }
                .show()
        }
    }
}