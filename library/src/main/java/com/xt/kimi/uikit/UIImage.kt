package com.xt.kimi.uikit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import com.xt.endo.CGSize
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage
import kotlin.math.floor


enum class UIImageRenderingMode {
    automatic,
    alwaysOriginal,
    alwaysTemplate,
}

class UIImage(val bitmap: Bitmap, val scale: Int = 1, val renderingMode: UIImageRenderingMode = UIImageRenderingMode.automatic) {

    val size: CGSize = CGSize(bitmap.width.toDouble() / this.scale, bitmap.height.toDouble() / this.scale)

    companion object {

        fun fileName(name: String, scale: Int): String {
            return if (scale == 1) {
                "$name.png"
            } else {
                "$name@${scale}x.png"
            }
        }

    }

}

fun KIMIPackage.installUIImage() {
    exporter.exportClass(UIImage::class.java, "UIImage")
    exporter.exportInitializer(UIImage::class.java) {
        (it.firstOrNull() as? Map<String, Any>)?.let { options ->
            val renderingMode = options["renderingMode"] as? UIImageRenderingMode ?: UIImageRenderingMode.automatic
            (options["base64"] as? String)?.let { base64 ->
                try {
                    val byteArray = Base64.decode(base64, 0)
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.count())?.let {
                        return@exportInitializer UIImage(it, 1, renderingMode)
                    }
                } catch (e: Exception) {}
            }
            (options["name"] as? String)?.let { name ->
                val applicationContext = EDOExporter.sharedExporter.applicationContext ?: return@let
                var currentScale = floor(scale).toInt()
                var targetFile: String? = null
                val files = applicationContext.assets.list("images").filter { it.startsWith(name) }
                if (files.contains(UIImage.fileName(name, currentScale))) {
                    targetFile = "images/${UIImage.fileName(name, currentScale)}"
                } else {
                    while (currentScale > 0) {
                        if (files.contains(UIImage.fileName(name, currentScale))) {
                            targetFile = "images/${UIImage.fileName(name, currentScale)}"
                            break
                        }
                        currentScale--
                    }
                }
                targetFile?.let { targetFile ->
                    try {
                        EDOExporter.sharedExporter.applicationContext?.assets?.open(targetFile).use {
                            return@exportInitializer UIImage(BitmapFactory.decodeStream(it), currentScale, renderingMode)
                        }
                    } catch (e: Exception) { }
                }
            }
        }
        return@exportInitializer UIImage(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
    }
    exporter.exportProperty(UIImage::class.java, "size")
    exporter.exportProperty(UIImage::class.java, "scale")
    exporter.exportEnum("UIImageRenderingMode", mapOf(
            Pair("automatic", UIImageRenderingMode.automatic),
            Pair("alwaysOriginal", UIImageRenderingMode.alwaysOriginal),
            Pair("alwaysTemplate", UIImageRenderingMode.alwaysTemplate)
    ))
}