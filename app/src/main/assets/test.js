
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var urlRequest = new URLRequest(URL.URLWithString("http://www.baidu.com"))

var webView = new UIWebView
webView.frame = main.bounds


webView.on('didStart', function () {
    console.log('didStart')
})

webView.on('didFinish', function () {
    console.log('didFinish')
})

webView.on('didFail', function (error) {
    console.log('didFail', error.message)
})

webView.loadRequest(urlRequest)


main.addSubview(webView)
