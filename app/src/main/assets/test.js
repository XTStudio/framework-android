var main = new UIView
main.frame = { x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height }

var filePath = FileManager.temporaryDirectory + '/s/ttt.txt'
// var toPath = FileManager.temporaryDirectory + '/eee.txt'
FileManager.defaultManager.createFile(filePath, new Data({ utf8String: 'Hello, World!' }))
// FileManager.defaultManager.moveItem(filePath, toPath)
// var e = FileManager.defaultManager.readFile(toPath)
console.log(FileManager.defaultManager.subpaths(FileManager.temporaryDirectory))