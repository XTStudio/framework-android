
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

var a = new UISwitch
a.frame = { x: 44, y: 44, width: 66, height: 44 }
a.onTintColor = UIColor.yellow

a.on('valueChanged', function (sender) {
    if (sender.isOn) {
        UIAnimator.linear(0.3, function () {
            main.backgroundColor = UIColor.black
        })
    }
    else {
        UIAnimator.linear(0.3, function () {
            main.backgroundColor = UIColor.white
        })
    }
})

main.addSubview(a)