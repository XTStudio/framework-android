
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var layout = new UICollectionViewFlowLayout()

var collectionView = new UICollectionView(layout)
collectionView.register(function (context) {
    return new UICollectionViewCell(context)
}, "TestCell")
collectionView.on("numberOfSections", function () {
    return 1
})
collectionView.on("numberOfItems", function () {
    return 1000
})
collectionView.on("cellForItem", function (indexPath) {
    var cell = collectionView.dequeueReusableCell("TestCell", indexPath)
    cell.contentView.backgroundColor = UIColor.gray
    return cell
})
layout.scrollDirection = UICollectionViewScrollDirection.horizontal
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
