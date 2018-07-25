
var main = new UIView
main.frame = { x: 44, y: 44, width: 300, height: 300 }
main.backgroundColor = new UIColor(1, 0, 0, 1)
main.contentMode = UIViewContentMode.scaleAspectFit
main.layer.masksToBounds = true
main.layer.cornerRadius = 150
main.layer.borderWidth = 10
main.layer.borderColor = new UIColor(0, 0, 0, 1)
// main.layer.opacity = 0.5

var yellowLayer = new CALayer
yellowLayer.frame = { x: 44, y: 44, width: 44, height: 44 }
yellowLayer.backgroundColor = new UIColor(1, 1, 0, 1)
main.layer.addSublayer(yellowLayer)

var blueLayer = new CALayer
blueLayer.frame = { x: 22, y: 200, width: 44, height: 44 }
blueLayer.backgroundColor = new UIColor(0, 1, 1, 1)
main.layer.replaceSublayer(yellowLayer, blueLayer)

// var longPressGesture = new UILongPressGestureRecognizer
// longPressGesture.numberOfTouchesRequired = 1
// longPressGesture.on('began', function () {
//     main.backgroundColor = new UIColor(1, 1, 1, 1)
// })
// longPressGesture.on('changed', function () {
//     main.backgroundColor = new UIColor(1, 1, 0, 1)
// })
// longPressGesture.on('ended', function () {
//     main.backgroundColor = new UIColor(1, 0, 0, 1)
// })
// main.addGestureRecognizer(longPressGesture)

var yellowView = new UISwitch
yellowView.frame = { x: 44, y: 44, width: 44, height: 44 }
yellowView.transform = { a: 1.0, b: 0.0, c: 0.0, d: 1.0, tx: 66.0, ty: 66.0 }
yellowView.backgroundColor = new UIColor(1, 1, 0, 1)
main.addSubview(yellowView)

var blueView = new UIView
blueView.frame = { x: 22, y: 22, width: 88, height: 88 }
blueView.backgroundColor = new UIColor(0, 0, 1, 1)
var tapGesture = new UITapGestureRecognizer
tapGesture.numberOfTapsRequired = 1
tapGesture.on('touch', function () {
    blueView.backgroundColor = new UIColor(1, 1, 0, 1)
})
blueView.addGestureRecognizer(tapGesture)
main.addSubview(blueView)

// if (blueView.isDescendantOfView(main)) {
//     blueView.backgroundColor = new UIColor(1, 1, 1, 1)
// }
// main.exchangeSubview(1, 0)