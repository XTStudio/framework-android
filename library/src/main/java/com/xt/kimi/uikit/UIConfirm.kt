package com.xt.kimi.uikit

import android.app.AlertDialog
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage
import com.xt.kimi.currentActivity

class UIConfirm(val message: String) {

    var confirmTitle: String = "Done"

    var cancelTitle: String = "Cancel"

    fun show(completed: EDOCallback, cancelled: EDOCallback?) {
        val currentActivity = currentActivity ?: return
        AlertDialog.Builder(currentActivity)
                .setMessage(message)
                .setPositiveButton(confirmTitle) { _, _ ->
                    completed.invoke()
                }
                .setNegativeButton(cancelTitle) { _, _ ->
                    cancelled?.invoke()
                }
                .show()
    }

}

fun KIMIPackage.installUIConfirm() {
    exporter.exportClass(UIConfirm::class.java, "UIConfirm")
    exporter.exportInitializer(UIConfirm::class.java) {
        return@exportInitializer UIConfirm(
                if (0 < it.count()) it[0] as? String ?: "" else ""
        )
    }
    exporter.exportProperty(UIConfirm::class.java, "confirmTitle")
    exporter.exportProperty(UIConfirm::class.java, "cancelTitle")
    exporter.exportMethodToJavaScript(UIConfirm::class.java, "show")
}