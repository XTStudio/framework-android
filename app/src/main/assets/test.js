class TestCell extends UICollectionViewCell {

    constructor(context) {
        super(context)
        this.contentView.backgroundColor = UIColor.gray
        this.on("selected", function (cell, selected) {
            cell.contentView.backgroundColor = selected ? UIColor.red : UIColor.gray
        })
        // this.on("highlighted", function (cell, highlighted) {
        //     cell.contentView.backgroundColor = highlighted ? UIColor.yellow : UIColor.gray
        // })
    }

}


var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var layout = new UICollectionViewFlowLayout()

var collectionView = new UICollectionView(layout)
collectionView.register(function (context) {
    return new TestCell(context)
}, "TestCell")
collectionView.on("numberOfSections", function () {
    return 1
})
collectionView.on("numberOfItems", function () {
    return 1000
})
collectionView.on("cellForItem", function (indexPath) {
    return collectionView.dequeueReusableCell("TestCell", indexPath)
})
collectionView.on('didSelectItem', function (indexPath) {
    DispatchQueue.main.asyncAfter(0.35, function () {
        collectionView.selectItem(new UIIndexPath(indexPath.row + 1, indexPath.section), true)
    })
})
// collectionView.on("didSelectItem", function (indexPath, cell) {
//     cell.contentView.backgroundColor = UIColor.red
// })
// collectionView.on("didDeselectItem", function (indexPath, cell) {
//     cell.contentView.backgroundColor = UIColor.gray
// })

// layout.scrollDirection = UICollectionViewScrollDirection.horizontal
layout.on("sizeForItem", function (indexPath) {
    return { width: 88, height: 88 }
})
layout.on("insetForSection", function (inSection) {
    return { top: 44, left: 22, bottom: 44, right: 22 }
})
// layout.on("minimumLineSpacing", function () {
//     return 1
// })
// layout.on("minimumInteritemSpacing", function () {
//     return 1
// })
collectionView.backgroundColor = UIColor.yellow
collectionView.frame = main.bounds
collectionView.reloadData()

main.addSubview(collectionView)
