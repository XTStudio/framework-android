class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "星巴克用星说"
        this.setupNavigationItem()
        // this.view.backgroundColor = UIColor.yellow
        this.redView = new UIView
        this.redView.backgroundColor = UIColor.red
        this.redView.addGestureRecognizer(new UITapGestureRecognizer().on('touch', function () {
            this.navigationController.pushViewController(new BarViewController, true)
            // this.redView.backgroundColor = UIColor.green
        }.bind(this)))
        this.view.addSubview(this.redView)
    }

    setupNavigationItem() {
        var a = new UIBarButtonItem()
        a.title = "发布"
        a.tintColor = UIColor.red
        a.width = 32
        a.on("touchUpInside", function () {
            this.navigationController.pushViewController(new BarViewController, true)
        }.bind(this))
        var b = new UIBarButtonItem()
        // b.title = "测试"
        b.image = new UIImage({name: "location", renderingMode: UIImageRenderingMode.alwaysTemplate})
        this.navigationItem.rightBarButtonItems = [a, b]
    }

    viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        this.redView.frame = { x: 0, y: 44, width: this.view.bounds.width / 2.0, height: 88 }
        this.barViewController.view.frame = { x: 0, y: 128, width: this.view.bounds.width / 2.0, height: 88 }
    }

}

class BarViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "第二页"
        // this.view.backgroundColor = UIColor.gray
        this.view.addGestureRecognizer(new UITapGestureRecognizer().on('touch', function () {
            this.navigationController.popToRootViewController()
        }.bind(this)))
    }

}


var main = new UINavigationController(new FooViewController)

// DispatchQueue.main.asyncAfter(2.0, function () {
//     main.pushViewController(new UIViewController, true)
// })

// DispatchQueue.main.asyncAfter(4.0, function () {
//     main.popToRootViewController()
// })