package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UIIndexPath(val row: Int, val section: Int) {

    fun mapKey(): String {
        return "$row.$section"
    }

    fun isEqual(to: UIIndexPath): Boolean {
        return row == to.row && section == to.section
    }

    override fun equals(other: Any?): Boolean {
        val right = other as? UIIndexPath ?: return false
        return this.row == right.row && this.section == right.section
    }

    override fun hashCode(): Int {
        return "$row.$section".hashCode()
    }

}

fun KIMIPackage.installUIIndexPath() {
    exporter.exportClass(UIIndexPath::class.java, "UIIndexPath")
    exporter.exportInitializer(UIIndexPath::class.java) {
        return@exportInitializer UIIndexPath(
                if (0 < it.count()) it[0] as? Int ?: 0 else 0,
                if (1 < it.count()) it[1] as? Int ?: 0 else 0
        )
    }
    exporter.exportProperty(UIIndexPath::class.java, "row", true, true)
    exporter.exportProperty(UIIndexPath::class.java, "section", true, true)
}