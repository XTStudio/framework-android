package com.xt.kimi.uikit

import android.os.SystemClock
import com.eclipsesource.v8.V8
import com.xt.endo.*
import com.xt.jscore.JSContext
import com.xt.kimi.KIMIPackage

// @Reference https://github.com/BigZaphod/Chameleon/blob/master/UIKit/Classes/UITableView.m

class UITableView: UIScrollView() {

    internal var kimi_context: V8? = null

    var rowHeight: Double = 44.0

    var tableHeaderView: UIView? = null

    var tableFooterView: UIView? = null

    fun register(initializer: EDOCallback, reuseIdentifier: String) {
        this._registeredCells[reuseIdentifier] = initializer
    }

    fun dequeueReusableCell(reuseIdentifier: String, indexPath: UIIndexPath): UITableViewCell? {
        this._reusableCells.firstOrNull { it.reuseIdentifier == reuseIdentifier }?.let {
            this._reusableCells.remove(it)
            return it
        }
        val kimi_context = this.kimi_context?.takeIf { !it.isReleased } ?: return UITableViewCell()
        val initializer = this._registeredCells[reuseIdentifier] ?: return UITableViewCell()
        val cell = UITableViewCell()
        cell.reuseIdentifier = reuseIdentifier
        EDOExporter.sharedExporter.scriptObjectWithObject(cell, kimi_context, true, initializer)
        return cell
    }

    fun reloadData() {
        _cachedCells.forEach { it.value.removeFromSuperview() }
        _reusableCells.forEach { it.removeFromSuperview() }
        _reusableCells.clear()
        _cachedCells.clear()
        this._updateSectionsCache()
        this._setContentSize()
        this._needsReload = false
        this._layoutTableView()
    }

    // DataSource & Delegate

    fun numberOfSections(): Int {
        return 1
    }

    fun numberOfRows(inSection: Int): Int {
        return 20
    }

    fun heightForRow(indexPath: UIIndexPath): Double {
        return this.rowHeight
    }

    fun cellForRow(indexPath: UIIndexPath): UITableViewCell {
        (EDOJavaHelper.value(this, "cellForRow", indexPath) as? UITableViewCell)?.let {
            return it
        }
        return UITableViewCell()
    }

    // Implementation

    override var edo_contentOffset: CGPoint
        get() = super.edo_contentOffset
        set(value) {
            if (super.edo_contentOffset.y == value.y) { return }
            super.edo_contentOffset = value
            this._layoutTableView()
        }

    init {
        this.alwaysBounceVertical = true
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this._layoutTableView()
    }

    private val _registeredCells: MutableMap<String, EDOCallback> = mutableMapOf()
    private val _reusableCells: MutableSet<UITableViewCell> = mutableSetOf()
    private val _cachedCells: MutableMap<String, UITableViewCell> = mutableMapOf()
    private var _selectedRow: UIIndexPath? = null
    private var _highlightedRow: UIIndexPath? = null
    private var _needsReload = false
    private val _sections: MutableList<UITableViewSection> = mutableListOf()

    private fun _updateSectionsCache() {
        _sections.forEach {
            it.headerView?.removeFromSuperview()
            it.footerView?.removeFromSuperview()
        }
        _sections.clear()
        val numberOfSections = this.numberOfSections()
        (0 until numberOfSections).forEach { section ->
            val numberOfRowsInSection = this.numberOfRows(section)
            val sectionRecord = UITableViewSection()
            val rowHeights = (0 until numberOfRowsInSection).map { row ->
                return@map this.heightForRow(UIIndexPath(row, section))
            }
            val totalRowsHeight = rowHeights.sum()
            sectionRecord.rowsHeight = totalRowsHeight
            sectionRecord.setNumberOfRows(numberOfRowsInSection, rowHeights)
            _sections.add(sectionRecord)
        }
    }

    private fun _updateSectionsCacheIfNeeded() {
        if (_sections.count() == 0) {
            this._updateSectionsCache()
        }
    }

    private fun _setContentSize() {
        this._updateSectionsCacheIfNeeded()
        this.contentSize = CGSize(
                0.0,
                (this.tableHeaderView?.frame?.height ?: 0.0) + _sections.map { it.rowsHeight }.sum() + (this.tableFooterView?.frame?.height ?: 0.0)
                )
    }

