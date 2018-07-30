
var main = new UIView
main.frame = { x: 0, y: 88, width: 300, height: 300 }

var button = new UIButton
button.frame = { x: 44, y: 44, width: 100, height: 44 }
button.setImage(new UIImage({ name: "location" }), UIControlState.normal)
button.setTitle("Click me", UIControlState.normal)
button.setTitleFont(new UIFont(14))
button.imageEdgeInsets = { top: 0, left: 0, bottom: 0, right: 4 }
button.titleEdgeInsets = { top: 0, left: 4, bottom: 0, right: 0 }
button.on('touchUpInside', function () {
    // button.setTitle("Nice!", UIControlState.normal)
})
// button.enabled = false
button.backgroundColor = UIColor.yellow
// button.contentVerticalAlignment = UIControlContentVerticalAlignment.bottom
// button.contentHorizontalAlignment = UIControlContentHorizontalAlignment.right
// var blueView = new UIView
// blueView.frame = { x: 0, y: 0, width: 22, height: 22 }
// blueView.backgroundColor = UIColor.blue
// button.addSubview(blueView)

main.addSubview(button)