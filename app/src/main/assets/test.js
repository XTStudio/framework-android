var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var attributedString = new UIAttributedString("Hello, World!", {
    [UIAttributedStringKey.foregroundColor]: UIColor.red,
})

var label = new UILabel
label.frame = { x: 0, y: 0, width: 300, height: 88 }
label.attributedText = attributedString

main.addSubview(label)