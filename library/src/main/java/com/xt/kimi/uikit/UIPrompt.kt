package com.xt.kimi.uikit

import android.app.AlertDialog
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage
import com.xt.kimi.currentActivity

class UIPrompt(val message: String) {

    var confirmTitle: String = "Done"

    var cancelTitle: String = "Cancel"

    var placeholder: String? = null

    var defaultValue: String? = null

    fun show(completed: EDOCallback, cancelled: EDOCallback?) {
        val currentActivity = currentActivity ?: return
        val layout = RelativeLayout(currentActivity)
        val editText = EditText(currentActivity)
        editText.hint = this.placeholder
        editText.text.append(this.defaultValue)
        editText.setSingleLine()
        layout.setPadding((20 * scale).toInt(), 0, (20 * scale).toInt(), 0)
        layout.addView(editText, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        AlertDialog.Builder(currentActivity)
                .setMessage(message)
                .setView(layout)
                .setPositiveButton(confirmTitle) { _, _ ->
                    completed.invoke(editText.text.toString())
                }
                .setNegativeButton(cancelTitle) { _, _ ->
                    cancelled?.invoke()
                }
                .show()
    }

}

fun KIMIPackage.installUIPrompt() {
    exporter.exportClass(UIPrompt::class.java, "UIPrompt")
    exporter.exportInitializer(UIPrompt::class.java) {
        return@exportInitializer UIPrompt(
                if (0 < it.count()) it[0] as? String ?: "" else ""
        )
    }
    exporter.exportProperty(UIPrompt::class.java, "confirmTitle")
    exporter.exportProperty(UIPrompt::class.java, "cancelTitle")
    exporter.exportProperty(UIPrompt::class.java, "placeholder")
    exporter.exportProperty(UIPrompt::class.java, "defaultValue")
    exporter.exportMethodToJavaScript(UIPrompt::class.java, "show")
}