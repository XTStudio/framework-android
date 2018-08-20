class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "Test"
        this.on("statusBarStyle", function(){
            return this.s === true ? UIStatusBarStyle.default : UIStatusBarStyle.lightContent
        }.bind(this))
        DispatchQueue.main.asyncAfter(3.0, function(){
            this.s = true
            this.setNeedsStatusBarAppearanceUpdate()
        }.bind(this))
        var textField = new UITextField
        textField.backgroundColor = UIColor.yellow
        textField.frame = {x:44, y: 500, width: 200, height: 44}
        this.view.addSubview(textField)
    }

    viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
    }

}


var main = new UINavigationController(new FooViewController)

