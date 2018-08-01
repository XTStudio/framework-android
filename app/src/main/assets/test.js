
var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }

var tableView = new UITableView
tableView.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: 400 }
tableView.register(function (context) {
    return new UITableViewCell(context)
}, "TestCell")
tableView.on('numberOfSections', function () {
    return 2
})
tableView.on('numberOfRows', function () {
    return 20
})
tableView.on('heightForRow', function () {
    return 88.0
})
tableView.on('cellForRow', function (indexPath) {
    var cell = tableView.dequeueReusableCell("TestCell", indexPath)
    cell.contentView.backgroundColor = UIColor.red
    cell.contentView.alpha = indexPath.row / 20.0
    return cell
})
tableView.contentInset = { top: 22, left: 0, bottom: 22, right: 0 }
tableView.separatorInset = { top: 0, left: 15, bottom: 0, right: 15 }
tableView.reloadData()

// tableView.addGestureRecognizer(new UITapGestureRecognizer().on('touch', function () {
//     tableView.backgroundColor = UIColor.yellow
// }))


var headerView = new UIView
headerView.frame = { x: 0, y: 0, width: 0, height: 88 }
headerView.backgroundColor = UIColor.green
tableView.tableHeaderView = headerView

var footerView = new UIView
footerView.frame = { x: 0, y: 0, width: 0, height: 88 }
footerView.backgroundColor = UIColor.gray
tableView.tableFooterView = footerView

main.addSubview(tableView)
