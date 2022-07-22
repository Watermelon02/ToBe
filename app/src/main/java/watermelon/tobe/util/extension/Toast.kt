package watermelon.tobe.util.extension

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import watermelon.tobe.base.BaseApp

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 20:21
 */
val toastHandler by lazy { Handler(Looper.getMainLooper()) }
fun toast(s: CharSequence) {
    toastHandler.post {
        Toast.makeText(BaseApp.appContext, s, Toast.LENGTH_SHORT).show()
    }
}