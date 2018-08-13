class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "Test"
        var imageView = new UIImageView
        imageView.frame = {x: 44, y: 44, width: 200, height: 88}
        imageView.contentMode = UIViewContentMode.scaleAspectFit
//        imageView.layer.cornerRadius = 44
        imageView.layer.masksToBounds = true
        imageView.loadImageWithURLString("http://img.hb.aicdn.com/625d4850d10e332a08c5655b9c6e30d9143138cfee08b-g7Ar2k_sq320")
        this.view.addSubview(imageView)
    }

}


var main = new UINavigationController(new FooViewController)

