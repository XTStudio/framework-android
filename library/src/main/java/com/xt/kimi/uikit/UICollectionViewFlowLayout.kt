package com.xt.kimi.uikit

import com.xt.endo.CGPoint
import com.xt.endo.CGRect
import com.xt.endo.CGSize
import com.xt.endo.UIEdgeInsets
import kotlin.math.max
import kotlin.math.min

enum class UICollectionViewScrollDirection {
    vertical,
    horizontal
}

private val UIFlowLayoutCommonRowHorizontalAlignmentKey = "UIFlowLayoutCommonRowHorizontalAlignmentKey"
private val UIFlowLayoutLastRowHorizontalAlignmentKey = "UIFlowLayoutLastRowHorizontalAlignmentKey"
private val UIFlowLayoutRowVerticalAlignmentKey = "UIFlowLayoutRowVerticalAlignmentKey"

enum class UIFlowLayoutHorizontalAlignment {
    left,
    center,
    right,
    justify,
}

class UIGridLayoutInfo {

    var sections: MutableList<UIGridLayoutSection> = mutableListOf()
    var rowAlignmentOptions: Map<String, Any> = mapOf()
    var usesFloatingHeaderFooter = false
    var dimension: Double = 0.0
    var horizontal = false
    var leftToRight = false
    var contentSize: CGSize = CGSize(0.0, 0.0)
    private var _isValid = false

    fun frameForItemAtIndexPath(indexPath: UIIndexPath): CGRect {
        val section = this.sections[indexPath.section]
        val itemFrame: CGRect
        if (section.fixedItemSize) {
            itemFrame = CGRect(0.0, 0.0, section.itemSize.width, section.itemSize.height)
        }
        else {
            itemFrame = section.items[indexPath.row].itemFrame
        }
        return itemFrame
    }

    fun addSection(): UIGridLayoutSection {
        val section = UIGridLayoutSection()
        section.layoutInfo = this
        this.sections.add(section)
        this.invalidate(false)
        return section
    }

    fun invalidate(arg: Boolean) {
        _isValid = false
    }

    fun snapshot(): UIGridLayoutInfo {
        val layoutInfo = UIGridLayoutInfo()
        layoutInfo.sections = this.sections
        layoutInfo.rowAlignmentOptions = this.rowAlignmentOptions
        layoutInfo.usesFloatingHeaderFooter = this.usesFloatingHeaderFooter
        layoutInfo.dimension = this.dimension
        layoutInfo.horizontal = this.horizontal
        layoutInfo.leftToRight = this.leftToRight
        layoutInfo.contentSize = this.contentSize
        return layoutInfo
    }

}

class UIGridLayoutSection {

    var items: MutableList<UIGridLayoutItem> = mutableListOf()
    var rows: MutableList<UIGridLayoutRow> = mutableListOf()
    var fixedItemSize: Boolean = false
    var itemSize: CGSize = CGSize(0.0, 0.0)
    var itemsCount: Int = 0
        get() {
            return if (this.fixedItemSize) field else this.items.count()
        }

