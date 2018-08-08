class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.view.backgroundColor = UIColor.yellow
        this.redView = new UIView
        this.redView.backgroundColor = UIColor.red
        this.redView.addGestureRecognizer(new UITapGestureRecognizer().on('touch', function () {
            this.redView.backgroundColor = UIColor.green
        }.bind(this)))
        this.view.addSubview(this.redView)
        this.barViewController = new BarViewController
        this.addChildViewController(this.barViewController)
        this.view.addSubview(this.barViewController.view)
        this.childViewControllers[0].view.backgroundColor = UIColor.green
    }

    viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        this.redView.frame = { x: 0, y: 0, width: this.view.bounds.width / 2.0, height: 88 }
        this.barViewController.view.frame = { x: 0, y: 128, width: this.view.bounds.width / 2.0, height: 88 }
    }

}

class BarViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.view.backgroundColor = UIColor.gray
    }

}


var main = new FooViewController