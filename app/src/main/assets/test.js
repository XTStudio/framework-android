var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var e = Bundle.native.resourceURL('test', 'js')
console.error(e.absoluteString)