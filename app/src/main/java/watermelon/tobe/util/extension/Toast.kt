package watermelon.lightmusic.util.extension

import android.widget.Toast
import watermelon.tobe.base.BaseApp

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/6/22 10:51
 */
fun toast(s: CharSequence) {
    Toast.makeText(BaseApp.appContext, s, Toast.LENGTH_SHORT).show()
}