package watermelon.lightmusic.base

import android.content.Context
import androidx.lifecycle.ViewModel
import watermelon.tobe.base.BaseApp

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/6/22 10:24
 */
open class BaseViewModel: ViewModel() {
    val appContext: Context
        get() = BaseApp.appContext
}