package com.xt.kimi.foundation

import android.content.Context
import android.util.Base64
import com.xt.endo.EDOExporter
import com.xt.endo.EDOObjectTransfer
import com.xt.jscore.JSContext
import com.xt.kimi.KIMIPackage
import java.io.File

class FileManager {

    fun subpaths(atPath: String, deepSearch: Boolean?): List<String> {
        if (atPath.startsWith("/com.xt.bundle.js/")) {
            val bundleRef = JSContext.currentContext?.evaluateScript("Bundle.js") ?: return emptyList()
            val bundle = EDOObjectTransfer.convertToJavaObjectWithJSValue(bundleRef, bundleRef, null) as? JSBundle ?: return emptyList()
            val basePath = atPath.replaceFirst("/com.xt.bundle.js/", "")
            return bundle.resources.keys.filter { aKey ->
                if (basePath.isEmpty() || aKey.startsWith(basePath)) {
                    var trimPath = aKey.substring(basePath.length)
                    if (trimPath.startsWith("/")) {
                        trimPath = trimPath.substring(1)
                    }
                    if (trimPath.contains("/")) {
                        if (deepSearch == true) {
                            return@filter true
                        }
                    }
                    else {
                        return@filter true
                    }
                }
                return@filter false
            }
        }
        else if (atPath.startsWith("/android_assets/")) {
            val basePath = atPath.replaceFirst("/android_assets/", "")
            val applicationContext = EDOExporter.sharedExporter.applicationContext ?: return emptyList()
            return try {
                applicationContext.assets.list(basePath).toList()
            } catch (e: Exception) { emptyList() }
        }
        else {
            if (deepSearch == true) {
                val result: MutableList<String> = mutableListOf()
                this.subpathsWithDeepSearch(File(atPath), File(atPath), result)
                return result.toList()
            }
            else {
                try {
                    val file = File(atPath)
                    return file.list().toList()
                } catch (e: Exception) {
                    return emptyList()
                }
            }
        }
    }

    private fun subpathsWithDeepSearch(base: File, file: File, result: MutableList<String>) {
        if (file.isDirectory) {
            file.listFiles().forEach {
                if (it.isDirectory) {
                    result.add(it.path.replace(base.path + "/", ""))
                    this.subpathsWithDeepSearch(base, it, result)
                }
                else {
                    result.add(it.path.replace(base.path + "/", ""))
                }
            }
        }
    }

    fun createDirectory(atPath: String, withIntermediateDirectories: Boolean): Error? {
        if (atPath.startsWith("/com.xt.bundle.js/") || atPath.startsWith("/android_assets/")) {
            return Error("readonly")
        }
        try {
            val file = File(atPath)
            if (withIntermediateDirectories) {
                file.mkdirs()
            }
            else {
                file.mkdir()
            }
        } catch (e: Exception) {
            return Error(e.message ?: "unknown error.")
        }
        return null
    }

    fun createFile(atPath: String, data: Data): Error? {
        if (atPath.startsWith("/com.xt.bundle.js/") || atPath.startsWith("/android_assets/")) {
            return Error("readonly")
        }
        try {
            val file = File(atPath)
            mkdirs(file)
            file.writeBytes(data.byteArray)
        } catch (e: Exception) {
            return Error(e.message ?: "unknown error.")
        }
        return null
    }

    fun readFile(atPath: String): Data? {
        if (atPath.startsWith("/com.xt.bundle.js/")) {
            val fileName = atPath.replaceFirst("/com.xt.bundle.js/", "")
            val bundleRef = JSContext.currentContext?.evaluateScript("Bundle.js") ?: return null
            val bundle = EDOObjectTransfer.convertToJavaObjectWithJSValue(bundleRef, bundleRef, null) as? JSBundle ?: return null
            bundle.resources[fileName]?.let {
                return Data(Base64.decode(it, 0))
            }
            return null
        }
        else if (atPath.startsWith("/android_assets/")) {
            val fileName = atPath.replaceFirst("/android_assets/", "")
            val applicationContext = EDOExporter.sharedExporter.applicationContext ?: return null
            try {
                applicationContext.assets.open(fileName)?.use {
                    val byteArray = ByteArray(it.available())
                    it.read(byteArray)
                    return Data(byteArray)
                }
            } catch (e: Exception) {}
            return null
        }
        else {
            return try {
                val file = File(atPath)
                Data(file.readBytes())
            } catch (e: Exception) {
                null
            }
        }
    }

    fun removeItem(atPath: String): Error? {
        if (atPath.startsWith("/com.xt.bundle.js/") || atPath.startsWith("/android_assets/")) {
            return Error("readonly")
        }
        try {
            val file = File(atPath)
            file.delete()
        } catch (e: Exception) {
            return Error(e.message ?: "unknown error.")
        }
        return null
    }

