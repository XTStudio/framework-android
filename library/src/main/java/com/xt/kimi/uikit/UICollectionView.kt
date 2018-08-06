package com.xt.kimi.uikit

import android.os.SystemClock
import com.eclipsesource.v8.V8
import com.xt.endo.*
import com.xt.jscore.JSContext
import com.xt.kimi.KIMIPackage
import java.lang.reflect.Executable

class UICollectionView(val collectionViewLayout: UICollectionViewLayout): UIScrollView() {

    internal var kimi_context: V8? = null

    init {
        collectionViewLayout.collectionView = this
    }

    var allowsSelection: Boolean = true

    var allowsMultipleSelection: Boolean = false

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public

    fun register(initializer: EDOCallback, reuseIdentifier: String) {
        this._registeredCells[reuseIdentifier] = initializer
    }

    fun dequeueReusableCell(reuseIdentifier: String, indexPath: UIIndexPath): UICollectionViewCell? {
        this._cellReuseQueues[reuseIdentifier]?.let { reusableCells ->
            if (reusableCells.count() > 0) {
                (reusableCells.removeAt(0) as? UICollectionViewCell)?.let { cell ->
                    return cell
                }
            }
        }
        val kimi_context = this.kimi_context?.takeIf { !it.isReleased } ?: return UICollectionViewCell()
        val initializer = this._registeredCells[reuseIdentifier] ?: return UICollectionViewCell()
        val cell = UICollectionViewCell()
        cell.reuseIdentifier = reuseIdentifier
        cell.collectionView = this
        EDOExporter.sharedExporter.scriptObjectWithObject(cell, kimi_context, true, initializer)
        return cell
    }

    fun allCells(): List<UICollectionViewCell> {
        return _allVisibleViewsDict.values.mapNotNull {
            return@mapNotNull (it as? UICollectionViewCell)
        }
    }

    fun visibleCells(): List<UICollectionViewCell> {
        return _allVisibleViewsDict.values.mapNotNull {
            return@mapNotNull (it as? UICollectionViewCell)?.takeIf { CGRectIntersectsRect(this.visibleBoundRects, it.frame) }
        }
    }

    fun reloadData() {
        this.invalidateLayout()
        _allVisibleViewsDict.forEach {
            it.value.hidden = true
        }
        _allVisibleViewsDict.clear()
        _indexPathsForSelectedItems.forEach {
            this.cellForItemAtIndexPath(it)?.let {
                it.edo_selected = false
                it.edo_highlighted = false
            }
        }
        _indexPathsForSelectedItems.clear()
        _indexPathsForHighlightedItems.clear()
        this.setNeedsLayout()
    }

    fun selectItem(indexPath: UIIndexPath, animated: Boolean) {

    }

