package com.xt.kimi.foundation

import android.util.Base64
import com.xt.kimi.KIMIPackage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

open class Data(open val byteArray: ByteArray) {

    fun arrayBuffer(): ByteBuffer? {
        val byteBuffer = ByteBuffer.allocateDirect(this.byteArray.size)
        byteBuffer.put(this.byteArray)
        return byteBuffer
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

    fun appendArrayBuffer(arrayBuffer: ByteBuffer) {
        val byteArray = ByteArray(arrayBuffer.capacity())
        arrayBuffer.get(byteArray)
        this.byteArrayOutputStream.write(byteArray)
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
            (it[0] as? ByteBuffer)?.let {
                val byteArray = ByteArray(it.capacity())
                it.get(byteArray)
                return@exportInitializer Data(byteArray)
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
            (it[0] as? Data)?.let {
                return@exportInitializer MutableData(it.byteArray)
            }
            (it[0] as? ByteBuffer)?.let {
                val byteArray = ByteArray(it.capacity())
                it.get(byteArray)
                return@exportInitializer MutableData(byteArray)
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