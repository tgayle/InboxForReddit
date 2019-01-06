package app.tgayle.inboxforreddit.util

import android.view.View
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import app.tgayle.inboxforreddit.util.MaterialSnackbar.SnackbarLength.*
import com.google.android.material.snackbar.Snackbar

/**
 * Wrapping class for creating snackbars that appear above a BottomNavigationBar/BottomAppBar
 */
class MaterialSnackbar {
    companion object {
        private const val DEFAULT_MARGIN_SIDE = 0
        private const val DEFAULT_MARGIN_BOTTOM = 140

        fun make(view: View,
                 message: String,
                 duration: SnackbarLength,
                 marginSide: Int = DEFAULT_MARGIN_SIDE,
                 marginBottom: Int = DEFAULT_MARGIN_BOTTOM): Snackbar {

            val length = when (duration) {
                SHORT -> Snackbar.LENGTH_SHORT
                LONG -> Snackbar.LENGTH_LONG
                INDEFINITE -> Snackbar.LENGTH_INDEFINITE
            }

            val snackbar = Snackbar.make(view, message, length)
            val snackbarView = snackbar.view
            val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams

            params.setMargins(
                params.leftMargin + marginSide,
                params.topMargin,
                params.rightMargin + marginSide,
                params.bottomMargin + marginBottom
            )

            snackbarView.layoutParams = params
            return snackbar
        }

        fun make(view: View,
                 @StringRes stringResource: Int,
                 duration: SnackbarLength,
                 marginSide: Int = DEFAULT_MARGIN_SIDE,
                 marginBottom: Int = DEFAULT_MARGIN_BOTTOM
        ): Snackbar {
            val content = view.context.getString(stringResource)
            return make(view, content, duration, marginSide, marginBottom)
        }
    }

    enum class SnackbarLength {
        SHORT,
        LONG,
        INDEFINITE
    }
}