var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var data = new Data({ utf8String: "Main Thread String" })

DispatchQueue.global.isolate(function () {
    DispatchQueue.global.asyncAfter(3.0, function () {
        console.log("fdhslkajfhl")
    })
})