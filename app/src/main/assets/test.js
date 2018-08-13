class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "Test"
        this.view.addGestureRecognizer(new UITapGestureRecognizer().on("touch", function(){
                var sub = new UINavigationBarViewController
                sub.navigationBar.backgroundColor = UIColor.yellow
                sub.view.backgroundColor = UIColor.green
                this.navigationController.pushViewController(sub)
            }.bind(this)))
    }

}


var main = new UINavigationController(new FooViewController)



//sub.view.addGestureRecognizer(new UITapGestureRecognizer().on("touch", function(){
//                                                              sub.navigationController.pushViewController(new UIViewController())
//                                                              }))
