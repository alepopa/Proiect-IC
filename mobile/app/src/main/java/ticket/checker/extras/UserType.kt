package ticket.checker.extras

import ticket.checker.R
import java.io.Serializable

enum class UserType(val role : String, val colorResource : Int) : Serializable {
    ADMIN("Admin", R.color.yesGreen),
    USER("User", R.color.darkerGrey);

    companion object {
        fun fromRoleToUserType(role : String?) : UserType = when(role) {
            "ROLE_ADMIN" -> ADMIN
            else -> USER
        }

        fun fromUserTypeToRole(userType : UserType) : String = when(userType) {
            ADMIN -> "ROLE_ADMIN"
            USER -> "ROLE_USER"
        }
    }
}