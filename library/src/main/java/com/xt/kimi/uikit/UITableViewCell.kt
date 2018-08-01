package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UITableViewCell: UIView() {

    val contentView: UIView = UIView()

    var reuseIdentifier: String? = null
        internal set

    var hasSelectionStyle: Boolean = true

    var edo_highlighted = false

    var edo_selected = false

    init {
        addSubview(this.contentView)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.contentView.frame = this.bounds
    }

}

fun KIMIPackage.installUITableViewCell() {
    exporter.exportClass(UITableViewCell::class.java, "UITableViewCell", "UIView")
    exporter.exportProperty(UITableViewCell::class.java, "contentView", true)
    exporter.exportProperty(UITableViewCell::class.java, "reuseIdentifier", true)
    exporter.exportInitializer(UITableViewCell::class.java) {
        return@exportInitializer UITableViewCell()
    }
}