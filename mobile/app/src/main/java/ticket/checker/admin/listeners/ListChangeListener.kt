package ticket.checker.admin.listeners

interface ListChangeListener<in T> {
    fun onAdd(addedObject : T)
    fun onEdit(editedObject : T, editedObjectPosition : Int)
    fun onRemove(removedItemPosition : Int)
}