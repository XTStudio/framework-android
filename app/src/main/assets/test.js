Bundle.js.addResource("ttt/foo.txt", "SGVsbG8sIFdvcmxkIQ==");

var path = Bundle.native.resourcePath("eee", "txt")

// console.log(FileManager.defaultManager.subpaths(FileManager.jsBundleDirectory, false))
//var data = FileManager.defaultManager.readFile(FileManager.jsBundleDirectory + "foo.txt")
console.log(FileManager.defaultManager.subpaths(path).utf8String());