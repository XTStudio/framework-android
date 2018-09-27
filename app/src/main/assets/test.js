var main = new UIViewController
main.view.backgroundColor = UIColor.green
var r = new UIView
r.backgroundColor = UIColor.white
r.frame = {x:44, y:44, width:44, height: 44}
r.touchAreaInsets = {top: 44, left: 44, bottom: 44, right: 44}
main.view.addSubview(r)
r.addGestureRecognizer(new UITapGestureRecognizer().on("touch", function(){
                                                               var menu = new UIMenu()
                                                               menu.addMenuItem("测试", function() {
                                                                                main.view.backgroundColor = UIColor.yellow
                                                                                })
                                                               menu.show(r)
                                                               }))
