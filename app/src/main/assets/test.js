
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var sampleView = new UIStackView()
sampleView.accessibilityIdentifier = "sample view"
sampleView.frame = { x: 0, y: 0, width: 300, height: 88 }
// sampleView.backgroundColor = UIColor.gray

var redView = new UIView
redView.backgroundColor = UIColor.red
var yellowView = new UIView
yellowView.backgroundColor = UIColor.yellow
var blueView = new UIView
blueView.backgroundColor = UIColor.blue

sampleView.addArrangedSubview(redView)
sampleView.addArrangedSubview(yellowView)
sampleView.addArrangedSubview(blueView)

sampleView.layoutArrangedSubview(yellowView, { width: 50 })
sampleView.layoutArrangedSubview(blueView, { width: 50 })

sampleView.axis = UILayoutConstraintAxis.vertical
sampleView.distribution = UIStackViewDistribution.fill

//

sampleView.layoutArrangedSubview(yellowView)
sampleView.layoutArrangedSubview(blueView)
sampleView.distribution = UIStackViewDistribution.fillEqually
sampleView.alignment = UIStackViewAlignment.fill

//

sampleView.spacing = 30
sampleView.distribution = UIStackViewDistribution.fillProportionally
sampleView.alignment = UIStackViewAlignment.fill

//

sampleView.spacing = 0
sampleView.layoutArrangedSubview(redView, { width: 50 })
sampleView.layoutArrangedSubview(yellowView, { width: 80 })
sampleView.layoutArrangedSubview(blueView, { width: 100 })
sampleView.distribution = UIStackViewDistribution.equalSpacing
sampleView.alignment = UIStackViewAlignment.fill

// //

// sampleView.spacing = 0
// sampleView.layoutArrangedSubview(redView, { width: 50 })
// sampleView.layoutArrangedSubview(yellowView, { width: 80 })
// sampleView.layoutArrangedSubview(blueView, { width: 100 })
// sampleView.distribution = UIStackViewDistribution.equalCentering
// sampleView.alignment = UIStackViewAlignment.fill

// //

// sampleView.layoutArrangedSubview(redView, { width: 50, height: 44 })
// sampleView.layoutArrangedSubview(yellowView, { width: 80, height: 55 })
// sampleView.layoutArrangedSubview(blueView, { width: 100, height: 66 })
// sampleView.alignment = UIStackViewAlignment.leading

// //

// sampleView.layoutArrangedSubview(redView, { width: 50, height: 44 })
// sampleView.layoutArrangedSubview(yellowView, { width: 80, height: 55 })
// sampleView.layoutArrangedSubview(blueView, { width: 100, height: 66 })
// sampleView.alignment = UIStackViewAlignment.center

// //

// sampleView.layoutArrangedSubview(redView, { width: 50, height: 44 })
// sampleView.layoutArrangedSubview(yellowView, { width: 80, height: 55 })
// sampleView.layoutArrangedSubview(blueView, { width: 100, height: 66 })
// sampleView.alignment = UIStackViewAlignment.trailing

main.addSubview(sampleView)
