
var main = new UIView
main.frame = { x: 0, y: 88, width: 300, height: 300 }

// var sLayer = new CAShapeLayer
// sLayer.frame = { x: 0, y: 0, width: 300, height: 300 }

// var path = new UIBezierPath
// path.moveTo({x: 100, y: 100})
// path.addLineTo({x: 200, y: 100})
// path.addLineTo({x: 200, y: 200})
// path.addLineTo({x: 100, y: 200})
// path.closePath()
// sLayer.path = path
// sLayer.fillColor = UIColor.red
// sLayer.lineWidth = 4.0
// sLayer.strokeColor = UIColor.blue
// sLayer.lineDashPattern = [10, 5]
// sLayer.lineDashPhase = 0

// main.layer.addSublayer(sLayer)


// main.backgroundColor = UIColor.black

var label = new UILabel
label.backgroundColor = UIColor.yellow
label.font = new UIFont(24, "bold", "monospace")
label.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 300 }
label.text = `    欢聚时代集团于2005年4月成立，2012年11月21日在纳斯达克上市（NASDAQ：YY），是全球最大的直播和社交业务服务商。旗下业务覆盖直播、资讯、教育、社交、游戏、金融等领域，核心产品包括但不仅限于YY 、虎牙直播、Bigo、Like、开心斗、Hago、YY交友、欢聚游戏等。其中虎牙直播于2018年5月11日在美国纽交所正式上市，成为集团内部孵化的首家上市公司。
目前集团员工超过4000人，70%以上为研发人员。欢聚时代以AI技术为核心，以用户需求为导向，搭建专业技术核心体系。集团不断革新行业技术水平，实现了富集媒体语音技术，群体通信全球服务技术，多终端在线视频交互技术以及在线互动教育学习技术等多重领域的创新，希望用科技让人们的生活变得更愉悦。`
label.textAlignment = UITextAlignment.left
label.textColor = UIColor.red
label.numberOfLines = 0

main.addSubview(label)

// DispatchQueue.main.asyncAfter(3.0, function() {
//     label.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width / 2.0, height: 300 }
// })

// console.error(UIDevice.current.identifierForVendor.UUIDString)