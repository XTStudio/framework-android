var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var timer = new Timer(3.0, function () {
    console.log("Test")
}, true)