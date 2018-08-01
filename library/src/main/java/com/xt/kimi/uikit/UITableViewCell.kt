package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

class UITableViewCell: UIView() {

    val selectionView: UIView = UIView()

    val contentView: UIView = UIView()

    var reuseIdentifier: String? = null
        internal set

    var hasSelectionStyle: Boolean = true

    var edo_highlighted = false
        set(value) {
            field = value
            this.onStateChanged()
        }

    var edo_selected = false
        set(value) {
            field = value
            this.onStateChanged()
        }

    internal var currentIndexPath: UIIndexPath? = null

    internal var currentSectionRecord: UITableViewSection? = null

    init {
        this.selectionView.edo_alpha = 0.0
        this.selectionView.edo_backgroundColor = UIColor(0xd0 / 255.0, 0xd0 / 255.0, 0xd0 / 255.0, 1.0)
        addSubview(this.selectionView)
        addSubview(this.contentView)
    }

    private fun onStateChanged() {
        if (this.hasSelectionStyle) {
            if (this.edo_selected || this.edo_highlighted) {
                this.selectionView.edo_alpha = 1.0
            }
            else {
                this.selectionView.edo_alpha = 0.0
            }
        }
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.selectionView.frame = this.bounds
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