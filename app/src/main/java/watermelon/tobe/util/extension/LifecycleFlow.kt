package watermelon.tobe.util.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

/**
 * description ：
 * 1.使用launch是不安全的，在应用在后台时也会接收数据更新，可能会导致应用崩溃
 * 2.使用launchWhenStarted或launchWhenResumed会好一些，在后台时不会接收数据更新，但是，上游数据流会在应用后台运行期间保持活跃，因此可能浪费一定的资源
 * 使用 repeatOnLifecycle 当这个Fragment处于STARTED状态时会开始收集流，并且在RESUMED状态时保持收集，最终在Fragment进入STOPPED状态时结束收集过程。
 * 结合使用repeatOnLifecycle API和WhileSubscribed,可以帮助您的应用妥善利用设备资源的同时，发挥最佳性能
 */
fun LifecycleOwner.safeLaunch(action: suspend () -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            action.invoke()
        }
    }
}

fun LifecycleOwner.safeLaunchAndCatch(action: suspend () -> Unit): Flow<Throwable?> {
    return flow<Throwable?> {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            action.invoke()
        }
    }.shareIn(lifecycleScope, SharingStarted.WhileSubscribed())
}