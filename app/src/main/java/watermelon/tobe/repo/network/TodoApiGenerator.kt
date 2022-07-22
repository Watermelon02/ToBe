package watermelon.tobe.repo.network

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * description ： 设置用户登录cookie
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/6/20 19:15
 */
object ToDoApiGenerator {
    private var cookies: MutableMap<String, List<Cookie>> = HashMap()
    private lateinit var instance: Retrofit

    fun <T : Any> getApiService(
        clazz: KClass<T>,
    ): T {
        return instance.create(clazz.java)
    }

    private fun OkHttpClient.Builder.defaultOkhttpConfig(context: Context): OkHttpClient {
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.SECONDS)
        /*addInterceptor(HttpLoggingInterceptor(object :HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.d("testTag", "log: $message")
            }
        }).apply { this.level = HttpLoggingInterceptor.Level.BODY })*/
        cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                this@ToDoApiGenerator.cookies[url.host] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookies[url.host]
                return cookies ?: ArrayList()
            }
        })
        return build()
    }

    fun init(context: Context) {
        instance =
            Retrofit.Builder().baseUrl("https://www.wanandroid.com/")
                .client(OkHttpClient().newBuilder().defaultOkhttpConfig(context))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build()
    }
}