package app.tgayle.inboxforreddit.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.lifecycle.MutableLiveData

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

/**
 * Resolves the value of a custom attribute and returns it based on the application's current theme.
 */
fun Context.getColorFromAttr(@AttrRes attrColor: Int, typedValue: TypedValue = TypedValue(), resolveRefs: Boolean = true): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}