    var verticalInterstice: Double = 0.0
    var horizontalInterstice: Double = 0.0
    var sectionMargins: UIEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)

    var frame = CGRect(0.0, 0.0, 0.0, 0.0)
    var headerFrame = CGRect(0.0, 0.0, 0.0, 0.0)
    var footerFrame = CGRect(0.0, 0.0, 0.0, 0.0)
    var headerDimension: Double = 0.0
    var footerDimension: Double = 0.0
    var layoutInfo: UIGridLayoutInfo? = null
    var rowAlignmentOptions: Map<String, Any> = mapOf()

    var otherMargin: Double = 0.0
    var beginMargin: Double = 0.0
    var endMargin: Double = 0.0
    var actualGap: Double = 0.0
    var lastRowBeginMargin: Double = 0.0
    var lastRowEndMargin: Double = 0.0
    var lastRowActualGap: Double = 0.0
    var lastRowIncomplete = false
    var itemsByRowCount = 0
    var indexOfImcompleteRow = 0

    private var _isValid = false

    fun recomputeFromIndex(index: Int) {
        this.invalidate()
        this.computeLayout()
    }

    fun invalidate() {
        _isValid = false
        this.rows = mutableListOf()
    }

    fun computeLayout() {
        if (!_isValid) {
            val layoutInfo = this.layoutInfo ?: return
            assert(this.rows.count() == 0)
            val sectionSize = CGSizeMutable(0.0, 0.0)
            var rowIndex = 0
            var itemIndex = 0
            var itemsByRowCount = 0
            var dimensionLeft = 0.0
            var row: UIGridLayoutRow? = null
            val headerFooterDimension: Double = layoutInfo.dimension
            var dimension = headerFooterDimension
            if (layoutInfo.horizontal) {
                dimension -= this.sectionMargins.top + sectionMargins.bottom
                this.headerFrame = CGRect(sectionSize.width, 0.0, headerDimension, headerFooterDimension)
                sectionSize.width += headerDimension + sectionMargins.right
            }
            else {
                dimension -= this.sectionMargins.left + this.sectionMargins.right
                this.headerFrame = CGRect(0.0, sectionSize.height, headerFooterDimension, this.headerDimension)
                sectionSize.height += this.headerDimension + this.sectionMargins.top
            }
            val spacing = if (layoutInfo.horizontal) this.verticalInterstice else this.horizontalInterstice
            while (itemIndex <= this.itemsCount) {
                val finishCycle = itemIndex >= this.itemsCount
                var item: UIGridLayoutItem? = null
                if (!finishCycle) {
                    item = if (this.fixedItemSize) null else this.items[itemIndex]
                }
                val itemSize = if (this.fixedItemSize) this.itemSize else CGSize(item?.itemFrame?.width ?: 0.0, item?.itemFrame?.height ?: 0.0)
                var itemDimension = if (layoutInfo.horizontal) itemSize.height else itemSize.width
                if (itemsByRowCount > 0) {
                    itemDimension += spacing
                }
                if (dimensionLeft < itemDimension || finishCycle) {
                    row?.let { row ->
                        this.itemsByRowCount = max(itemsByRowCount, this.itemsByRowCount)
                        row.itemCount = itemsByRowCount
                        if (!finishCycle) {
                            this.indexOfImcompleteRow = rowIndex
                        }
                        row.layoutRow()
                        if (layoutInfo.horizontal) {
                            row.rowFrame = CGRect(sectionSize.width, this.sectionMargins.top, row.rowFrame.width, row.rowSize.height)
                            sectionSize.height = max(row.rowSize.height, sectionSize.height)
                            sectionSize.width += row.rowSize.width + (if (finishCycle) 0.0 else this.horizontalInterstice)
                        }
                        else {
                            row.rowFrame = CGRect(this.sectionMargins.left, sectionSize.height, row.rowSize.width, row.rowSize.height)
                            sectionSize.height += row.rowSize.height + (if (finishCycle) 0.0 else this.verticalInterstice)
                            sectionSize.width = max(row.rowSize.width, sectionSize.width)
                        }
                    }
                    if (!finishCycle) {
                        row?.complete = true
                        row = this.addRow()
                        row.fixedItemSize = this.fixedItemSize
                        row.index = rowIndex
                        this.indexOfImcompleteRow = rowIndex
                        rowIndex++
                        if (itemsByRowCount > 0) {
                            itemDimension -= spacing
                        }
                        dimensionLeft = dimension - itemDimension
                        itemsByRowCount = 0
                    }
                }
                else {
                    dimensionLeft -= itemDimension
                }
                item?.let {
                    row?.addItem(it)
                }
                itemIndex++
                itemsByRowCount++
            }
            if (layoutInfo.horizontal) {
                sectionSize.width += this.sectionMargins.right
                this.footerFrame = CGRect(sectionSize.width, 0.0, this.footerDimension, headerFooterDimension)
                sectionSize.width += this.footerDimension
            }
            else {
                sectionSize.height += this.sectionMargins.bottom
                this.footerFrame = CGRect(0.0, sectionSize.height, headerFooterDimension, this.footerDimension)
                sectionSize.height += this.footerDimension
            }
            this.frame = CGRect(0.0, 0.0, sectionSize.width, sectionSize.height)
            _isValid = true
        }
    }

    fun addItem(): UIGridLayoutItem {
        val item = UIGridLayoutItem()
        item.section = this
        this.items.add(item)
        return item
    }

    fun addRow(): UIGridLayoutRow {
        val item = UIGridLayoutRow()
        item.section = this
        this.rows.add(item)
        return item
    }

    fun snapshot(): UIGridLayoutSection {
        val snapshotSection = UIGridLayoutSection()
        snapshotSection.items = this.items.toMutableList()
        snapshotSection.rows = this.rows.toMutableList()
        snapshotSection.verticalInterstice = this.verticalInterstice
        snapshotSection.horizontalInterstice = this.horizontalInterstice
        snapshotSection.sectionMargins = this.sectionMargins
        snapshotSection.frame = this.frame
        snapshotSection.headerFrame = this.headerFrame
        snapshotSection.footerFrame = this.footerFrame
        snapshotSection.headerDimension = this.headerDimension
        snapshotSection.footerDimension = this.footerDimension
        snapshotSection.layoutInfo = this.layoutInfo
        snapshotSection.rowAlignmentOptions = this.rowAlignmentOptions
        snapshotSection.fixedItemSize = this.fixedItemSize
        snapshotSection.itemSize = this.itemSize
        snapshotSection.itemsCount = this.itemsCount
        snapshotSection.otherMargin = this.otherMargin
        snapshotSection.beginMargin = this.beginMargin
        snapshotSection.endMargin = this.endMargin
        snapshotSection.actualGap = this.actualGap
        snapshotSection.lastRowBeginMargin = this.lastRowBeginMargin
        snapshotSection.lastRowEndMargin = this.lastRowEndMargin
        snapshotSection.lastRowActualGap = this.lastRowActualGap
        snapshotSection.lastRowIncomplete = this.lastRowIncomplete
        snapshotSection.itemsByRowCount = this.itemsByRowCount
        snapshotSection.indexOfImcompleteRow = this.indexOfImcompleteRow
        return snapshotSection
    }

}

