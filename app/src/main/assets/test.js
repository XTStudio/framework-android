
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

var tableView = new UITableView
tableView.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }
tableView.register(function (context) {
    return new UITableViewCell(context)
}, "TestCell")
tableView.on('cellForRow', function (indexPath) {
    var cell = tableView.dequeueReusableCell("TestCell", indexPath)
    cell.contentView.backgroundColor = UIColor.red
    cell.contentView.alpha = indexPath.row / 20.0
    return cell
})
tableView.reloadData()

tableView.addGestureRecognizer(new UITapGestureRecognizer().on('touch', function () {
    tableView.backgroundColor = UIColor.yellow
}))

main.addSubview(tableView)
