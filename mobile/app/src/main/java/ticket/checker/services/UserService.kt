package ticket.checker.services

import retrofit2.Call
import retrofit2.http.*
import ticket.checker.models.User

interface UserService {

    @GET("/login")
    fun getUser() : Call<User>

}