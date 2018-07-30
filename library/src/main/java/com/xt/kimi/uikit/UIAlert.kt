package com.xt.kimi.uikit

import android.app.AlertDialog
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage
import com.xt.kimi.currentActivity

class UIAlert(val message: String, val buttonText: String? = "OK") {

    fun show(callback: EDOCallback) {
        val currentActivity = currentActivity ?: return
        AlertDialog.Builder(currentActivity)
                .setMessage(message)
                .setPositiveButton(buttonText) { _, _ ->
                    callback.invoke()
                }
                .show()
    }

}

fun KIMIPackage.installUIAlert() {
    exporter.exportClass(UIAlert::class.java, "UIAlert")
    exporter.exportInitializer(UIAlert::class.java) {
        return@exportInitializer UIAlert(
                if (0 < it.count()) it[0] as? String ?: "" else "",
                if (1 < it.count()) it[1] as? String else null
        )
    }
    exporter.exportMethodToJavaScript(UIAlert::class.java, "show")
}