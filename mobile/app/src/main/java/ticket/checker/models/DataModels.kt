package ticket.checker.models

import ticket.checker.extras.UserType
import java.io.Serializable
import java.util.*

data class User(var userId : Long?, var username : String, var password : String, var name : String, var role : String, var createdAt : Date?, var soldTicketsNo : Int?, var validatedTicketsNo : Int?) : Serializable {
    constructor(username : String, password : String, name : String, userType : UserType) : this(null, username, password, name, UserType.fromUserTypeToRole(userType), null, null, null)

    val userType : UserType
        get() = UserType.fromRoleToUserType(role)
}

data class ErrorResponse(val timestamp : Date, val message : String, val details : String) : Serializable