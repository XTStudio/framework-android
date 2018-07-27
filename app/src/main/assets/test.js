
var main = new UIView
main.frame = { x: 0, y: 88, width: 300, height: 300 }

var sLayer = new CAShapeLayer
sLayer.frame = { x: 0, y: 0, width: 300, height: 300 }

var path = new UIBezierPath
path.moveTo({x: 100, y: 100})
path.addLineTo({x: 200, y: 100})
path.addLineTo({x: 200, y: 200})
path.addLineTo({x: 100, y: 200})
path.closePath()
sLayer.path = path
sLayer.fillColor = UIColor.red
sLayer.lineWidth = 4.0
sLayer.strokeColor = UIColor.blue
sLayer.lineDashPattern = [10, 5]
sLayer.lineDashPhase = 0

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