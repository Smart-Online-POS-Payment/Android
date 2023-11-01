import com.example.myapplication.Account
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class VerificationRequest(
    val email: String?,
    val password: String?
)

data class VerificationResponse(
    val isSuccess: Boolean,
    val message: String
)

object AuthService {
    fun verifyAccount(email: String?, password: String?): Account? {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://your-backend-url.com/") // Replace with your actual backend URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val authApi: AuthApi = retrofit.create(AuthApi::class.java)
        var return_account: Account? = null
        val request = VerificationRequest(email, password)
        val call: retrofit2.Call<VerificationResponse> = authApi.verifyAccount(request)
        call.enqueue(object : Callback<VerificationResponse> {
            override fun onResponse(call: retrofit2.Call<VerificationResponse>, response: Response<VerificationResponse>) {
                if (response.isSuccessful) {
                    // Account verification successful
                    val verificationResponse: VerificationResponse? = response.body()
                    if (verificationResponse != null) {
                        return_account = Account(verificationResponse.message) // Assuming Account class has a constructor taking a message
                    }
                } else {
                    // Account verification failed
                    // Handle the error
                }
            }

            override fun onFailure(call: retrofit2.Call<VerificationResponse>, t: Throwable) {
                // Network request failure
                // Handle the error
            }
        })
        return return_account
    }

    // Define your API interface using Retrofit
    interface AuthApi {
        @POST("your_verification_endpoint")
        fun verifyAccount(@Body request: VerificationRequest): retrofit2.Call<VerificationResponse>
    }
}
