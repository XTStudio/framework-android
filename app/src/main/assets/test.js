
var main = new UIView
main.frame = { x: 0, y: 88, width: 300, height: 300 }

var button = new UIButton
button.frame = { x: 44, y: 44, width: 100, height: 44 }
button.setTitle("Click me", UIControlState.normal)
button.on('touchUpInside', function () {
    button.setTitle("Nice!", UIControlState.normal)
})
// button.enabled = false
// button.backgroundColor = UIColor.yellow
// button.contentVerticalAlignment = UIControlContentVerticalAlignment.bottom
// button.contentHorizontalAlignment = UIControlContentHorizontalAlignment.right
// var blueView = new UIView
// blueView.frame = { x: 0, y: 0, width: 22, height: 22 }
// blueView.backgroundColor = UIColor.blue
// button.addSubview(blueView)

main.addSubview(button)