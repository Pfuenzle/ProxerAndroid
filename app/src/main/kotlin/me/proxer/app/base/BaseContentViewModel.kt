package me.proxer.app.base

import android.app.Application
import io.reactivex.Single
import me.proxer.app.util.extension.buildSingle
import me.proxer.library.api.Endpoint

/**
 * @author Ruben Gees
 */
abstract class BaseContentViewModel<T>(application: Application) : BaseViewModel<T>(application) {

    override val dataSingle: Single<T>
        get() = Single.fromCallable { validate() }
                .flatMap { endpoint.buildSingle() }

    abstract protected val endpoint: Endpoint<T>
}
