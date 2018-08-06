package com.xt.kimi.foundation

import android.util.Base64
import com.eclipsesource.v8.V8ArrayBuffer
import com.xt.jscore.JSContext
import com.xt.kimi.KIMIPackage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*

open class Data(open val byteArray: ByteArray) {

    fun arrayBuffer(): V8ArrayBuffer? {
        val runtime = JSContext.currentContext?.runtime ?: return null
        val byteBuffer = ByteBuffer.wrap(this.byteArray)
        return V8ArrayBuffer(runtime, byteBuffer)
    }

    fun utf8String(): String? {
        return String(this.byteArray)
    }

    fun base64EncodedData(): Data {
        val encodedByteArray = Base64.encode(this.byteArray, 0)
        return Data(encodedByteArray)
    }

    fun base64EncodedString(): String {
        return Base64.encodeToString(this.byteArray, 0)
    }

    fun mutable(): MutableData {
        return MutableData(this.byteArray)
    }

}

class MutableData(byteArray: ByteArray): Data(byteArray) {

    val byteArrayOutputStream = ByteArrayOutputStream()

    init {
        byteArrayOutputStream.write(byteArray)
    }

    override val byteArray: ByteArray
        get() {
            return this.byteArrayOutputStream.toByteArray()
        }

    fun appendData(data: Data) {
        this.byteArrayOutputStream.write(data.byteArray)
    }

    fun appendArrayBuffer(arrayBuffer: V8ArrayBuffer) {
        this.byteArrayOutputStream.write(arrayBuffer.backingStore.array())
    }

    fun setData(data: Data) {
        this.byteArrayOutputStream.reset()
        this.byteArrayOutputStream.write(data.byteArray)
    }

    fun immutable(): Data {
        return Data(this.byteArray)
    }

}

fun KIMIPackage.installData() {
    exporter.exportClass(Data::class.java, "Data")
    exporter.exportInitializer(Data::class.java) {
        if (0 < it.count()) {
            (it[0] as? Data)?.let {
                return@exportInitializer Data(it.byteArray)
            }
            (it[0] as? V8ArrayBuffer)?.let {
                return@exportInitializer Data(it.backingStore.array())
            }
            (it[0] as? ByteArray)?.let {
                return@exportInitializer Data(it)
            }
            (it[0] as? Map<String, Any>)?.let {
                (it["utf8String"] as? String)?.let {
                    return@exportInitializer Data(it.toByteArray())
                }
                (it["base64EncodedData"] as? Data)?.let {
                    try {
                        return@exportInitializer Data(Base64.decode(it.byteArray, 0))
                    } catch (e: Exception) {
                        return@exportInitializer Data(ByteArray(0))
                    }
                }
                (it["base64EncodedString"] as? String)?.let {
                    try {
                        return@exportInitializer Data(Base64.decode(it, 0))
                    } catch (e: Exception) {
                        return@exportInitializer Data(ByteArray(0))
                    }
                }
            }
        }
        return@exportInitializer Data(ByteArray(0))
    }
    exporter.exportMethodToJavaScript(Data::class.java, "arrayBuffer")
    exporter.exportMethodToJavaScript(Data::class.java, "utf8String")
    exporter.exportMethodToJavaScript(Data::class.java, "base64EncodedData")
    exporter.exportMethodToJavaScript(Data::class.java, "base64EncodedString")
    exporter.exportMethodToJavaScript(Data::class.java, "mutable")
    exporter.exportClass(MutableData::class.java, "MutableData", "Data")
    exporter.exportInitializer(MutableData::class.java) {
        if (0 < it.count()) {
            (it[0] as? MutableData)?.let {
                return@exportInitializer MutableData(it.byteArray)
            }
            (it[0] as? V8ArrayBuffer)?.let {
                return@exportInitializer MutableData(it.backingStore.array())
            }
            (it[0] as? ByteArray)?.let {
                return@exportInitializer MutableData(it)
            }
            (it[0] as? Map<String, Any>)?.let {
                (it["utf8String"] as? String)?.let {
                    return@exportInitializer MutableData(it.toByteArray())
                }
                (it["base64EncodedData"] as? Data)?.let {
                    try {
                        return@exportInitializer MutableData(Base64.decode(it.byteArray, 0))
                    } catch (e: Exception) {
                        return@exportInitializer MutableData(ByteArray(0))
                    }
                }
                (it["base64EncodedString"] as? String)?.let {
                    try {
                        return@exportInitializer MutableData(Base64.decode(it, 0))
                    } catch (e: Exception) {
                        return@exportInitializer MutableData(ByteArray(0))
                    }
                }
            }
        }
        return@exportInitializer MutableData(ByteArray(0))
    }
    exporter.exportMethodToJavaScript(MutableData::class.java, "appendData")
    exporter.exportMethodToJavaScript(MutableData::class.java, "appendArrayBuffer")
    exporter.exportMethodToJavaScript(MutableData::class.java, "setData")
    exporter.exportMethodToJavaScript(MutableData::class.java, "immutable")

}