package com.xt.kimi.uikit

import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import com.eclipsesource.v8.V8
import com.xt.endo.*
import com.xt.jscore.JSContext
import com.xt.kimi.KIMIPackage
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

// @Reference https://github.com/BigZaphod/Chameleon/blob/master/UIKit/Classes/UITableView.m

open class UITableView: UIScrollView() {

    internal var kimi_context: V8? = null

    var rowHeight: Double = 44.0

    var tableHeaderView: UIView? = null
        set(value) {
            field?.removeFromSuperview()
            field = value
            EDOJavaHelper.valueChanged(this, "tableHeaderView")
            this._setContentSize()
            value?.let { this.addSubview(it) }
            this._layoutTableView()
            this._layoutSectionHeaders()
            this._layoutSectionFooters()
        }

    var tableFooterView: UIView? = null
        set(value) {
            field?.removeFromSuperview()
            field = value
            EDOJavaHelper.valueChanged(this, "tableFooterView")
            this._setContentSize()
            value?.let { this.addSubview(it) }
            this._layoutTableView()
            this._layoutSectionHeaders()
            this._layoutSectionFooters()
        }

    var separatorColor: UIColor? = UIColor(0xbc / 255.0, 0xba / 255.0, 0xc1 / 255.0, 0.75)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "separatorColor")
            this.setNeedsDisplay()
        }

    var separatorInset: UIEdgeInsets = UIEdgeInsets(0.0, 15.0, 0.0, 0.0)
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "separatorInset")
            this.setNeedsDisplay()
        }

    var allowsSelection: Boolean = true
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "allowsSelection")
        }

    var allowsMultipleSelection: Boolean = false
        set(value) {
            field = value
            EDOJavaHelper.valueChanged(this, "allowsMultipleSelection")
        }

    fun register(clazz: Class<*>, reuseIdentifier: String) {
        this._registeredCellsClass[reuseIdentifier] = clazz as Class<UITableViewCell>
    }

    fun register(initializer: EDOCallback, reuseIdentifier: String) {
        this._registeredCells[reuseIdentifier] = initializer
    }

    fun dequeueReusableCell(reuseIdentifier: String, indexPath: UIIndexPath): UITableViewCell? {
        this._reusableCells.firstOrNull { it.reuseIdentifier == reuseIdentifier }?.let {
            this._reusableCells.remove(it)
            return it
        }
        val cell = this._registeredCellsClass[reuseIdentifier]?.newInstance() ?: UITableViewCell()
        cell.reuseIdentifier = reuseIdentifier
        this._registeredCells[reuseIdentifier]?.let { initializer ->
            val kimi_context = this.kimi_context?.takeIf { !it.isReleased } ?: return@let
            EDOExporter.sharedExporter.scriptObjectWithObject(cell, kimi_context, true, initializer)
        }
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
        this._layoutSectionHeaders()
        this._layoutSectionFooters()
    }

    fun selectRow(indexPath: UIIndexPath, animated: Boolean) {
        if (!this.allowsMultipleSelection) {
            this._selectedRows.forEach {  indexPathKey ->
                this._cachedCells.values.firstOrNull { it.currentIndexPath?.mapKey() == indexPathKey }?.let {
                    it.edo_selected = false
                    EDOJavaHelper.emit(it, "selected", it, false, false)
                }
            }
            this._selectedRows.clear()
        }
        this._selectedRows.add(indexPath.mapKey())
        this._cachedCells.values.firstOrNull { it.currentIndexPath?.mapKey() == indexPath.mapKey() }?.let {
            if (animated) {
                UIAnimator.shared.linear(0.30, EDOCallback.createWithBlock { _ ->
                    it.edo_selected = true
                }, null)
            }
            else {
                it.edo_selected = true
            }
            EDOJavaHelper.emit(it, "selected", it, true, animated)
        }
    }

    fun deselectRow(indexPath: UIIndexPath, animated: Boolean) {
        this._selectedRows.remove(indexPath.mapKey())
        this._cachedCells.values.firstOrNull { it.currentIndexPath?.mapKey() == indexPath.mapKey() }?.let {
            if (animated) {
                UIAnimator.shared.linear(0.30, EDOCallback.createWithBlock { _ ->
                    it.edo_selected = false
                }, null)
            }
            else {
                it.edo_selected = false
            }
            EDOJavaHelper.emit(it, "selected", it, false, animated)
        }
    }

    // DataSource & Delegate

    open fun numberOfSections(): Int {
        return EDOJavaHelper.value(this, "numberOfSections") as? Int ?: 1
    }

    open fun numberOfRows(inSection: Int): Int {
        return EDOJavaHelper.value(this, "numberOfRows", inSection) as? Int ?: 0
    }

    open fun heightForRow(indexPath: UIIndexPath): Double {
        return (EDOJavaHelper.value(this, "heightForRow", indexPath) as? Number)?.toDouble() ?: this.rowHeight
    }

    open fun cellForRow(indexPath: UIIndexPath): UITableViewCell {
        (EDOJavaHelper.value(this, "cellForRow", indexPath) as? UITableViewCell)?.let {
            return it
        }
        return UITableViewCell()
    }

    open fun viewForHeader(inSection: Int): UIView? {
        return EDOJavaHelper.value(this, "viewForHeader", inSection) as? UIView
    }

    open fun heightForHeader(inSection: Int): Double {
        return (EDOJavaHelper.value(this, "heightForHeader", inSection) as? Number)?.toDouble() ?: 0.0
    }

    open fun viewForFooter(inSection: Int): UIView? {
        return EDOJavaHelper.value(this, "viewForFooter", inSection) as? UIView
    }

    open fun heightForFooter(inSection: Int): Double {
        return (EDOJavaHelper.value(this, "heightForFooter", inSection) as? Number)?.toDouble() ?: 0.0
    }

    open fun didSelectRow(indexPath: UIIndexPath) {

    }

    open fun didDeselectRow(indexPath: UIIndexPath) {

    }

    // Implementation

    override var edo_contentOffset: CGPoint
        get() = super.edo_contentOffset
        set(value) {
            if (super.edo_contentOffset.y == value.y) { return }
            super.edo_contentOffset = value
            this._layoutTableView()
            this._layoutSectionHeaders()
            this._layoutSectionFooters()
        }

    init {
        this.alwaysBounceVertical = true
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this._layoutTableView()
        this._layoutSectionHeaders()
        this._layoutSectionFooters()
    }

    private val _registeredCellsClass: MutableMap<String, Class<UITableViewCell>> = mutableMapOf()
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
            this.viewForHeader(section)?.let {
                this.addSubview(it)
                sectionRecord.headerView = it
                sectionRecord.headerHeight = this.heightForHeader(section)
            } ?: kotlin.run {
                sectionRecord.headerHeight = 0.0
            }
            this.viewForFooter(section)?.let {
                this.addSubview(it)
                sectionRecord.footerView = it
                sectionRecord.footerHeight = this.heightForFooter(section)
            } ?: kotlin.run {
                sectionRecord.footerHeight = 0.0
            }
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
                (this.tableHeaderView?.frame?.height ?: 0.0) + _sections.map { it.sectionHeight() }.sum() + (this.tableFooterView?.frame?.height ?: 0.0)
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
                var startIndex: Int
                var left = 0
                var right = max(0, sectionRecord.numberOfRows - 1)
                while (true) {
                    if (abs(right - left) <= 1) {
                        startIndex = left
                        break
                    }
                    val mid = ceil((right.toDouble() + left.toDouble()) / 2.0).toInt()
                    val indexPath = UIIndexPath(mid, section)
                    val rowRect = this._rectForRowAtIndexPath(indexPath)
                    if (rowRect.y <= this.edo_contentOffset.y && rowRect.y + rowRect.height >= this.edo_contentOffset.y) {
                        startIndex = mid
                        break
                    }
                    else if (rowRect.y + rowRect.height < this.edo_contentOffset.y) {
                        left = mid
                    }
                    else if (rowRect.y > this.edo_contentOffset.y) {
                        right = mid
                    }
                }
                var renderCount = 0
                for (row in startIndex until numberOfRows) {
                    renderCount++
                    val indexPath = UIIndexPath(row, section)
                    val rowRect = this._rectForRowAtIndexPath(indexPath)
                    if (CGRectIntersectsRect(rowRect, visibleBounds) && rowRect.height > 0) {
                        var cell = availableCells[indexPath.mapKey()] ?: this.cellForRow(indexPath)
                        cell.currentIndexPath = indexPath
                        cell.currentSectionRecord = sectionRecord
                        this._cachedCells[indexPath.mapKey()] = cell
                        availableCells.remove(indexPath.mapKey())
                        cell.edo_highlighted = this._highlightedRow == indexPath.mapKey()
                        EDOJavaHelper.emit(cell, "highlighted", cell, cell.edo_highlighted, false)
                        cell.edo_selected = this._selectedRows.contains(indexPath.mapKey())
                        EDOJavaHelper.emit(cell, "selected", cell, cell.edo_selected, false)
                        cell.frame = rowRect
                        cell.edo_backgroundColor = this.edo_backgroundColor
                        if (cell.superview == null) {
                            this.insertSubviewAtIndex(cell, 0)
                        }
                        cell.hidden = false
                    }
                    else if (renderCount > 10) {
                        break
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

    private fun _layoutSectionHeaders() {
        var lastHeight = 0.0
        var nextHeight = 0.0
        this._sections.forEach { sectionRecord ->
            nextHeight += sectionRecord.sectionHeight()
            val footerHeight = sectionRecord.footerView?.frame?.height ?: 0.0
            val boxHeight = nextHeight - footerHeight
            if (this.edo_contentOffset.y >= lastHeight && this.edo_contentOffset.y <= boxHeight) {
                sectionRecord.headerView?.let {
                    if (this.edo_contentOffset.y >= boxHeight - it.frame.height) {
                        it.frame = CGRect(0.0, this.edo_contentOffset.y - (this.edo_contentOffset.y - (boxHeight - it.frame.height)), it.frame.width, it.frame.height)
                    }
                    else {
                        it.frame = CGRect(0.0, this.edo_contentOffset.y, it.frame.width, it.frame.height)
                    }
                }
            }
            else {
                sectionRecord.headerView?.let {
                    it.frame = CGRect(0.0, lastHeight, it.frame.width, it.frame.height)
                }
            }
            lastHeight += sectionRecord.sectionHeight()
        }
    }

    private fun _layoutSectionFooters() {
        var lastHeight = 0.0
        var nextHeight = 0.0
        this._sections.forEach { sectionRecord ->
            nextHeight += sectionRecord.sectionHeight()
            val headerHeight = sectionRecord.headerView?.frame?.height ?: 0.0
            val boxHeight = lastHeight + headerHeight
            if (this.edo_contentOffset.y + this.bounds.height >= boxHeight && this.edo_contentOffset.y + this.bounds.height <= nextHeight) {
                sectionRecord.footerView?.let {
                    if (it.frame.height > this.edo_contentOffset.y + this.bounds.height - boxHeight) {
                        it.frame = CGRect(0.0, (this.edo_contentOffset.y + this.bounds.height - it.frame.height) - ((this.edo_contentOffset.y + this.bounds.height - boxHeight) - it.frame.height), it.frame.width, it.frame.height)
                    }
                    else {
                        it.frame = CGRect(0.0, this.edo_contentOffset.y + this.bounds.height - it.frame.height, it.frame.width, it.frame.height)
                    }
                }
            }
            else {
                sectionRecord.footerView?.let {
                    it.frame = CGRect(0.0, nextHeight - it.frame.height, it.frame.width, it.frame.height)
                }
            }
            lastHeight += sectionRecord.sectionHeight()
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

    override fun touchesBegan(touches: Set<UITouch>) {
        super.touchesBegan(touches)
        val firstTouch = touches.firstOrNull() ?: return
        this.handleTouch(UITouchPhase.began, firstTouch)
    }

    override fun touchesMoved(touches: Set<UITouch>) {
        super.touchesMoved(touches)
        val firstTouch = touches.firstOrNull() ?: return
        this.handleTouch(UITouchPhase.moved, firstTouch)
    }

    override fun touchesEnded(touches: Set<UITouch>) {
        super.touchesEnded(touches)
        val firstTouch = touches.firstOrNull() ?: return
        this.handleTouch(UITouchPhase.ended, firstTouch)
    }

    override fun touchesCancelled(touches: Set<UITouch>) {
        super.touchesCancelled(touches)
        val firstTouch = touches.firstOrNull() ?: return
        this.handleTouch(UITouchPhase.cancelled, firstTouch)
    }

    private var firstTouchPoint: CGPoint? = null

    private var firstTouchCell: UITableViewCell? = null

    private fun handleTouch(phase: UITouchPhase, currentTouch: UITouch) {
        if (!this.allowsSelection) { return }
        when (phase) {
            UITouchPhase.began -> {
                if (!this.tracking) {
                    var hitTestView = currentTouch.view
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
                        this.firstTouchPoint = currentTouch.windowPoint
                        (hitTestView as? UITableViewCell)?.let {
                            this.firstTouchCell = it
                            this.postDelayed({
                                if (this@UITableView.firstTouchPoint == null) { return@postDelayed }
                                this@UITableView._highlightedRow = it.currentIndexPath?.mapKey()
                                it.edo_highlighted = true
                                EDOJavaHelper.emit(it, "highlighted", it, true, false)
                            }, 150)
                        }
                    }
                }
            }
            UITouchPhase.moved -> {
                this.firstTouchPoint?.let { firstTouchPoint ->
                    if (UIView.recognizedGesture != null || abs((currentTouch.windowPoint?.y ?: 0.0) - firstTouchPoint.y) > 8) {
                        this._highlightedRow = null
                        this._cachedCells.values.forEach {
                            it.edo_highlighted = false
                            EDOJavaHelper.emit(it, "highlighted", it, true, false)
                        }
                        this.firstTouchPoint = null
                        this.firstTouchCell = null
                    }
                }
            }
            UITouchPhase.ended -> {
                this.firstTouchCell?.let { cell ->
                    this._highlightedRow = null
                    if (!this.allowsMultipleSelection) {
                        this._selectedRows.forEach {  indexPathKey ->
                            this._cachedCells.values.firstOrNull { it.currentIndexPath?.mapKey() == indexPathKey }?.let {
                                it.edo_selected = false
                                EDOJavaHelper.emit(it, "selected", it, false, false)
                                EDOJavaHelper.emit(this, "didDeselectRow", it.currentIndexPath, it)
                            }
                        }
                        this._selectedRows.clear()
                    }
                    this.firstTouchPoint = null
                    this.firstTouchCell = null
                    this._highlightedRow = null
                    this._cachedCells.values.forEach {
                        it.edo_highlighted = false
                        EDOJavaHelper.emit(it, "highlighted", it, false, false)
                    }
                    cell.currentIndexPath?.mapKey()?.let {
                        if (this._selectedRows.contains(it)) {
                            this._selectedRows.remove(it)
                        }
                        else {
                            this._selectedRows.add(it)
                        }
                    }
                    cell.edo_selected = !cell.edo_selected
                    EDOJavaHelper.emit(cell, "selected", cell, cell.edo_selected, false)
                    if (cell.edo_selected) {
                        cell.currentIndexPath?.let { this.didSelectRow(it) }
                        EDOJavaHelper.emit(this, "didSelectRow", cell.currentIndexPath, cell)
                    }
                    else {
                        cell.currentIndexPath?.let { this.didDeselectRow(it) }
                        EDOJavaHelper.emit(this, "didDeselectRow", cell.currentIndexPath, cell)
                    }
                } ?: kotlin.run {
                    this.firstTouchPoint = null
                    this.firstTouchCell = null
                    this._highlightedRow = null
                    this._cachedCells.values.forEach {
                        it.edo_highlighted = false
                        EDOJavaHelper.emit(it, "highlighted", it, false, false)
                    }
                }
            }
            UITouchPhase.cancelled -> {
                this.firstTouchPoint = null
                this.firstTouchCell = null
                this._highlightedRow = null
                this._cachedCells.values.forEach {
                    it.edo_highlighted = false
                    EDOJavaHelper.emit(it, "highlighted", it, false, false)
                }
            }
        }
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

    fun sectionHeight(): Double {
        return this.headerHeight + this.rowsHeight + this.footerHeight
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
    exporter.exportProperty(UITableView::class.java, "tableHeaderView", false, true, true)
    exporter.exportProperty(UITableView::class.java, "tableFooterView", false, true, true)
    exporter.exportProperty(UITableView::class.java, "separatorColor", false, true, true)
    exporter.exportProperty(UITableView::class.java, "separatorInset", false, true, true)
    exporter.exportProperty(UITableView::class.java, "allowsSelection", false, true, true)
    exporter.exportProperty(UITableView::class.java, "allowsMultipleSelection", false, true, true)
    exporter.exportMethodToJavaScript(UITableView::class.java, "register")
    exporter.exportMethodToJavaScript(UITableView::class.java, "dequeueReusableCell")
    exporter.exportMethodToJavaScript(UITableView::class.java, "reloadData")
    exporter.exportMethodToJavaScript(UITableView::class.java, "selectRow")
    exporter.exportMethodToJavaScript(UITableView::class.java, "deselectRow")
}