package com.xt.kimi.uikit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage

class UIPasteboard {

    var string: String?
        set(value) {
            if (value == null) {
                (EDOExporter.sharedExporter.applicationContext?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
                    it.primaryClip = ClipData.newPlainText("default", "")
                }
            }
            else {
                (EDOExporter.sharedExporter.applicationContext?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
                    it.primaryClip = ClipData.newPlainText("default", value)
                }
            }
        }
        get() {
            (EDOExporter.sharedExporter.applicationContext?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
                val item = it.primaryClip.getItemAt(0)
                item?.let {
                    return it.text.toString()
                }
            }
            return null
        }

    companion object {

        @JvmStatic val shared = UIPasteboard()

    }

}

fun KIMIPackage.installUIPasteboard() {
    exporter.exportClass(UIPasteboard::class.java, "UIPasteboard")
    exporter.exportStaticProperty(UIPasteboard::class.java, "shared", true, true)
    exporter.exportProperty(UIPasteboard::class.java, "string")
}