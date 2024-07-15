import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

//const val APIKEY = "sk-proj-CSBWzEVbXGDYl31YU9E6T3BlbkFJWty4Qgm5Taj4g3V0mxfY"
const val APIKEY = "sk-None-Ylfe5gOGrxlAZxgKGXC7T3BlbkFJIh0ULhNpBXHqaCkGAGu7"
//const val APIKEY = "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" //fake

interface Api {

    @POST("completions")
    @Headers("Authorization: Bearer $APIKEY", "Content-Type: application/json")
    suspend fun askQuestion(
        @Body question: Question
    ): Response<Answer>

}