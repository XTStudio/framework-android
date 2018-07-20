
var main = new UIView
main.frame = { x: 44, y: 44, width: 200, height: 200 }
main.backgroundColor = new UIColor(1, 0, 0, 1)

var subview = new UISwitch
subview.frame = { x: 44, y: 44, width: 44, height: 44 }
subview.backgroundColor = new UIColor(1, 1, 0, 1)
main.addSubview(subview)

// subview.removeFromSuperview()

// console.log("Hello, World!")