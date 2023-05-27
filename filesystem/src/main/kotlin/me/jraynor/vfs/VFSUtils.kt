package me.jraynor.vfs

///**
// * Builds a list of paths from the root to the target path
// */
//fun VFS.pathsToRoot(root: VPath, targetPath: VPath): List<VPath> {
//    val paths = mutableListOf<VPath>()
//    var currentPath = targetPath
//    while (currentPath != root) {
//        paths.add(currentPath)
//        currentPath = currentPath.parent
//    }
//    paths.add(root)
//    return paths
//}

fun walk(root: VPath, targetPath: VPath, callback: (VPath) -> Unit) {
    var currentPath = targetPath
    while (currentPath != root) {
        callback(currentPath)
        currentPath = currentPath.parent
    }
    callback(root)
}

