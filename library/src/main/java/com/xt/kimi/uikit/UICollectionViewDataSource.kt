package com.xt.kimi.uikit

interface UICollectionViewDataSource {

    fun cellForItemAtIndexPath(collectionView: UICollectionView, indexPath: UIIndexPath): UICollectionViewCell

    fun viewForSupplementaryElementOfKind(collectionView: UICollectionView, kind: String, indexPath: UIIndexPath): UICollectionReusableView?

    fun numberOfSections(collectionView: UICollectionView): Int

    fun numberOfItemsInSection(collectionView: UICollectionView, inSection: Int): Int

}