package com.xt.kimi.foundation

import android.content.Context
import android.content.SharedPreferences
import com.xt.endo.EDOExporter
import com.xt.kimi.KIMIPackage

class UserDefaults(val suite: String?) {

    private val sharedPreferences: SharedPreferences? = EDOExporter.sharedExporter.applicationContext?.getSharedPreferences("com.xt.userdefaults.${this.suite ?: "standard"}", Context.MODE_PRIVATE)

    fun valueForKey(forKey: String): Any? {
        return this.sharedPreferences?.all?.get(forKey)
    }

    fun setValue(value: Any?, forKey: String) {
        if (value == null) {
            this.sharedPreferences?.edit()?.remove(forKey)?.apply()
        }
        else {
            (value as? String)?.let {
                this.sharedPreferences?.edit()?.putString(forKey, it)?.apply()
            }
            (value as? Int)?.let {
                this.sharedPreferences?.edit()?.putInt(forKey, it)?.apply()
            }
            (value as? Double)?.let {
                this.sharedPreferences?.edit()?.putFloat(forKey, it.toFloat())?.apply()
            }
            (value as? Boolean)?.let {
                this.sharedPreferences?.edit()?.putBoolean(forKey, it)?.apply()
            }
        }
    }

    fun reset() {
        this.sharedPreferences?.edit()?.clear()?.apply()
    }

    fun dump(): Any? {
        return this.sharedPreferences?.all ?: emptyMap<String, Any>()
    }

    companion object {

        @JvmField val standard: UserDefaults = UserDefaults(null)

    }

}

fun KIMIPackage.installUserDefaults() {
    exporter.exportClass(UserDefaults::class.java, "UserDefaults")
    exporter.exportStaticProperty(UserDefaults::class.java, "standard", true, true)
    exporter.exportMethodToJavaScript(UserDefaults::class.java, "valueForKey")
    exporter.exportMethodToJavaScript(UserDefaults::class.java, "setValue")
    exporter.exportMethodToJavaScript(UserDefaults::class.java, "reset")
    exporter.exportMethodToJavaScript(UserDefaults::class.java, "dump")
    exporter.exportInitializer(UserDefaults::class.java) {
        return@exportInitializer UserDefaults(it.firstOrNull() as? String)
    }
}