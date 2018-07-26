
var main = new UIView
main.frame = { x: 44, y: 88, width: 300, height: 300 }
// main.backgroundColor = UIColor.black

var label = new UILabel
label.backgroundColor = UIColor.yellow
label.font = new UIFont(24, "bold", "monospace")
label.frame = { x: 0, y: 0, width: 300, height: 300 }
label.text = "Hello, World!"
label.textAlignment = UITextAlignment.center
label.textColor = UIColor.black

main.addSubview(label)