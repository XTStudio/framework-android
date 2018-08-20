class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "Test"
        var attributedString = new UIAttributedString("test", {})
        console.log(attributedString.measure({width: Infinity, height: Infinity}))
    }

    viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
    }

}


var main = new UINavigationController(new FooViewController)

