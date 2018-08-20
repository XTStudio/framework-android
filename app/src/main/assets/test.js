class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "Test"
        var textField = new UITextField
        textField.backgroundColor = UIColor.yellow
        textField.frame = {x:44, y: 500, width: 200, height: 44}
        this.view.addSubview(textField)
        this.on("keyboardWillShow", function(rect) {
            textField.transform = {a: 1.0, b:0.0, c:0.0, d:1.0, tx:0.0, ty:-rect.height}
        })
        this.on("keyboardWillHide", function(){
        textField.transform = {a: 1.0, b:0.0, c:0.0, d:1.0, tx:0.0, ty:0.0}
        })
    }

    viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
    }

}


var main = new UINavigationController(new FooViewController)