class UIGridLayoutItem {
    var section: UIGridLayoutSection? = null
    var rowObject: UIGridLayoutRow? = null
    var itemFrame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
}

class UIGridLayoutRow {

    var section: UIGridLayoutSection? = null
    var items: MutableList<UIGridLayoutItem> = mutableListOf()
    var rowSize = CGSize(0.0, 0.0)
    var rowFrame = CGRect(0.0, 0.0, 0.0, 0.0)
    var index = 0
    var complete = false
    var fixedItemSize = false
    var itemCount = 0
        get() {
            return if (this.fixedItemSize) field else this.items.count()
        }

    private var _isValid = false

    fun addItem(item: UIGridLayoutItem) {
        this.items.add(item)
        item.rowObject = this
        this.invalidate()
    }

    fun layoutRow() {
        this.layoutRowAndGenerateRectArray(false)
    }

    fun itemRects(): List<CGRect> {
        return this.layoutRowAndGenerateRectArray(true) ?: listOf()
    }

    fun invalidate() {
        _isValid = false
        rowSize = CGSize(0.0, 0.0)
        rowFrame = CGRect(0.0, 0.0, 0.0, 0.0)
    }

    fun snapshot(): UIGridLayoutRow {
        val snapshotRow = UIGridLayoutRow()
        snapshotRow.section = this.section
        snapshotRow.items = this.items
        snapshotRow.rowSize = this.rowSize
        snapshotRow.rowFrame = this.rowFrame
        snapshotRow.index = this.index
        snapshotRow.complete = this.complete
        snapshotRow.fixedItemSize = this.fixedItemSize
        snapshotRow.itemCount = this.itemCount
        return snapshotRow
    }

