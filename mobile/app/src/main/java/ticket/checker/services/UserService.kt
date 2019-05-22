package ticket.checker.services

import retrofit2.Call
import retrofit2.http.*
import ticket.checker.models.User

interface UserService {

    @GET("/login")
    fun getUser() : Call<User>

    @GET("/users")
    fun getUsers(@Query("type") type : String?,@Query("value") value : String?, @Query("page") page : Int?, @Query("size") size : Int?) : Call<List<User>>

    @GET("/users/{loggedInUserId}")
    fun getUsersById(@Path("loggedInUserId") userId : Long) : Call<User>

    @POST("/users")
    fun createUser(@Body user: User) : Call<User>

    @POST("/users/{loggedInUserId}")
    fun editUser(@Path("loggedInUserId") userId : Long, @Body user: User) : Call<User>

    @DELETE("/users/{loggedInUserId}")
    fun deleteUserById(@Path("loggedInUserId") userId: Long) : Call<Void>

}