    fun copyItem(atPath: String, toPath: String): Error? {
        if (atPath.startsWith("/com.xt.bundle.js/")) {
            val fromFileData = this.readFile(atPath) ?: return Error("file not exists.")
            return this.createFile(toPath, fromFileData)
        }
        try {
            val fromFile = File(atPath)
            val toFile = File(toPath)
            fromFile.copyTo(toFile, true)
        } catch (e: Exception) {
            return Error(e.message ?: "unknown error.")
        }
        return null
    }

    fun moveItem(atPath: String, toPath: String): Error? {
        if (atPath.startsWith("/com.xt.bundle.js/") || atPath.startsWith("/android_assets/")) {
            return Error("readonly")
        }
        try {
            val fromFile = File(atPath)
            val toFile = File(toPath)
            fromFile.copyTo(toFile, true)
            fromFile.delete()
        } catch (e: Exception) {
            return Error(e.message ?: "unknown error.")
        }
        return null
    }

    fun fileExists(atPath: String): Boolean {
        if (atPath.startsWith("/com.xt.bundle.js/")) {
            val fileName = atPath.replaceFirst("/com.xt.bundle.js/", "")
            val bundleRef = JSContext.currentContext?.evaluateScript("Bundle.js") ?: return false
            val bundle = EDOObjectTransfer.convertToJavaObjectWithJSValue(bundleRef, bundleRef, null) as? JSBundle ?: return false
            return bundle.resources.containsKey(fileName)
        }
        else if (atPath.startsWith("/android_assets/")) {
            val fileName = atPath.replaceFirst("/android_assets/", "")
            val applicationContext = EDOExporter.sharedExporter.applicationContext ?: return false
            return if (fileName.contains("/")) {
                try {
                    var coms = fileName.split("/")
                    applicationContext.assets.list(coms.filterIndexed { index, s -> index < coms.count() - 1 }.joinToString("/")).contains(fileName)
                } catch (e: Exception) {
                    false
                }
            } else {
                try {
                    applicationContext.assets.list("").contains(fileName)
                } catch (e: Exception) {
                    false
                }
            }
        }
        else {
            val file = File(atPath)
            return try {
                file.isFile
            } catch (e: Exception) { false }
        }
    }

    fun dirExists(atPath: String): Boolean {
        if (atPath.startsWith("/com.xt.bundle.js/") || atPath.startsWith("/android_assets/")) {
            return false
        }
        val file = File(atPath)
        return try {
            file.isDirectory
        } catch (e: Exception) { false }
    }

    companion object {

        @JvmField val defaultManager = FileManager()

        @JvmField val documentDirectory: String = EDOExporter.sharedExporter.applicationContext?.getDir("com.xt.filemanager.document", Context.MODE_PRIVATE)?.absolutePath ?: ""

        @JvmField val libraryDirectory: String = EDOExporter.sharedExporter.applicationContext?.getDir("com.xt.filemanager.library", Context.MODE_PRIVATE)?.absolutePath ?: ""

        @JvmField val cacheDirectory: String = (EDOExporter.sharedExporter.applicationContext?.cacheDir?.absolutePath ?: "") + "/caches"

        @JvmField val temporaryDirectory: String = (EDOExporter.sharedExporter.applicationContext?.cacheDir?.absolutePath ?: "") + "/tmp"

        @JvmField val jsBundleDirectory: String = "/com.xt.bundle.js/"

        private fun mkdirs(file: File) {
            file.parentFile.mkdirs()
        }

    }

}

fun KIMIPackage.installFileManager() {
    exporter.exportClass(FileManager::class.java, "FileManager")
    exporter.exportStaticProperty(FileManager::class.java, "defaultManager", true, true)
    exporter.exportStaticProperty(FileManager::class.java, "documentDirectory", true, true)
    exporter.exportStaticProperty(FileManager::class.java, "libraryDirectory", true, true)
    exporter.exportStaticProperty(FileManager::class.java, "cacheDirectory", true, true)
    exporter.exportStaticProperty(FileManager::class.java, "temporaryDirectory", true, true)
    exporter.exportStaticProperty(FileManager::class.java, "jsBundleDirectory", true, true)
    exporter.exportMethodToJavaScript(FileManager::class.java, "subpaths")
    exporter.exportMethodToJavaScript(FileManager::class.java, "createDirectory")
    exporter.exportMethodToJavaScript(FileManager::class.java, "createFile")
    exporter.exportMethodToJavaScript(FileManager::class.java, "readFile")
    exporter.exportMethodToJavaScript(FileManager::class.java, "removeItem")
    exporter.exportMethodToJavaScript(FileManager::class.java, "copyItem")
    exporter.exportMethodToJavaScript(FileManager::class.java, "moveItem")
    exporter.exportMethodToJavaScript(FileManager::class.java, "fileExists")
    exporter.exportMethodToJavaScript(FileManager::class.java, "dirExists")
}