    private fun layoutRowAndGenerateRectArray(generateRectArray: Boolean): List<CGRect>? {
        val rects: MutableList<CGRect>? = if (generateRectArray) mutableListOf() else null
        if (!_isValid || generateRectArray) {
            val section = this.section ?: return null
            val isHorizontal = section.layoutInfo?.horizontal ?: false
            val isLastRow = section.indexOfImcompleteRow == this.index
            val horizontalAlignment = section.rowAlignmentOptions[if (isLastRow) UIFlowLayoutLastRowHorizontalAlignmentKey else UIFlowLayoutCommonRowHorizontalAlignmentKey] as? UIFlowLayoutHorizontalAlignment ?: return null
            var leftOverSpace = section.layoutInfo?.dimension ?: 0.0
            if (isHorizontal) {
                leftOverSpace -= section.sectionMargins.top + section.sectionMargins.bottom
            }
            else {
                leftOverSpace -= section.sectionMargins.left + section.sectionMargins.right
            }
            var usedItemCount = 0
            var itemIndex = 0
            val spacing = if (isHorizontal) section.verticalInterstice else section.horizontalInterstice
            while (itemIndex < this.itemCount || isLastRow) {
                var nextItemSize: Double
                if (!this.fixedItemSize) {
                    val item = this.items[min(itemIndex, this.itemCount - 1)]
                    nextItemSize = if (isHorizontal) item.itemFrame.height else item.itemFrame.width
                }
                else {
                    nextItemSize = if (isHorizontal) section.itemSize.height else section.itemSize.width
                }
                if (itemIndex > 0) {
                    nextItemSize += spacing
                }
                if (leftOverSpace < nextItemSize) {
                    break
                }
                leftOverSpace -= nextItemSize
                itemIndex++
                usedItemCount = itemIndex
            }
            var itemOffset = CGPointMutable(0.0, 0.0)
            if (horizontalAlignment == UIFlowLayoutHorizontalAlignment.right) {
                itemOffset.x += leftOverSpace
            }
            else if (horizontalAlignment == UIFlowLayoutHorizontalAlignment.center ||
                    (horizontalAlignment == UIFlowLayoutHorizontalAlignment.justify && usedItemCount == 1)) {
                itemOffset.x += leftOverSpace / 2.0
            }
            val interSpacing = if (usedItemCount <= 1) 0.0 else leftOverSpace / (usedItemCount - 1).toDouble()
            var frame = CGRect(0.0, 0.0, 0.0, 0.0)
            var itemFrame = CGRectMutable(0.0, 0.0, section.itemSize.width, section.itemSize.height)
            for (itemIndex in 0 until this.itemCount) {
                var item: UIGridLayoutItem? = null
                if (!this.fixedItemSize) {
                    item = this.items[itemIndex]
                    itemFrame = CGRectMutable(item.itemFrame.x, item.itemFrame.y, item.itemFrame.width, item.itemFrame.height)
                }
                if (isHorizontal) {
                    itemFrame.y = itemOffset.y
                    itemOffset.y += itemFrame.height + section.verticalInterstice
                    if (horizontalAlignment == UIFlowLayoutHorizontalAlignment.justify) {
                        itemOffset.y += interSpacing
                    }
                }
                else {
                    itemFrame.x = itemOffset.x
                    itemOffset.x += itemFrame.width + section.horizontalInterstice
                    if (horizontalAlignment == UIFlowLayoutHorizontalAlignment.justify) {
                        itemOffset.x += interSpacing
                    }
                }
                val iFrame = CGRect(itemFrame.x, itemFrame.y, itemFrame.width, itemFrame.height)
                item?.itemFrame = iFrame
                rects?.add(iFrame)
                frame = CGRectUnion(frame, iFrame)
            }
            rowSize = CGSize(frame.width, frame.height)
            _isValid = true
        }
        return rects?.toList()
    }

}

class UICollectionViewFlowLayout: UICollectionViewLayout() {

    private var _data: UIGridLayoutInfo? = null

    var minimumLineSpacing: Double = 10.0

    var minimumInteritemSpacing: Double = 10.0

    var itemSize: CGSize = CGSize(50.0, 50.0)

    var headerReferenceSize: CGSize = CGSize(0.0, 0.0)

    var footerReferenceSize: CGSize = CGSize(0.0, 0.0)

    var sectionInset: UIEdgeInsets = UIEdgeInsets(0.0, 0.0, 0.0, 0.0)

    var scrollDirection: UICollectionViewScrollDirection = UICollectionViewScrollDirection.vertical

    var rowAlignmentOptions: Map<String, Any> = mapOf(
            Pair(UIFlowLayoutCommonRowHorizontalAlignmentKey, UIFlowLayoutHorizontalAlignment.justify),
            Pair(UIFlowLayoutLastRowHorizontalAlignmentKey, UIFlowLayoutHorizontalAlignment.justify),
            Pair(UIFlowLayoutRowVerticalAlignmentKey, UIFlowLayoutHorizontalAlignment.center)
    )

    override fun prepareLayout() {
        super.prepareLayout()
    }

    override fun layoutAttributesForElementsInRect(rect: CGRect): List<UICollectionViewLayoutAttributes> {
        if (_data == null) {
            this.prepareLayout()
        }
        return super.layoutAttributesForElementsInRect(rect)
    }

    internal fun sizeForItem(indexPath: UIIndexPath): CGSize {
        return this.itemSize
    }

    internal fun insetForSection(inSection: Int): UIEdgeInsets {
        return this.sectionInset
    }

    internal fun minimumLineSpacing(inSection: Int): Double {
        return this.minimumLineSpacing
    }

    internal fun minimumInteritemSpacing(inSection: Int): Double {
        return this.minimumInteritemSpacing
    }

    internal fun referenceSizeForHeader(inSection: Int): CGSize {
        return this.headerReferenceSize
    }

    internal fun referenceSizeForFooter(inSection: Int): CGSize {
        return this.footerReferenceSize
    }

}