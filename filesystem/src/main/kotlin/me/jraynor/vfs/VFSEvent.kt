package me.jraynor.vfs

/***
 * This class is used to represent an event that occurs in the VFS
 */
data class VFSEvent(
    //The file that the event occurred on
    val file: VFile,
    //The virtual file system that the event occurred in
    val vfs: VFS,
    //The type of event that occurred
    val type: Type,
    //The handles that are open to the file
    val openHandles: () -> List<VHandle>,
) {

    /**
     * The type of event that occurred. This is used to determine what happened to the file.
     */
    enum class Type {
        OPEN, CLOSE, CREATE, DELETE, WRITE, READ, MOVE, RENAME, COPY, MOUNT
    }

    /**
     * This is used to determine if the event is an open event.
     */
    fun isOpen() = type == Type.OPEN

    /**
     * This is used to determine if the event is a close event.
     */
    fun isClose() = type == Type.CLOSE

    /**
     * This is used to determine if the event is a create event.
     */
    fun isCreate() = type == Type.CREATE

    /**
     * This is used to determine if the event is a delete event.
     */
    fun isDelete() = type == Type.DELETE

    /**
     * This is used to determine if the event is a write event.
     */
    fun isWrite() = type == Type.WRITE

    /**
     * This is used to determine if the event is a read event.
     */
    fun isRead() = type == Type.READ

    /**
     * This is used to determine if the event is a copy event.
     */

    fun isCopy() = type == Type.COPY

    /**
     * This is used to determine if the event is a move event.
     */
    fun isMove() = type == Type.MOVE

    /**
     * This is used to determine if the event is a rename event.
     */
    fun isRename() = type == Type.RENAME

    /**
     * This is used to determine if the event is a mount event.
     */
    fun isMount() = type == Type.MOUNT

}

/**
 * This interface is used to listen for events that occur in the VFS
 */
fun interface VFSListener {
    /**
     * Called when an event occurs. This is called on the main thread.
     */
    fun onEvent(event: VFSEvent)
}