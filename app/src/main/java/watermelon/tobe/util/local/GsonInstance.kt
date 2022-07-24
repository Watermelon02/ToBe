package watermelon.tobe.util.local

import com.google.gson.Gson

object GsonInstance {
    val INSTANCE by lazy { Gson() }
}