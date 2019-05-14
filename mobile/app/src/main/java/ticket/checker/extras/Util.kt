package ticket.checker.extras
import android.support.v4.app.FragmentManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import ticket.checker.AppTicketChecker
import ticket.checker.dialogs.DialogInfo
import ticket.checker.models.ErrorResponse
import ticket.checker.services.ServiceManager
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

object Util {

    val DATE_FORMAT = SimpleDateFormat("dd MMM yyyy")

    const val ERROR_TICKET_VALIDATION = "TicketValidationException"
    const val ERROR_TICKET_EXISTS = "TicketExistsException"

    class BirthDateFormatException : Exception()
    class BirthDateIncorrectException : Exception()

    fun isTicketFormatValid(ticketNumber : String?) : Boolean {
        var isValid = ticketNumber != null
        if(isValid) {
            if (ticketNumber!!.contains(" ") || ticketNumber.contains("http://") || ticketNumber.contains("https://")) {
                isValid = false
            }
        }
        return isValid
    }

    fun getBirthdateFromText(birthString : String) : Date {
        try {
            val splitBirthString = birthString.split(".")
            var validFormat = true
            if (splitBirthString[0].toInt() !in 1..31) {
                validFormat = false
            }
            if (splitBirthString[1].toInt() !in 1..12) {
                validFormat = false
            }
            if (splitBirthString[2].toInt() !in 1000..2100) {
                validFormat = false
            }

            if (!validFormat) {
                throw throw BirthDateFormatException()
            }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, splitBirthString[0].toInt())
            calendar.set(Calendar.MONTH, splitBirthString[1].toInt() - 1)
            calendar.set(Calendar.YEAR, splitBirthString[2].toInt())
            calendar.set(Calendar.HOUR,0)
            calendar.set(Calendar.MINUTE,0)
            calendar.set(Calendar.SECOND,0)
            calendar.set(Calendar.HOUR_OF_DAY,0)
            if (calendar.after(Date())) {
                throw BirthDateIncorrectException()
            } else {
                return calendar.time
            }
        }
        catch (e : RuntimeException) {
            throw BirthDateFormatException()
        }
    }

    fun convertError(errorBody : ResponseBody?) : ErrorResponse {
        return try {
            ServiceManager.errorConverter!!.convert(errorBody)
        }
        catch (e : Exception) {
            ErrorResponse(Date(),"","")
        }
    }

    fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }

    fun <T> treatBasicError(call : Call<T>, response : Response<T>?, fragmentManager : FragmentManager?) : Boolean {
        var hasResponded = false
        if(response == null) {
            val dialogConnectionError = DialogInfo.newInstance("Connection error", "There was an error connecting to the server", DialogType.ERROR)
            dialogConnectionError.show(fragmentManager, "DIALOG_ERROR")
            hasResponded = true
        }
        else {
            when(response.code()) {
                401 -> {
                    AppTicketChecker.clearSession()
                    val dialogAuthError = DialogInfo.newInstance("Session expired", "You need to provide your authentication once again", DialogType.AUTH_ERROR)
                    dialogAuthError.isCancelable = false
                    dialogAuthError.show(fragmentManager, "DIALOG_SESSION_ERROR")
                    hasResponded = true
                }
                403 -> {
                    val dialogError = DialogInfo.newInstance("Authorization required", "You are not allowed to do this action", DialogType.ERROR)
                    dialogError.show(fragmentManager, "DIALOG_AUTH_ERROR")
                    hasResponded = true
                }
                500 -> {
                    val dialogServerError = DialogInfo.newInstance("Server error", "Ooups, there was a server error", DialogType.ERROR)
                    dialogServerError.show(fragmentManager, "DIALOG_SERVER_ERROR")
                    hasResponded = true
                }
            }
        }
        return hasResponded
    }

}