    private fun _layoutTableView() {
        val boundsSize = CGSize(this.bounds.width, this.bounds.height)
        val contentOffset = this.edo_contentOffset.y
        val visibleBounds = CGRect(0.0, contentOffset, boundsSize.width, boundsSize.height)
        var tableHeight = 0.0
        this.tableHeaderView?.let {
            it.frame = CGRect(0.0, 0.0, boundsSize.width, it.frame.height)
            tableHeight += it.frame.height
        }
        val availableCells = this._cachedCells.toMutableMap()
        val numberOfSections = this._sections.count()
        this._cachedCells.clear()
        (0 until numberOfSections).forEach { section ->
            val sectionRect = this._rectForSection(section)
            tableHeight += sectionRect.height
            if (CGRectIntersectsRect(sectionRect, visibleBounds)) {
                val headerRect = this._rectForHeaderInSection(section)
                val footerRect = this._rectForFooterInSection(section)
                val sectionRecord = this._sections[section]
                val numberOfRows = sectionRecord.numberOfRows
                sectionRecord.headerView?.let {
                    it.frame = headerRect
                }
                sectionRecord.footerView?.let {
                    it.frame = footerRect
                }
                (0 until numberOfRows).forEach { row ->
                    val indexPath = UIIndexPath(row, section)
                    val rowRect = this._rectForRowAtIndexPath(indexPath)
                    if (CGRectIntersectsRect(rowRect, visibleBounds) && rowRect.height > 0) {
                        val e = SystemClock.uptimeMillis()
                        var cell = availableCells[indexPath.mapKey()] ?: this.cellForRow(indexPath)
                        System.out.println("used: " + (SystemClock.uptimeMillis() - e))
                        this._cachedCells[indexPath.mapKey()] = cell
                        availableCells.remove(indexPath.mapKey())
                        cell.edo_highlighted = this._highlightedRow?.isEqual(indexPath) ?: false
                        cell.edo_selected = this._selectedRow?.isEqual(indexPath) ?: false
                        cell.frame = rowRect
                        cell.edo_backgroundColor = this.edo_backgroundColor
                        if (cell.superview == null) {
                            this.addSubview(cell)
                        }
                        cell.hidden = false
                    }
                }
            }
        }
        availableCells.values.forEach { cell ->
            cell.reuseIdentifier?.let {
                this._reusableCells.add(cell)
            } ?: kotlin.run {
                cell.hidden = true
            }
        }
        val allCachedCells = this._cachedCells.values
        _reusableCells.forEach { cell ->
            if (CGRectIntersectsRect(cell.frame, visibleBounds) && !allCachedCells.contains(cell)) {
                cell.hidden = true
            }
        }
        this.tableFooterView?.let {
            it.frame = CGRect(0.0, tableHeight, boundsSize.width, it.frame.height)
        }
    }

    private fun _rectForSection(section: Int): CGRect {
        this._updateSectionsCacheIfNeeded()
        return this._CGRectFromVerticalOffset(this._offsetForSection(section), this._sections[section].sectionHeight())
    }

    private fun _rectForHeaderInSection(section: Int): CGRect {
        this._updateSectionsCacheIfNeeded()
        return this._CGRectFromVerticalOffset(this._offsetForSection(section), this._sections[section].headerHeight)
    }

    private fun _rectForFooterInSection(section: Int): CGRect {
        this._updateSectionsCacheIfNeeded()
        val sectionRecord = this._sections[section]
        var offset = this._offsetForSection(section)
        offset += sectionRecord.headerHeight
        offset += sectionRecord.rowsHeight
        return this._CGRectFromVerticalOffset(offset, this._sections[section].footerHeight)
    }

    private fun _rectForRowAtIndexPath(indexPath: UIIndexPath): CGRect {
        this._updateSectionsCacheIfNeeded()
        if (indexPath.section < this._sections.count()) {
            val sectionRecord = this._sections[indexPath.section]
            if (indexPath.row < sectionRecord.numberOfRows) {
                var offset = this._offsetForSection(indexPath.section)
                offset += sectionRecord.headerHeight
                (0 until indexPath.row).forEach { currentRow ->
                    offset += sectionRecord.rowHeights[currentRow]
                }
                return this._CGRectFromVerticalOffset(offset, sectionRecord.rowHeights[indexPath.row])
            }
        }
        return CGRect(0.0, 0.0, 0.0, 0.0)
    }

    private fun _offsetForSection(section: Int): Double {
        var offset = this.tableHeaderView?.frame?.height ?: 0.0
        (0 until section).forEach {
            offset += _sections[it].sectionHeight()
        }
        return offset
    }

    private fun _cellForRow(indexPath: UIIndexPath): UITableViewCell? {
        return this._cachedCells[indexPath.mapKey()]
    }

    private fun _CGRectFromVerticalOffset(offset: Double, height: Double): CGRect {
        return CGRect(0.0, offset, this.bounds.width, height)
    }

}

private class UITableViewSection {

    var rowsHeight: Double = 0.0

    var headerHeight: Double = 0.0

    var footerHeight: Double = 0.0

    var numberOfRows: Int = 0
        private set

    var rowHeights: List<Double> = listOf()
        private set

    var headerView: UIView? = null

    var footerView: UIView? = null

    var headerTitle: String? = null

    var footerTitle: String? = null

    fun sectionHeight(): Double {
        return (this.headerView?.frame?.height ?: 0.0) + this.rowsHeight + (this.footerView?.frame?.height ?: 0.0)
    }

    fun setNumberOfRows(rows: Int, rowHeights: List<Double>) {
        this.numberOfRows = rows
        this.rowHeights = rowHeights
    }

}

fun KIMIPackage.installUITableView() {
    exporter.exportClass(UITableView::class.java, "UITableView", "UIScrollView")
    exporter.exportInitializer(UITableView::class.java) {
        val tableView = UITableView()
        tableView.kimi_context = JSContext.currentContext?.runtime
        return@exportInitializer tableView
    }
    exporter.exportMethodToJavaScript(UITableView::class.java, "register")
    exporter.exportMethodToJavaScript(UITableView::class.java, "dequeueReusableCell")
    exporter.exportMethodToJavaScript(UITableView::class.java, "reloadData")
}