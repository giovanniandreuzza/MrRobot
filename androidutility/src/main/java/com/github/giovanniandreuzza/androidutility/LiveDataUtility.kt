package com.github.giovanniandreuzza.androidutility

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

fun <T : Any> LiveData<Result<T>>.observe(
    owner: LifecycleOwner,
    onSuccess: ((success: T) -> Unit)? = null,
    onFailure: ((error: Throwable) -> Unit)? = null
) {
    observe(owner, Observer { result ->
        result.apply {
            onSuccess { t ->
                onSuccess?.invoke(t)
            }
            onFailure { error ->
                onFailure?.invoke(error)
            }
        }
    })
}

/****
 * removes the observer once its value has been emitted once
 * using observeForever
 */
fun <T> LiveData<T>.observeForeverOnce(observer: (T?) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(value: T?) {
            removeObserver(this)
            observer(value)
        }
    })
}

/****
 * removes the observer once its value has been emitted once
 * using lifeCycleOwner
 */
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T?) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T?) {
            removeObserver(this)
            observer(value)
        }
    })
}

/**
 * from https://stackoverflow.com/a/57079290
 * combine emission of two different LiveData into one,
 * see example:
 * @sample ```val profile = MutableLiveData<ProfileData>()
    val user = MutableLiveData<CurrentUser>()
    val title = profile.combineWith(user) { profile, user ->
    "${profile.job} ${user.name}"
    }```
*/
fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block.invoke(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block.invoke(this.value, liveData.value)
    }
    return result
}
