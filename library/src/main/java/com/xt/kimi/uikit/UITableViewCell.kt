package com.xt.kimi.uikit

import com.xt.kimi.KIMIPackage

open class UITableViewCell: UIView() {

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
        this.contentView.edo_backgroundColor = UIColor.white
        addSubview(this.selectionView)
        addSubview(this.contentView)
    }

    private var restoringContentViewBackgroundColor: UIColor? = null

    private fun onStateChanged() {
        if (this.hasSelectionStyle) {
            if (this.edo_selected || this.edo_highlighted) {
                if (restoringContentViewBackgroundColor == null) {
                    restoringContentViewBackgroundColor = this.contentView.edo_backgroundColor
                }
                this.selectionView.edo_alpha = 1.0
                this.contentView.edo_backgroundColor = UIColor.clear
            }
            else {
                this.selectionView.edo_alpha = 0.0
                if (restoringContentViewBackgroundColor != null) {
                    this.contentView.edo_backgroundColor = restoringContentViewBackgroundColor
                    restoringContentViewBackgroundColor = null
                }
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
    exporter.exportProperty(UITableViewCell::class.java, "hasSelectionStyle")
    exporter.exportProperty(UITableViewCell::class.java, "edo_highlighted", true)
    exporter.exportProperty(UITableViewCell::class.java, "edo_selected", true)
    exporter.exportInitializer(UITableViewCell::class.java) {
        return@exportInitializer UITableViewCell()
    }
}