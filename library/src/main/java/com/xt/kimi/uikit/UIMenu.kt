package com.xt.kimi.uikit

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Menu
import com.xt.endo.EDOCallback
import com.xt.kimi.KIMIPackage
import com.xt.kimi.currentActivity

class UIMenu {

    private class UIMenuItem(val title: String, val block: EDOCallback)

    private val menuItems: MutableList<UIMenuItem> = mutableListOf()

    fun addMenuItem(title: String, actionBlock: EDOCallback) {
        this.menuItems.add(UIMenuItem(title, actionBlock))
    }

    fun show(inView: UIView) {
        currentActivity?.let { currentActivity ->
            val menuDialog = AlertDialog.Builder(currentActivity)
                    .setItems(this.menuItems.map { it.title }.toTypedArray()) { p0, p1 ->
                        this.menuItems.getOrNull(p1)?.block?.invoke()
                    }
                    .create()
            menuDialog.show()
        }
    }

}

fun KIMIPackage.installUIMenu() {
    exporter.exportClass(UIMenu::class.java, "UIMenu")
    exporter.exportMethodToJavaScript(UIMenu::class.java, "addMenuItem")
    exporter.exportMethodToJavaScript(UIMenu::class.java, "show")
}