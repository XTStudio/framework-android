package com.xt.kimi.foundation

import android.content.Context
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage
import java.io.File

class FileManager {

    fun subpaths(atPath: String, deepSearch: Boolean?): List<String> {
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
        try {
            val file = File(atPath)
            return Data(file.readBytes())
        } catch (e: Exception) { }
        return null
    }

    fun removeItem(atPath: String): Error? {
        try {
            val file = File(atPath)
            file.delete()
        } catch (e: Exception) {
            return Error(e.message ?: "unknown error.")
        }
        return null
    }

    fun copyItem(atPath: String, toPath: String): Error? {
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
        val file = File(atPath)
        return try {
            file.isFile
        } catch (e: Exception) { false }
    }

    fun dirExists(atPath: String): Boolean {
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

        private fun mkdirs(file: File) {
            file.parentFile.mkdirs()
        }

    }

}

fun KIMIPackage.installFileManager() {
    exporter.exportClass(FileManager::class.java, "FileManager")
    exporter.exportStaticProperty(FileManager::class.java, "defaultManager", true)
    exporter.exportStaticProperty(FileManager::class.java, "documentDirectory", true)
    exporter.exportStaticProperty(FileManager::class.java, "libraryDirectory", true)
    exporter.exportStaticProperty(FileManager::class.java, "cacheDirectory", true)
    exporter.exportStaticProperty(FileManager::class.java, "temporaryDirectory", true)
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