class FooViewController extends UIViewController {

    viewDidLoad() {
        super.viewDidLoad()
        this.title = "Test"
        this.scrollView = new UIScrollView
        var refreshControl = new UIRefreshControl
        refreshControl.on("refresh", function(sender) {
            DispatchQueue.main.asyncAfter(3.0, function(){
                sender.endRefreshing()
            })
        })
        var redView = new UIView
        redView.frame = {x: 0, y: 0, width: 44, height: 44}
        redView.backgroundColor = UIColor.red
        this.scrollView.addSubview(redView)
        this.scrollView.addSubview(refreshControl)
//        this.scrollView.contentSize = {width:0, height: 2000}
        this.view.addSubview(this.scrollView)
    }

    viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        this.scrollView.frame = this.view.bounds
    }

}


var main = new UINavigationController(new FooViewController)

