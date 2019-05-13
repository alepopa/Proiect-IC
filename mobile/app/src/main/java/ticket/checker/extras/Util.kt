package ticket.checker.extras
import android.support.v4.app.FragmentManager
import retrofit2.Call
import retrofit2.Response
import ticket.checker.AppTicketChecker
import ticket.checker.dialogs.DialogInfo
import java.security.MessageDigest

object Util {

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