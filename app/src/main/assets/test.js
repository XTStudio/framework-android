var main = new UIViewController
main.view.backgroundColor = UIColor.green
main.view.addGestureRecognizer(new UITapGestureRecognizer().on("touch", function(){

                                                               var actionSheet = new UIActionSheet
                                                               actionSheet.message = "退出后不会删除任何历史数据，下次登录依然可以使用本帐号。"
                                                               actionSheet.addDangerAction('退出登录', function() {
                                                                                          console.log('b')
                                                                                          });
                                                               actionSheet.addCancelAction('取消', function() {
                                                                                          main.view.backgroundColor = UIColor.yellow
                                                                                          });
                                                               actionSheet.show()
                                                               }))
