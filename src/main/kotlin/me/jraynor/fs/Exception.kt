package me.jraynor.fs


sealed class VirtualDiskException(val disk: VirtualDisk, message: String) : RuntimeException() {
    override val message: String? = "[${disk.path}] $message"
}

sealed class VirtualFileException(val file: VirtualFile, message: String) : RuntimeException() {
    override val message: String? = "[${file.path}] $message"

    class WriteToReadOnlyException(file: VirtualFile) :
        VirtualFileException(file, "Tried to write to a read only file.")

    class ListFilesFromFileException(file: VirtualFile) :
        VirtualFileException(file, "Tried to list files from a file. This is not allowed.")

    class ReadFileFromFileException(file: VirtualFile) :
        VirtualFileException(file, "Tried to read a file from a file. This is not allowed.")

    class FileNotFoundException(file: VirtualFile) :
        VirtualFileException(file, "File not found.")
}