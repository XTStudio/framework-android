var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var data = new Data(new Uint8Array([72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33]).buffer)
var mutableData = data.mutable()
mutableData.appendArrayBuffer(new Uint8Array([33, 33, 33]).buffer)
console.error(mutableData.utf8String())