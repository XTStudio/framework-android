var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var attributedString = new UIAttributedString("Hello, World!", {
    [UIAttributedStringKey.foregroundColor]: UIColor.red,
    [UIAttributedStringKey.font]: new UIFont(28),
    [UIAttributedStringKey.backgroundColor]: UIColor.yellow,
    [UIAttributedStringKey.kern]: 2,
//    [UIAttributedStringKey.strikethroughStyle]: 1,
//    [UIAttributedStringKey.underlineStyle]: 1,
    [UIAttributedStringKey.strokeWidth]: 2,
    [UIAttributedStringKey.strokeColor]: UIColor.blue,
})

var label = new UILabel
label.frame = { x: 0, y: 0, width: 400, height: 200 }
label.attributedText = attributedString

main.addSubview(label)