package ticket.checker.extras

interface BarcodeTypeChangeListener {
    fun onBarcodeTypeChanged(barcodeType : BarcodeType)
}

interface IScanDialogListener {
    fun dismiss()
}