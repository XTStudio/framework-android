
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

var scrollView = new UIScrollView
scrollView.frame = main.bounds
scrollView.backgroundColor = UIColor.yellow
scrollView.contentInset = { top: 0, left: 44, bottom: 0, right: 44 }

{
    var redView = new UIView
    redView.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }
    redView.backgroundColor = UIColor.red
    scrollView.addSubview(redView)
}
{
    var redView = new UIView
    redView.frame = { x: UIScreen.main.bounds.width, y: 0, width: UIScreen.main.bounds.width, height: 400 }
    redView.backgroundColor = UIColor.gray
    scrollView.addSubview(redView)
}
{
    var redView = new UIView
    redView.frame = { x: UIScreen.main.bounds.width * 2, y: 0, width: UIScreen.main.bounds.width, height: 400 }
    redView.backgroundColor = UIColor.blue
    scrollView.addSubview(redView)
}
{
    var redView = new UIView
    redView.frame = { x: UIScreen.main.bounds.width * 3, y: 0, width: UIScreen.main.bounds.width, height: 400 }
    redView.backgroundColor = UIColor.green
    scrollView.addSubview(redView)
}
{
    var redView = new UIView
    redView.frame = { x: UIScreen.main.bounds.width * 4, y: 0, width: UIScreen.main.bounds.width, height: 400 }
    redView.backgroundColor = UIColor.black
    scrollView.addSubview(redView)
}
scrollView.contentSize = { width: UIScreen.main.bounds.width * 5, height: 0 }

// DispatchQueue.main.asyncAfter(3.0, function () {
//     scrollView.scrollRectToVisible({ x: UIScreen.main.bounds.width * 3, y: 0, width: 100, height: 1 }, true)
// })


main.addSubview(scrollView)