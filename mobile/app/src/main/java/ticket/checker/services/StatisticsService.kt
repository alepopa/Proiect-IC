package ticket.checker.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StatisticsService {

    @GET("/statistics/numbers/users")
    fun getUserNumbers(@Query("type") type : String?, @Query("value") value : String?) : Call<Int>

    @GET("/statistics/numbers/tickets")
    fun getTicketNumbers(@Query("type") type : String?, @Query("value") value : String?) : Call<Int>

}