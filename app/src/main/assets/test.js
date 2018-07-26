
var main = new UIView
main.frame = { x: 0, y: 88, width: 300, height: 300 }


var sLayer = new CAGradientLayer
sLayer.frame = { x: 0, y: 0, width: 300, height: 300 }
sLayer.colors = [UIColor.clear, UIColor.red]
sLayer.locations = [0, 1]
sLayer.startPoint = { x: 0.3, y: 0 }
sLayer.endPoint = { x: 0.7, y: 0 }

main.layer.addSublayer(sLayer)

// main.backgroundColor = UIColor.black

// var label = new UILabel
// label.backgroundColor = UIColor.yellow
// label.font = new UIFont(24, "bold", "monospace")
// label.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 300 }
// label.text = "Hello, World!"
// label.textAlignment = UITextAlignment.center
// label.textColor = UIColor.black

// main.addSubview(label)

// DispatchQueue.main.asyncAfter(3.0, function() {
//     label.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width / 2.0, height: 300 }
// })

// console.error(UIDevice.current.identifierForVendor.UUIDString)