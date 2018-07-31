
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

var a = new UISlider
a.minimumValue = 0
a.maximumValue = 100
a.value = 20
a.on('valueChanged', function (sender) {
    console.log(sender.value)
})
a.frame = { x: 44, y: 44, width: 200, height: 44 }

main.addSubview(a)