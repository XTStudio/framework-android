
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

var a = new UITextField
a.frame = { x: 44, y: 44, width: 200, height: 200 }
a.backgroundColor = UIColor.yellow

a.returnKeyType = UIReturnKeyType.next

main.addSubview(a)

main.addGestureRecognizer(new UITapGestureRecognizer().on('touch', function () {
    a.scrollRangeToVisible({ location: 0, length: 1 })
}))