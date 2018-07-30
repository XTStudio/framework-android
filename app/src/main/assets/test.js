
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

DispatchQueue.main.asyncAfter(3.0, function () {
    var dialog = new UIConfirm("Destory Facebook?")
    dialog.show(function () {
        main.backgroundColor = UIColor.red
    })
})