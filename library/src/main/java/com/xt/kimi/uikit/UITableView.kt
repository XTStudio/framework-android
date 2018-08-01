package com.xt.kimi.uikit

import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import com.eclipsesource.v8.V8
import com.xt.endo.*
import com.xt.jscore.JSContext
import com.xt.kimi.KIMIPackage
import com.xt.kimi.foundation.DispatchQueue
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs

// @Reference https://github.com/BigZaphod/Chameleon/blob/master/UIKit/Classes/UITableView.m

class UITableView: UIScrollView() {

    internal var kimi_context: V8? = null

    var rowHeight: Double = 44.0

    var tableHeaderView: UIView? = null
        set(value) {
            field?.removeFromSuperview()
            field = value
            this._setContentSize()
            value?.let { this.addSubview(it) }
            this._layoutTableView()
        }

    var tableFooterView: UIView? = null
        set(value) {
            field?.removeFromSuperview()
            field = value
            this._setContentSize()
            value?.let { this.addSubview(it) }
            this._layoutTableView()
        }

    var separatorColor: UIColor? = UIColor(224.0 / 255.0, 224.0 / 255.0, 224.0 / 255.0, 1.0)
        set(value) {
            field = value
            this.setNeedsDisplay()
        }

    var separatorInset: UIEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)
        set(value) {
            field = value
            this.setNeedsDisplay()
        }

    var allowsSelection: Boolean = true

    var allowsMultipleSelection: Boolean = false

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
        return EDOJavaHelper.value(this, "numberOfSections") as? Int ?: 1
    }

    fun numberOfRows(inSection: Int): Int {
        return EDOJavaHelper.value(this, "numberOfRows", inSection) as? Int ?: 0
    }

    fun heightForRow(indexPath: UIIndexPath): Double {
        return (EDOJavaHelper.value(this, "heightForRow", indexPath) as? Number)?.toDouble() ?: this.rowHeight
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
    private var _selectedRows: MutableSet<String> = mutableSetOf()
    private var _highlightedRow: String? = null
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
                        var cell = availableCells[indexPath.mapKey()] ?: this.cellForRow(indexPath)
                        cell.currentIndexPath = indexPath
                        cell.currentSectionRecord = sectionRecord
                        this._cachedCells[indexPath.mapKey()] = cell
                        availableCells.remove(indexPath.mapKey())
                        cell.edo_highlighted = this._highlightedRow == indexPath.mapKey()
                        cell.edo_selected = this._selectedRows.contains(indexPath.mapKey())
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

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val canvas = canvas ?: return
        this.separatorColor?.takeIf { it.a > 0.0 }?.let { separatorColor ->
            separatorPaint.reset()
            separatorPaint.strokeWidth = 1f
            separatorPaint.color = separatorColor.toInt()
            this._cachedCells.values
                    .filter { !it.hidden }
                    .filter {
                        val currentIndexPath = it.currentIndexPath ?: return@filter false
                        val currentSectionRecord = it.currentSectionRecord ?: return@filter false
                        return@filter currentIndexPath.row > 0 && currentIndexPath.row < currentSectionRecord.numberOfRows
                    }
                    .forEach {
                canvas.drawLine(
                        (this.separatorInset.left * scale).toFloat(),
                        ((it.frame.y - this.edo_contentOffset.y) * scale).toFloat(),
                        ((this.bounds.width - this.separatorInset.left - this.separatorInset.right) * scale).toFloat(),
                        ((it.frame.y - this.edo_contentOffset.y) * scale).toFloat(),
                        separatorPaint)
            }
        }
    }

    // Touches

    private var firstTouchPoint: CGPoint? = null

    override fun touchesBegan(touches: Set<UITouch>) {
        super.touchesBegan(touches)
        if (!this.tracking) {
            val firstTouch = touches.firstOrNull() ?: return
            var hitTestView = firstTouch.view
            var cellShouldHighlighted = true
            while (hitTestView != null) {
                if (hitTestView is UITableViewCell) {
                    break
                }
                if (hitTestView.gestureRecognizers.count() > 0) {
                    cellShouldHighlighted = false
                }
                hitTestView = hitTestView.superview
            }
            if (cellShouldHighlighted) {
                this.firstTouchPoint = firstTouch.windowPoint
                (hitTestView as? UITableViewCell)?.let {
                    this.postDelayed({
                        if (this@UITableView.firstTouchPoint == null) { return@postDelayed }
                        this@UITableView._highlightedRow = it.currentIndexPath?.mapKey()
                        it.edo_highlighted = true
                    }, 150)
                }
            }
        }
    }

    override fun touchesMoved(touches: Set<UITouch>) {
        super.touchesMoved(touches)
        this.firstTouchPoint?.let { firstTouchPoint ->
            val firstTouch = touches.firstOrNull() ?: return
            if (abs((firstTouch.windowPoint?.y ?: 0.0) - firstTouchPoint.y) > 8) {
                this._highlightedRow = null
                this._cachedCells.values.forEach { it.edo_highlighted = false }
                this.firstTouchPoint = null
            }
        }
    }

    override fun touchesEnded(touches: Set<UITouch>) {
        super.touchesEnded(touches)
        this.firstTouchPoint = null
        this._highlightedRow = null
        this._cachedCells.values.forEach { it.edo_highlighted = false }
    }

    override fun touchesCancelled(touches: Set<UITouch>) {
        super.touchesCancelled(touches)
        this.firstTouchPoint = null
        this._highlightedRow = null
        this._cachedCells.values.forEach { it.edo_highlighted = false }
    }

    companion object {

        private val separatorPaint = Paint()

    }

}

internal class UITableViewSection {

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
    exporter.exportProperty(UITableView::class.java, "tableHeaderView")
    exporter.exportProperty(UITableView::class.java, "tableFooterView")
    exporter.exportProperty(UITableView::class.java, "separatorColor")
    exporter.exportProperty(UITableView::class.java, "separatorInset")
    exporter.exportProperty(UITableView::class.java, "allowsSelection")
    exporter.exportProperty(UITableView::class.java, "allowsMultipleSelection")
    exporter.exportMethodToJavaScript(UITableView::class.java, "register")
    exporter.exportMethodToJavaScript(UITableView::class.java, "dequeueReusableCell")
    exporter.exportMethodToJavaScript(UITableView::class.java, "reloadData")
}