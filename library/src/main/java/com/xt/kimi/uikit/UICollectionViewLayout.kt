package com.xt.kimi.uikit

import com.xt.endo.*
import com.xt.kimi.KIMIPackage

open class UICollectionViewLayout {

    open val layoutAttributesClass: Class<UICollectionViewLayoutAttributes> = UICollectionViewLayoutAttributes::class.java

    var collectionView: UICollectionView? = null
        internal set

    open fun prepareLayout() { }

    open fun invalidateLayout() {
        this.collectionView?._collectionViewData?.invalidate()
        this.collectionView?.setNeedsLayout()
    }

    open fun layoutAttributesForElementsInRect(rect: CGRect): List<UICollectionViewLayoutAttributes> {
        return emptyList()
    }

    open fun layoutAttributesForItemAtIndexPath(indexPath: UIIndexPath): UICollectionViewLayoutAttributes? {
        return null
    }

    open fun layoutAttributesForSupplementaryViewOfKind(kind: String, indexPath: UIIndexPath): UICollectionViewLayoutAttributes? {
        return null
    }

    open fun layoutAttributesForDecorationViewOfKind(kind: String, indexPath: UIIndexPath): UICollectionViewLayoutAttributes? {
        return null
    }

    open fun collectionViewContentSize(): CGSize {
        return CGSize(0.0, 0.0)
    }

}

class UICollectionViewLayoutAttributes(val indexPath: UIIndexPath,
                                       internal val elementKind: String = "",
                                       internal val representedElementCategory: UICollectionViewItemKey.ItemType = UICollectionViewItemKey.ItemType.cell) {

    var frame: CGRect = CGRect(0.0, 0.0, 0.0, 0.0)
    var center: CGPoint = CGPoint(0.0, 0.0)
    var size: CGSize = CGSize(0.0, 0.0)
    var transform: CGAffineTransform = CGAffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
    var alpha: Double = 1.0
    var zIndex: Int = 0
    var hidden: Boolean = false

    var representedElementKind: String = this.elementKind

    fun isDecorationView(): Boolean {
        return representedElementCategory == UICollectionViewItemKey.ItemType.decorationView
    }

    fun isSupplementaryView(): Boolean {
        return representedElementCategory == UICollectionViewItemKey.ItemType.supplementaryView
    }

    fun isCell(): Boolean {
        return representedElementCategory == UICollectionViewItemKey.ItemType.cell
    }

}

fun KIMIPackage.installUICollectionViewLayout() {
    exporter.exportClass(UICollectionViewLayout::class.java, "UICollectionViewLayout")
    exporter.exportMethodToJavaScript(UICollectionViewLayout::class.java, "invalidateLayout")
    exporter.exportClass(UICollectionViewFlowLayout::class.java, "UICollectionViewFlowLayout", "UICollectionViewLayout")
    exporter.exportProperty(UICollectionViewFlowLayout::class.java, "minimumLineSpacing")
    exporter.exportProperty(UICollectionViewFlowLayout::class.java, "minimumInteritemSpacing")
    exporter.exportProperty(UICollectionViewFlowLayout::class.java, "itemSize")
    exporter.exportProperty(UICollectionViewFlowLayout::class.java, "headerReferenceSize")
    exporter.exportProperty(UICollectionViewFlowLayout::class.java, "footerReferenceSize")
    exporter.exportProperty(UICollectionViewFlowLayout::class.java, "sectionInset")
    exporter.exportProperty(UICollectionViewFlowLayout::class.java, "scrollDirection")
    exporter.exportEnum("UICollectionViewScrollDirection", mapOf(
            Pair("vertical", UICollectionViewScrollDirection.vertical),
            Pair("horizontal", UICollectionViewScrollDirection.horizontal)
    ))
}