var main = new UIViewController
main.view.backgroundColor = UIColor.green
var r = new UIView
r.backgroundColor = UIColor.white
r.frame = {x:44, y:44, width:200, height: 200}
main.view.addSubview(r)
main.view.addGestureRecognizer(new UITapGestureRecognizer().on("touch", function(){
                                                               var menu = new UIMenu()
                                                               menu.addMenuItem("测试", function() {
                                                                                main.view.backgroundColor = UIColor.yellow
                                                                                })
                                                               menu.show(r)
                                                               }))
