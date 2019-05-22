package ticket.checker.admin.listeners

interface FilterChangeListener {
    fun onFilterChange(filterType: String?, filterValue : String)
}