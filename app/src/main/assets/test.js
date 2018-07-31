
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

var a = new UITextField
a.frame = { x: 44, y: 44, width: 200, height: 44 }
a.backgroundColor = UIColor.yellow
a.text = "Hello"
a.textColor = UIColor.red
a.font = new UIFont(14)
a.textAlignment = UITextAlignment.center
a.placeholder = "Input username"
a.clearsOnBeginEditing = true
a.clearButtonMode = UITextFieldViewMode.whileEditing

// var leftView = new UIView
// leftView.frame = { x: 0, y: 0, width: 22, height: 22 }
// leftView.backgroundColor = UIColor.gray
// a.leftView = leftView
// a.leftViewMode = UITextFieldViewMode.always

// var rightView = new UIView
// rightView.frame = { x: 0, y: 0, width: 22, height: 22 }
// rightView.backgroundColor = UIColor.blue
// a.rightView = rightView
// a.rightViewMode = UITextFieldViewMode.whileEditing
a.autocapitalizationType = UITextAutocapitalizationType.none
a.on('shouldChange', function (_, _, str) {
    if (str === "e") {
        return false
    }
    return true
})


// a.keyboardType = UIKeyboardType.numbersAndPunctuation

// DispatchQueue.main.asyncAfter(3.0, function () {
//     a.focus()
// })
// DispatchQueue.main.asyncAfter(5.0, function () {
//     a.blur()
// })

main.addSubview(a)

main.addGestureRecognizer(new UITapGestureRecognizer().on('touch', function () {
    a.blur()
}))