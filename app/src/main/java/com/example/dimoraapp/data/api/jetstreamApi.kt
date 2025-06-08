import com.example.dimoraapp.data.model.NotificationsResponse
import com.example.dimoraapp.data.model.RegisterRequest
import com.example.dimoraapp.data.model.RegisterResponse
import com.example.dimoraapp.data.model.ProfileResponse
import com.example.dimoraapp.model.Advertisement
import com.example.dimoraapp.model.AdvertisementApi
import com.example.dimoraapp.model.AdvertisementListResponse
import com.example.dimoraapp.model.AdvertisementApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface JetstreamApi {

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/user")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    @GET("api/advertisement")
    suspend fun getAdvertisements(@Header("Authorization") token: String): Response<AdvertisementListResponse>

    @GET("api/advertisement/{id}")
    suspend fun getAdvertisementById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<AdvertisementApiResponse>

    @GET("api/all-notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Response<NotificationsResponse>

    // FIXED: Use Response<AdvertisementListResponse> for consistency
    @GET("api/search")
    suspend fun searchAdvertisements(
        @Query("q") query: String,
        @Header("Authorization") token: String
    ): Response<List<AdvertisementApi>>
}