    fun deselectItem(indexPath: UIIndexPath, animated: Boolean) {

    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Query Grid

    fun numberOfSections(): Int {
        return this._collectionViewData.numberOfSections()
    }

    fun numberOfItemsInSection(section: Int): Int {
        return this._collectionViewData.numberOfItemsInSection(section)
    }

    fun layoutAttributesForItemAtIndexPath(indexPath: UIIndexPath): UICollectionViewLayoutAttributes? {
        return this.collectionViewLayout.layoutAttributesForItemAtIndexPath(indexPath)
    }

    fun layoutAttributesForSupplementaryElementOfKind(kind: String, indexPath: UIIndexPath): UICollectionViewLayoutAttributes? {
        return this.collectionViewLayout.layoutAttributesForSupplementaryViewOfKind(kind, indexPath)
    }

    fun indexPathForItemAtPoint(point: CGPoint): UIIndexPath? {
        return this.collectionViewLayout.layoutAttributesForElementsInRect(CGRect(point.x, point.y, 1.0, 1.0)).lastOrNull()?.indexPath
    }

    fun indexPathForCell(cell: UICollectionViewCell): UIIndexPath? {
        _allVisibleViewsDict.forEach {
            if (it.key.type == UICollectionViewItemKey.ItemType.cell) {
                if (it.value == cell) {
                    return it.key.indexPath
                }
            }
        }
        return null
    }

    fun cellForItemAtIndexPath(indexPath: UIIndexPath): UICollectionViewCell? {
        _allVisibleViewsDict.forEach {
            if (it.key.type == UICollectionViewItemKey.ItemType.cell) {
                if (it.key.indexPath.isEqual(indexPath)) {
                    return it.value as? UICollectionViewCell
                }
            }
        }
        return null
    }

    fun indexPathsForVisibleItems(): List<UIIndexPath> {
        return this.visibleCells().mapNotNull { return@mapNotNull it.layoutAttributes?.indexPath }
    }

    fun indexPathsForSelectedItems(): List<UIIndexPath> {
        return _indexPathsForSelectedItems.toList()
    }

    // Implementation

    override var edo_contentOffset: CGPoint
        get() = super.edo_contentOffset
        set(value) {
            super.edo_contentOffset = value
            this.layoutCollectionViews()
        }

    private var _allVisibleViewsDict: MutableMap<UICollectionViewItemKey, UIView> = mutableMapOf()
    private var _indexPathsForSelectedItems: MutableSet<UIIndexPath> = mutableSetOf()
    private var _indexPathsForHighlightedItems: MutableSet<UIIndexPath> = mutableSetOf()
    private val _registeredCells: MutableMap<String, EDOCallback> = mutableMapOf()
    internal val _collectionViewData: UICollectionViewData = UICollectionViewData(this, this.collectionViewLayout)
    private val _cellReuseQueues: MutableMap<String, MutableList<UICollectionReusableView>> = mutableMapOf()
    private val _supplementaryViewReuseQueues: MutableMap<String, MutableList<UICollectionReusableView>> = mutableMapOf()
    private val _decorationViewReuseQueues: MutableMap<String, MutableList<UICollectionReusableView>> = mutableMapOf()

    val visibleBoundRects: CGRect
        get() {
            return CGRect(this.edo_contentOffset.x, this.edo_contentOffset.y, this.bounds.width, this.bounds.height)
        }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.layoutCollectionViews()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private

    private fun layoutCollectionViews() {
        _collectionViewData.validateLayoutInRect(this.visibleBoundRects)
        val contentRect = _collectionViewData.collectionViewContentRect()
        this.contentSize = CGSize(contentRect.width, contentRect.height)
        _collectionViewData.validateLayoutInRect(this.visibleBoundRects)
        this.updateVisibleCellsNow(true)
    }

    private fun invalidateLayout() {
        this.collectionViewLayout.invalidateLayout()
        this._collectionViewData.invalidate()
    }

    private fun updateVisibleCellsNow(now: Boolean = false) {
        val e = SystemClock.uptimeMillis()
        val layoutAttributesArray = _collectionViewData.layoutAttributesForElementsInRect(this.visibleBoundRects)
        if (layoutAttributesArray.count() == 0) {
            return
        }
        val itemKeysToAddDict: MutableMap<UICollectionViewItemKey, UICollectionViewLayoutAttributes> = mutableMapOf()
        layoutAttributesArray.forEach { layoutAttributes ->
            val itemKey = UICollectionViewItemKey.collectionItemKeyForLayoutAttributes(layoutAttributes)
            itemKeysToAddDict[itemKey] = layoutAttributes
            var view = _allVisibleViewsDict[itemKey]
            (view as? UICollectionReusableView)?.let {
                it.applyLayoutAttributes(layoutAttributes)
            } ?: kotlin.run {
                when (itemKey.type) {
                    UICollectionViewItemKey.ItemType.cell -> {
                        view = this.createPreparedCellForItemAtIndexPath(itemKey.indexPath, layoutAttributes)
                    }
                    UICollectionViewItemKey.ItemType.supplementaryView -> {
                        view = this.createPreparedSupplementaryViewForElementOfKind(layoutAttributes.representedElementKind, layoutAttributes.indexPath, layoutAttributes)
                    }
                    UICollectionViewItemKey.ItemType.decorationView -> {
                        view = null
                    }
                }
                (view as UICollectionReusableView)?.let {
                    _allVisibleViewsDict[itemKey] = it
                    this.addControlledSubview(it)
                    it.applyLayoutAttributes(layoutAttributes)
                }
            }
        }
        val allVisibleItemKeys: MutableSet<UICollectionViewItemKey> = this._allVisibleViewsDict.keys.toMutableSet()
        itemKeysToAddDict.keys.forEach {
            allVisibleItemKeys.remove(it)
        }
        allVisibleItemKeys.forEach { itemKey ->
            _allVisibleViewsDict[itemKey]?.let { reusableView ->
                reusableView.hidden = true
                _allVisibleViewsDict.remove(itemKey)
                when (itemKey.type) {
                    UICollectionViewItemKey.ItemType.cell -> {
                        // todo: call delegate didEndDisplayingCell
                        this.reuseCell(reusableView as UICollectionViewCell)
                    }
                    UICollectionViewItemKey.ItemType.supplementaryView -> {
                        // todo: call delegate didEndDisplayingSupplementaryView
                        this.reuseSupplementaryView(reusableView as UICollectionReusableView)
                    }
                    UICollectionViewItemKey.ItemType.decorationView -> {
                        this.reuseDecorationView(reusableView as UICollectionReusableView)
                    }
                }
            }
        }
        System.out.println("total used:" + (SystemClock.uptimeMillis() - e))
    }

    private fun createPreparedCellForItemAtIndexPath(indexPath: UIIndexPath, layoutAttributes: UICollectionViewLayoutAttributes): UICollectionViewCell {
        val cell = this.dataSource.cellForItemAtIndexPath(this, indexPath)
        cell.edo_highlighted = _indexPathsForHighlightedItems.firstOrNull { it.isEqual(indexPath) } != null
        cell.edo_selected = _indexPathsForSelectedItems.firstOrNull { it.isEqual(indexPath) } != null
        return cell
    }

    private fun createPreparedSupplementaryViewForElementOfKind(kind: String, indexPath: UIIndexPath, layoutAttributes: UICollectionViewLayoutAttributes): UICollectionReusableView? {
        val view = this.dataSource.viewForSupplementaryElementOfKind(this, kind, indexPath)
        view?.applyLayoutAttributes(layoutAttributes)
        return view
    }

    private fun addControlledSubview(subview: UICollectionReusableView) {
        if (subview.superview == null) {
            this.addSubview(subview)
        }
        subview.hidden = false
    }

    private fun queueReusableView(reusableView: UICollectionReusableView, queue: MutableMap<String, MutableList<UICollectionReusableView>>, identifier: String) {
        reusableView.hidden = true
        reusableView.prepareForReuse()
        val reusableViews = queue[identifier] ?: mutableListOf()
        reusableViews.add(reusableView)
        queue[identifier] = reusableViews
    }

    private fun reuseCell(cell: UICollectionViewCell) {
        val reuseIdentifier = cell.reuseIdentifier ?: return
        this.queueReusableView(cell, _cellReuseQueues, reuseIdentifier)
    }

    private fun reuseSupplementaryView(supplementaryView: UICollectionReusableView) {
        val layoutAttributes = supplementaryView.layoutAttributes ?: return
        val reuseIdentifier = supplementaryView.reuseIdentifier ?: return
        val kindAndIdentifier = "${layoutAttributes.elementKind}/$reuseIdentifier"
        this.queueReusableView(supplementaryView, _supplementaryViewReuseQueues, kindAndIdentifier)
    }

    private fun reuseDecorationView(decorationView: UICollectionReusableView) {
        val reuseIdentifier = decorationView.reuseIdentifier ?: return
        this.queueReusableView(decorationView, _decorationViewReuseQueues, reuseIdentifier)
    }

    // DataSource & Delegate

    var dataSource: UICollectionViewDataSource = object : UICollectionViewDataSource {

        override fun cellForItemAtIndexPath(collectionView: UICollectionView, indexPath: UIIndexPath): UICollectionViewCell {
            (EDOJavaHelper.value(collectionView, "cellForItem", indexPath) as? UICollectionViewCell)?.let {
                return it
            }
            return UICollectionViewCell()
        }

        override fun viewForSupplementaryElementOfKind(collectionView: UICollectionView, kind: String, indexPath: UIIndexPath): UICollectionReusableView? {
            return null
        }

        override fun numberOfSections(collectionView: UICollectionView): Int {
            return (EDOJavaHelper.value(collectionView, "numberOfSections") as? Number)?.toInt() ?: 1
        }

        override fun numberOfItemsInSection(collectionView: UICollectionView, inSection: Int): Int {
            return (EDOJavaHelper.value(collectionView, "numberOfItems", inSection) as? Number)?.toInt() ?: 0
        }

    }

}

val UICollectionElementKindCell = "UICollectionElementKindCell"

class UICollectionViewItemKey(val type: ItemType = ItemType.cell,
                                       val indexPath: UIIndexPath,
                                       val identifier: String) {

    override fun equals(other: Any?): Boolean {
        val right = other as? UICollectionViewItemKey ?: return false
        val leftKey = "${this.type.name}_${this.indexPath.mapKey()}_${this.identifier}"
        val rightKey = "${right.type.name}_${right.indexPath.mapKey()}_${right.identifier}"
        return leftKey == rightKey
    }

    override fun hashCode(): Int {
        return "${this.type.name}_${this.indexPath.mapKey()}_${this.identifier}".hashCode()
    }

    enum class ItemType {
        cell,
        supplementaryView,
        decorationView,
    }

    companion object {

        fun collectionItemKeyForCellWithIndexPath(indexPath: UIIndexPath): UICollectionViewItemKey {
            return UICollectionViewItemKey(
                    ItemType.cell,
                    indexPath,
                    UICollectionElementKindCell
            )
        }

        fun collectionItemKeyForLayoutAttributes(layoutAttributes: UICollectionViewLayoutAttributes): UICollectionViewItemKey {
            return UICollectionViewItemKey(
                    layoutAttributes.representedElementCategory,
                    layoutAttributes.indexPath,
                    layoutAttributes.representedElementKind
            )
        }

    }

}

fun KIMIPackage.installUICollectionView() {
    exporter.exportClass(UICollectionView::class.java, "UICollectionView", "UIScrollView")
    exporter.exportProperty(UICollectionView::class.java, "collectionViewLayout", true)
    exporter.exportProperty(UICollectionView::class.java, "allowsSelection")
    exporter.exportProperty(UICollectionView::class.java, "allowsMultipleSelection")
    exporter.exportMethodToJavaScript(UICollectionView::class.java, "register")
    exporter.exportMethodToJavaScript(UICollectionView::class.java, "dequeueReusableCell")
    exporter.exportMethodToJavaScript(UICollectionView::class.java, "reloadData")
    exporter.exportInitializer(UICollectionView::class.java) {
        val collectionView = UICollectionView(it.firstOrNull() as? UICollectionViewLayout ?: UICollectionViewFlowLayout())
        collectionView.kimi_context = JSContext.currentContext?.runtime
        return@exportInitializer collectionView
    }
}