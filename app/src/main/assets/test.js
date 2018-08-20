class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "Test"
        this.scrollView = new UIScrollView
        var fetchMoreControl = new UIFetchMoreControl
        fetchMoreControl.on("fetch", function(sender) {
            DispatchQueue.main.asyncAfter(3.0, function(){
                this.scrollView.contentSize = {width: 0, height: 3000}
                sender.endFetching()
            }.bind(this))
        }.bind(this))
        var redView = new UIView
        redView.frame = {x: 0, y: 2000 - 44, width: 44, height: 44}
        redView.backgroundColor = UIColor.red
        this.scrollView.addSubview(redView)
        this.scrollView.addSubview(fetchMoreControl)
        this.scrollView.contentSize = {width:0, height: 2000}
        this.view.addSubview(this.scrollView)
    }

    viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        this.scrollView.frame = this.view.bounds
    }

}


var main = new UINavigationController(new FooViewController)

