
var main = new UIView
main.frame = { x: 44, y: 88, width: 300, height: 300 }
main.backgroundColor = UIColor.black

var imageView = new UIImageView
imageView.contentMode = UIViewContentMode.scaleAspectFill
imageView.clipsToBounds = true
imageView.frame = { x: 0, y: 0, width: 100, height: 300 }
imageView.image = new UIImage({ name: "animal" })

main.addSubview(imageView)

DispatchQueue.main.asyncAfter(3.0, function () {
    UIAnimator.shared.linear(3.0, function () {
        imageView.frame = { x: 0, y: 0, width: 300, height: 300 }
    }, function () { })
})