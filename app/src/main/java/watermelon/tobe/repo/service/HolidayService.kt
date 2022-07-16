package watermelon.tobe.repo.service

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import watermelon.lightmusic.util.network.ApiGenerator
import watermelon.lightmusic.util.network.ApiGenerator.APP_ID
import watermelon.lightmusic.util.network.ApiGenerator.APP_SECRET
import watermelon.tobe.repo.bean.HolidayResponse
import watermelon.tobe.repo.bean.MonthResponse

/**
 * description ： 节日接口
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/15 22:02
 */
interface HolidayService {
    /**@param date 指定日期的字符串，格式 ‘20180223’*/
    @GET("single/{date}")
    suspend fun queryHoliday(
        @Path("date")date: String,
        @Query("ignoreHoliday") ignoreHoliday: Boolean = false,
        @Query("app_id")appId:String = APP_ID,
        @Query("app_secret")appSecret:String = APP_SECRET
    ): HolidayResponse

    /**@param month 指定月份的字符串，格式 ‘201802’*/
    @GET("list/month/{month}")
    suspend fun queryMonth(@Path("month")month: String, @Query("ignoreHoliday") ignoreHoliday: Boolean = false,
                   @Query("app_id")appId:String = APP_ID,
                   @Query("app_secret")appSecret:String = APP_SECRET):MonthResponse


    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(HolidayService::class) }
    }
}