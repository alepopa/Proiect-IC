package ticket.checker.admin.listeners

interface EditListener<in T> {
    fun onEdit(editedObject : T)
}