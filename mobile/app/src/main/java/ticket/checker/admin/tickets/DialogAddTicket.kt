package ticket.checker.admin.tickets

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ticket.checker.R
import ticket.checker.admin.listeners.ListChangeListener
import ticket.checker.extras.Util
import ticket.checker.models.Ticket
import ticket.checker.services.ServiceManager

class DialogAddTicket : DialogFragment(), View.OnClickListener {
    var listChangeListener: ListChangeListener<Ticket>? = null

    private var btnClose: ImageButton? = null
    private var tvTitle: TextView? = null
    private var etTicketNumber: EditText? = null
    private var etSoldTo: EditText? = null
    private var submitButton: Button? = null
    private var loadingSpinner: ProgressBar? = null
    private var tvResult: TextView? = null

    private val submitCallback: Callback<Ticket> = object : Callback<Ticket> {
        override fun onResponse(call: Call<Ticket>, response: Response<Ticket>) {
            loadingSpinner?.visibility = View.GONE
            submitButton?.visibility = View.VISIBLE
            if (response.isSuccessful) {
                listChangeListener?.onAdd(response.body() as Ticket)
                tvResult?.visibility = View.VISIBLE
                tvResult?.setTextColor(ContextCompat.getColor(context!!, R.color.yesGreen))
                tvResult?.text = "You have created the ticket successfully"
                etTicketNumber?.setText("")
                etTicketNumber?.requestFocus()
                etTicketNumber?.error = null
                etSoldTo?.setText("")
                etSoldTo?.error = null
            } else {
                onErrorResponse(call, response)
            }
        }

        override fun onFailure(call: Call<Ticket>, t: Throwable) {
            loadingSpinner?.visibility = View.GONE
            submitButton?.visibility = View.VISIBLE
            onErrorResponse(call, null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_add_ticket, container, false)
        btnClose = view.findViewById(R.id.btnClose)
        btnClose?.setOnClickListener(this)
        tvTitle = view.findViewById(R.id.tvTitle)
        etTicketNumber = view.findViewById(R.id.etTicketNumber)
        etSoldTo = view.findViewById(R.id.etSoldTo)
        submitButton = view.findViewById(R.id.btnSubmit)
        submitButton?.setOnClickListener(this)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        tvResult = view.findViewById(R.id.tvResult)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)
        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnClose -> {
                dismiss()
            }
            R.id.btnSubmit -> {
                if (validate()) {
                    submitTicket(etTicketNumber?.text.toString(), etSoldTo?.text.toString())
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true
        tvResult?.visibility = View.INVISIBLE
        val ticketNumber = etTicketNumber?.text.toString()
        if (ticketNumber.isEmpty()) {
            etTicketNumber?.error = "This field is required"
            isValid = false
        }

        val soldTo = etSoldTo?.text.toString()
        if(soldTo.isEmpty()) {
            etSoldTo?.error = "This field is required"
            isValid = false
        }

        return isValid
    }

    private fun submitTicket(ticketNumber: String, soldTo: String) {
        tvResult?.visibility = View.INVISIBLE
        submitButton?.visibility = View.GONE
        loadingSpinner?.visibility = View.VISIBLE

        val ticket = Ticket(ticketNumber, soldTo)
        val call = ServiceManager.getTicketService().createTicket(ticket)
        call.enqueue(submitCallback)
    }

    private fun onErrorResponse(call: Call<Ticket>, response: Response<Ticket>?) {
        val wasHandled = Util.treatBasicError(call, response, fragmentManager)
        if (!wasHandled) {
            if (response?.code() == 400) {
                etTicketNumber?.error = "This ticket id already exists!"
            }
        }
    }

}